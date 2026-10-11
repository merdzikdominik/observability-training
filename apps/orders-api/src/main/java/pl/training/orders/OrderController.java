package pl.training.orders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class OrderController {
  private static final Logger log = LoggerFactory.getLogger(OrderController.class);
  private final OrderRepository repo;
  private final RestTemplate http;
  private final String inventoryUrl;
  private final String paymentUrl;

  public OrderController(OrderRepository repo, RestTemplate http,
                         @Value("${services.inventory-url}") String inventoryUrl,
                         @Value("${services.payment-url}") String paymentUrl) {
    this.repo = repo; this.http = http; this.inventoryUrl = inventoryUrl; this.paymentUrl = paymentUrl;
  }

  @GetMapping("/orders")
  public List<OrderView> orders() { return repo.latest(); }

  @GetMapping("/orders/{id}")
  public OrderView order(@PathVariable String id) {
    return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @GetMapping("/catalog/{productId}")
  public Object catalog(@PathVariable String productId) {
    return http.getForObject(inventoryUrl + "/api/inventory/{id}", Object.class, productId);
  }

  @PostMapping("/orders")
  public ResponseEntity<OrderView> create(@RequestBody CreateOrderRequest req) {
    String id = UUID.randomUUID().toString();
    OrderView order = new OrderView(id, req.productId(), req.quantity(), req.amountCents(), req.customerEmail(), "PENDING", OffsetDateTime.now());
    repo.insert(order);
    log.info("Creating order order_id={} product_id={} quantity={} amount_cents={} customer_email={}", id, req.productId(), req.quantity(), req.amountCents(), req.customerEmail());
    try {
      http.postForEntity(inventoryUrl + "/api/inventory/{id}/reserve?quantity={q}", null, Object.class, req.productId(), req.quantity());
      http.postForEntity(paymentUrl + "/api/payments", new PaymentRequest(id, req.amountCents(), req.customerEmail()), Object.class);
      repo.updateStatus(id, "COMPLETED");
      log.info("Order completed order_id={}", id);
      return ResponseEntity.status(HttpStatus.CREATED).body(repo.findById(id).orElseThrow());
    } catch (HttpStatusCodeException ex) {
      repo.updateStatus(id, "FAILED");
      log.error("Order failed order_id={} downstream_status={} body={}", id, ex.getStatusCode().value(), ex.getResponseBodyAsString());
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Downstream service failed");
    }
  }

  @GetMapping("/reports/slow")
  public Map<String,Object> slowReport(@RequestParam(defaultValue = "750") int delayMs) {
    int count = repo.slowReport(delayMs);
    return Map.of("orders", count, "databaseDelayMs", delayMs);
  }

  @GetMapping("/compute")
  public Map<String,Object> compute(@RequestParam(defaultValue = "80000") int iterations) throws Exception {
    int safe = Math.max(1, Math.min(iterations, 2_000_000));
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] data = "observability-training".getBytes(StandardCharsets.UTF_8);
    for (int i=0; i<safe; i++) data = digest.digest(data);
    return Map.of("iterations", safe, "digest", HexFormat.of().formatHex(data));
  }
}

record CreateOrderRequest(String productId, int quantity, int amountCents, String customerEmail) {}
record PaymentRequest(String orderId, int amountCents, String customerEmail) {}
record OrderView(String id, String productId, int quantity, int amountCents, String customerEmail, String status, OffsetDateTime createdAt) {}

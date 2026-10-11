package pl.training.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class PaymentController {
  private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
  private final ChaosSettings chaos;
  public PaymentController(ChaosSettings chaos) { this.chaos = chaos; }

  @PostMapping("/api/payments")
  public Map<String,Object> pay(@RequestBody PaymentRequest req) throws InterruptedException {
    int delay = chaos.maxDelayMs() <= chaos.minDelayMs() ? chaos.minDelayMs() : ThreadLocalRandom.current().nextInt(chaos.minDelayMs(), chaos.maxDelayMs()+1);
    Thread.sleep(delay);
    if (ThreadLocalRandom.current().nextDouble() < chaos.errorRate()) {
      log.error("Payment declined order_id={} amount_cents={} customer_email={} delay_ms={}", req.orderId(), req.amountCents(), req.customerEmail(), delay);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "payment provider error");
    }
    String paymentId = UUID.randomUUID().toString();
    log.info("Payment accepted payment_id={} order_id={} amount_cents={} customer_email={} delay_ms={}", paymentId, req.orderId(), req.amountCents(), req.customerEmail(), delay);
    return Map.of("paymentId", paymentId, "status", "ACCEPTED", "delayMs", delay);
  }

  @GetMapping("/admin/chaos")
  public Map<String,Object> current() { return Map.of("errorRate", chaos.errorRate(), "minDelayMs", chaos.minDelayMs(), "maxDelayMs", chaos.maxDelayMs()); }

  @PostMapping("/admin/chaos")
  public Map<String,Object> update(@RequestParam(required=false) Double errorRate,
                                   @RequestParam(required=false) Integer minDelayMs,
                                   @RequestParam(required=false) Integer maxDelayMs) {
    chaos.update(errorRate, minDelayMs, maxDelayMs); return current();
  }
}

record PaymentRequest(String orderId, int amountCents, String customerEmail) {}

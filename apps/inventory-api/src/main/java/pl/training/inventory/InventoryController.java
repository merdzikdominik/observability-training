package pl.training.inventory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class InventoryController {
  private static final Logger log = LoggerFactory.getLogger(InventoryController.class);
  private final ChaosSettings chaos;
  public InventoryController(ChaosSettings chaos) { this.chaos = chaos; }

  @GetMapping("/api/inventory/{productId}")
  public Map<String,Object> inventory(@PathVariable String productId) throws InterruptedException {
    maybeChaos(productId);
    int available = 20 + Math.floorMod(productId.hashCode(), 180);
    return Map.of("productId", productId, "available", available);
  }

  @PostMapping("/api/inventory/{productId}/reserve")
  public Map<String,Object> reserve(@PathVariable String productId, @RequestParam int quantity) throws InterruptedException {
    maybeChaos(productId);
    if (productId.startsWith("out-") || quantity > 20) {
      log.warn("Insufficient stock product_id={} quantity={}", productId, quantity);
      throw new ResponseStatusException(HttpStatus.CONFLICT, "insufficient stock");
    }
    log.info("Inventory reserved product_id={} quantity={}", productId, quantity);
    return Map.of("productId", productId, "reserved", quantity);
  }

  @GetMapping("/admin/chaos")
  public Map<String,Object> current() { return Map.of("errorRate", chaos.errorRate(), "slowRate", chaos.slowRate(), "slowMs", chaos.slowMs()); }

  @PostMapping("/admin/chaos")
  public Map<String,Object> update(@RequestParam(required=false) Double errorRate,
                                   @RequestParam(required=false) Double slowRate,
                                   @RequestParam(required=false) Integer slowMs) {
    chaos.update(errorRate, slowRate, slowMs); return current();
  }

  private void maybeChaos(String productId) throws InterruptedException {
    double r = ThreadLocalRandom.current().nextDouble();
    if (r < chaos.errorRate()) {
      log.error("Inventory backend failure product_id={}", productId);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "inventory backend failure");
    }
    if (r < chaos.errorRate() + chaos.slowRate()) {
      log.warn("Slow inventory lookup product_id={} delay_ms={}", productId, chaos.slowMs());
      Thread.sleep(chaos.slowMs());
    } else {
      Thread.sleep(ThreadLocalRandom.current().nextInt(10, 55));
    }
  }
}

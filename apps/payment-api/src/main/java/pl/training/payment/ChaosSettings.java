package pl.training.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ChaosSettings {
  private volatile double errorRate;
  private volatile int minDelayMs;
  private volatile int maxDelayMs;

  public ChaosSettings(@Value("${chaos.error-rate:0.03}") double errorRate,
                       @Value("${chaos.min-delay-ms:35}") int minDelayMs,
                       @Value("${chaos.max-delay-ms:220}") int maxDelayMs) {
    this.errorRate = errorRate; this.minDelayMs = minDelayMs; this.maxDelayMs = maxDelayMs;
  }
  public double errorRate(){ return errorRate; }
  public int minDelayMs(){ return minDelayMs; }
  public int maxDelayMs(){ return maxDelayMs; }
  public void update(Double e, Integer min, Integer max) {
    if (e != null) errorRate = Math.max(0.0, Math.min(1.0, e));
    if (min != null) minDelayMs = Math.max(0, Math.min(min, 10000));
    if (max != null) maxDelayMs = Math.max(minDelayMs, Math.min(max, 15000));
  }
}

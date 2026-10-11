package pl.training.inventory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ChaosSettings {
  private volatile double errorRate;
  private volatile double slowRate;
  private volatile int slowMs;

  public ChaosSettings(@Value("${chaos.error-rate:0.01}") double errorRate,
                       @Value("${chaos.slow-rate:0.05}") double slowRate,
                       @Value("${chaos.slow-ms:900}") int slowMs) {
    this.errorRate = errorRate; this.slowRate = slowRate; this.slowMs = slowMs;
  }
  public double errorRate(){ return errorRate; }
  public double slowRate(){ return slowRate; }
  public int slowMs(){ return slowMs; }
  public void update(Double e, Double s, Integer m) {
    if (e != null) errorRate = clamp(e); if (s != null) slowRate = clamp(s); if (m != null) slowMs = Math.max(0, Math.min(m, 10000));
  }
  private double clamp(double v){ return Math.max(0.0, Math.min(1.0, v)); }
}

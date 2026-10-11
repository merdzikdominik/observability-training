package pl.training.orders;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository {
  private final JdbcTemplate jdbc;
  public OrderRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  public void insert(OrderView o) {
    jdbc.update("insert into orders(id, product_id, quantity, amount_cents, customer_email, status, created_at) values (?,?,?,?,?,?,?)",
        o.id(), o.productId(), o.quantity(), o.amountCents(), o.customerEmail(), o.status(), o.createdAt());
  }

  public void updateStatus(String id, String status) {
    jdbc.update("update orders set status=? where id=?", status, id);
  }

  public Optional<OrderView> findById(String id) {
    var list = jdbc.query("select * from orders where id=?", this::map, id);
    return list.stream().findFirst();
  }

  public List<OrderView> latest() {
    return jdbc.query("select * from orders order by created_at desc limit 50", this::map);
  }

  public int slowReport(int delayMs) {
    double seconds = Math.max(0, Math.min(delayMs, 5000)) / 1000.0;
    return jdbc.queryForObject("select pg_sleep(?), count(*) from orders", (rs, rowNum) -> rs.getInt(2), seconds);
  }

  private OrderView map(ResultSet rs, int rowNum) throws SQLException {
    return new OrderView(
        rs.getString("id"), rs.getString("product_id"), rs.getInt("quantity"), rs.getInt("amount_cents"),
        rs.getString("customer_email"), rs.getString("status"), rs.getObject("created_at", OffsetDateTime.class));
  }
}

create table if not exists orders (
  id varchar(64) primary key,
  product_id varchar(64) not null,
  quantity integer not null,
  amount_cents integer not null,
  customer_email varchar(255),
  status varchar(32) not null,
  created_at timestamptz not null
);
create index if not exists idx_orders_created_at on orders(created_at desc);

import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = { vus: 5, duration: '3m' };

export default function () {
  const product = `sku-${(__VU % 7) + 1}`;
  http.get(`http://orders-api:8080/api/catalog/${product}`);
  const payload = JSON.stringify({
    productId: product,
    quantity: 1,
    amountCents: 1999 + (__VU * 10),
    customerEmail: `student-${__VU}@example.com`,
  });
  const r = http.post('http://orders-api:8080/api/orders', payload, { headers: { 'Content-Type': 'application/json' } });
  check(r, { 'order accepted or downstream failure': (x) => [201, 502].includes(x.status) });
  sleep(0.6);
}

import http from 'k6/http';
import { sleep } from 'k6';
export const options = { vus: 25, duration: '3m' };
export default function () {
  const product = `sku-${(__ITER % 12) + 1}`;
  http.get(`http://orders-api:8080/api/catalog/${product}`);
  if (__ITER % 3 === 0) {
    http.post('http://orders-api:8080/api/orders', JSON.stringify({productId: product, quantity: 1, amountCents: 2599, customerEmail: `load-${__VU}@example.com`}), {headers:{'Content-Type':'application/json'}});
  }
  if (__ITER % 11 === 0) http.get('http://orders-api:8080/api/compute?iterations=250000');
  sleep(0.15);
}

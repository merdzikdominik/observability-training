from http.server import BaseHTTPRequestHandler, HTTPServer
import random

USERS = [f"user-{i:04d}" for i in range(1200)]

class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path != '/metrics':
            self.send_response(404); self.end_headers(); return
        lines = [
            '# HELP training_requests_total Deliberately high-cardinality metric for the lab.',
            '# TYPE training_requests_total counter',
        ]
        for i, user in enumerate(USERS):
            value = 100 + i + random.randint(0, 5)
            lines.append(f'training_requests_total{{service="cardinality-demo",user_id="{user}",method="GET"}} {value}')
        lines += [
            '# HELP training_queue_depth Example gauge.',
            '# TYPE training_queue_depth gauge',
            f'training_queue_depth{{service="cardinality-demo"}} {random.randint(2, 20)}',
        ]
        data = ('\n'.join(lines) + '\n').encode()
        self.send_response(200)
        self.send_header('Content-Type', 'text/plain; version=0.0.4')
        self.send_header('Content-Length', str(len(data)))
        self.end_headers(); self.wfile.write(data)
    def log_message(self, format, *args): return

HTTPServer(('0.0.0.0', 9101), Handler).serve_forever()

from http.server import BaseHTTPRequestHandler, HTTPServer
import json

class Handler(BaseHTTPRequestHandler):
    def do_POST(self):
        n = int(self.headers.get('content-length', '0'))
        body = self.rfile.read(n).decode('utf-8', errors='replace')
        print("\n=== ALERTMANAGER WEBHOOK ===", flush=True)
        try:
            print(json.dumps(json.loads(body), indent=2), flush=True)
        except Exception:
            print(body, flush=True)
        self.send_response(200)
        self.end_headers()
        self.wfile.write(b'ok')
    def log_message(self, format, *args):
        return

HTTPServer(('0.0.0.0', 5001), Handler).serve_forever()

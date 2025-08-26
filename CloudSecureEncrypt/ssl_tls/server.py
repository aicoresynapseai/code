# This is a simple Flask web server configured to use SSL/TLS
# with self-signed certificates. It demonstrates encryption in transit.

import ssl
from flask import Flask, request, jsonify

app = Flask(__name__)

# --- Configuration ---
# Paths to your self-signed certificate and private key
CERT_FILE = "server.crt"
KEY_FILE = "server.key"
HOST = "localhost"
PORT = 8443

@app.route('/')
def hello():
    """A simple endpoint to show the server is running."""
    return "Hello from the Secure Server! Try /secure-data"

@app.route('/secure-data', methods=['GET', 'POST'])
def secure_data():
    """
    An endpoint that demonstrates data exchange over SSL/TLS.
    It receives a message and sends back a response.
    """
    if request.method == 'POST':
        data = request.json
        message = data.get('message', 'No message provided')
        print(f"Received secure message: '{message}'")
        response_message = f"Server received your message securely: '{message}'"
        return jsonify({"status": "success", "response": response_message})
    else:
        return jsonify({"status": "info", "message": "This is a secure endpoint. Send a POST request with JSON data."})

if __name__ == '__main__':
    # Create an SSL context
    # PROTOCOL_TLS_SERVER: Use the highest available TLS version that acts as a server
    # SSLContext provides a way to manage various SSL/TLS options.
    context = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)
    
    try:
        # Load the certificate and private key files
        context.load_cert_chain(CERT_FILE)
        context.load_private_key(KEY_FILE)
        print(f"SSL/TLS context loaded with {CERT_FILE} and {KEY_FILE}")
    except FileNotFoundError:
        print(f"Error: Certificate or key file not found. Please run 'bash generate_certs.sh' first.")
        exit(1)
    except ssl.SSLError as e:
        print(f"Error loading SSL/TLS certificates: {e}")
        exit(1)

    print(f"Server starting on https://{HOST}:{PORT}")
    print("Press Ctrl+C to exit.")
    
    try:
        # Run the Flask app with the SSL context
        # ssl_context parameter tells Flask to use HTTPS with our certificates
        app.run(host=HOST, port=PORT, ssl_context=context, debug=False)
    except Exception as e:
        print(f"An error occurred while running the server: {e}")
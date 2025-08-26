# This script is a Python client that connects to the SSL/TLS secured Flask server.
# It demonstrates how a client can establish a secure connection and exchange data.

import requests
import ssl
import os

# --- Configuration ---
SERVER_HOST = "localhost"
SERVER_PORT = 8443
SERVER_URL = f"https://{SERVER_HOST}:{SERVER_PORT}/secure-data"

# Path to the server's certificate for client-side validation.
# For self-signed certificates, the client needs to explicitly trust it.
# In a real-world scenario, this would be a CA bundle.
CERT_FILE = "server.crt"

def send_secure_request():
    """
    Sends a POST request to the secure server and prints the response.
    It explicitly trusts the self-signed certificate.
    """
    if not os.path.exists(CERT_FILE):
        print(f"Error: Server certificate '{CERT_FILE}' not found.")
        print("Please ensure 'bash generate_certs.sh' has been run and the server certificate exists.")
        return

    data_to_send = {"message": "Hello secure server, this is my secret data!"}

    print(f"\nAttempting to connect to {SERVER_URL}...")
    try:
        # Send a POST request with JSON data
        # verify=CERT_FILE: Tells requests to verify the server's certificate against our self-signed cert.
        # This is how a client "trusts" a self-signed certificate for demonstration.
        # In production, `verify=True` would use system-wide trusted CAs.
        response = requests.post(SERVER_URL, json=data_to_send, verify=CERT_FILE)

        response.raise_for_status() # Raise an exception for HTTP errors (4xx or 5xx)

        print("\n--- Server Response (Secure) ---")
        print(f"Status Code: {response.status_code}")
        print(f"Response JSON: {response.json()}")

        # You can inspect the connection details (though `requests` abstracts a lot)
        # For a deeper dive into SSL/TLS, one would use `http.client` or `ssl` module directly.
        print("\n--- Connection Info ---")
        print(f"Is connection secure (HTTPS)? {'Yes' if response.url.startswith('https') else 'No'}")
        
    except requests.exceptions.SSLError as e:
        print(f"\nSSL/TLS Handshake Error: {e}")
        print("This often happens if the client doesn't trust the server's certificate.")
        print(f"Ensure '{CERT_FILE}' is the correct server certificate and is passed to 'verify='.")
        print("If you're using system-wide trusted certificates, ensure the server's cert is signed by a trusted CA.")
    except requests.exceptions.ConnectionError as e:
        print(f"\nConnection Error: {e}")
        print(f"Ensure the server is running on https://{SERVER_HOST}:{SERVER_PORT}.")
        print("Check if the server's IP/hostname is correct and firewall rules allow connection.")
    except requests.exceptions.RequestException as e:
        print(f"\nAn error occurred during the request: {e}")
    except Exception as e:
        print(f"\nAn unexpected error occurred: {e}")

if __name__ == '__main__':
    print("--- SSL/TLS Client Demo ---")
    send_secure_request()
    print("\nClient finished. Data was sent and received over an encrypted TLS connection.")
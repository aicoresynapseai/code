# This script uses OpenSSL to generate a self-signed SSL/TLS certificate
# and private key for demonstration purposes.
#
# WARNING: Self-signed certificates are NOT suitable for production
# environments as they are not trusted by default by browsers/clients.
# Use certificates from a trusted Certificate Authority (CA) in production.

echo "--- Generating Self-Signed SSL/TLS Certificates ---"

# 1. Generate a Private Key
#   - newkey rsa:2048: Generates a new RSA private key with 2048 bits.
#   - nodes: No DES encryption (no passphrase for the key, easier for demo).
#   - out server.key: Output the private key to 'server.key'.
echo "Generating server private key (server.key)..."
openssl genrsa -out server.key 2048

# 2. Generate a Certificate Signing Request (CSR)
#   - new: Create a new certificate request.
#   - key server.key: Use the private key generated above.
#   - out server.csr: Output the CSR to 'server.csr'.
#   - subj: Provide subject information non-interactively.
#     - C=Country, ST=State, L=Locality, O=Organization, OU=Organizational Unit, CN=Common Name
#     - CN (Common Name) should ideally match the hostname your server will use (e.g., localhost).
echo "Generating Certificate Signing Request (server.csr)..."
openssl req -new -key server.key -out server.csr -subj "/C=US/ST=CA/L=Anytown/O=CloudSecureEncrypt/OU=Dev/CN=localhost"

# 3. Generate a Self-Signed Certificate from the CSR
#   - x509: Output a self-signed certificate instead of a certificate request.
#   - req: Input is a certificate request.
#   - in server.csr: Use the CSR generated above.
#   - signkey server.key: Sign the certificate with the private key.
#   - out server.crt: Output the certificate to 'server.crt'.
#   - days 365: The certificate will be valid for 365 days.
echo "Generating self-signed certificate (server.crt)..."
openssl x509 -req -in server.csr -signkey server.key -out server.crt -days 365

echo "--- Certificates Generated Successfully ---"
echo "You now have:"
echo "  server.key - The private key (KEEP THIS SECURE!)"
echo "  server.crt - The self-signed certificate"
echo "  server.csr - The Certificate Signing Request (can be deleted after cert generation)"

# Optionally, remove the CSR as it's no longer needed for a self-signed cert
rm server.csr
echo "Cleaned up server.csr"

echo "These files are located in the current directory (CloudSecureEncrypt/ssl_tls/)."
echo "You can now run 'python server.py' to start the SSL/TLS server."
echo "Then, run 'python client.py' in a separate terminal to connect."
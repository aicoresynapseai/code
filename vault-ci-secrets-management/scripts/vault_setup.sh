#!/bin/bash
set -e # Exit immediately if a command exits with a non-zero status

echo "Waiting for Vault to start..."
# Loop until Vault is reachable
until curl -s http://localhost:8200/v1/sys/health | grep -q '{"initialized":true,"sealed":false,"standby":false,"performance_standby":false,"replication_perf_standby":false,"server_time_utc":'; do
  echo "Vault not yet ready. Retrying in 2 seconds..."
  sleep 2
done
echo "Vault is up and running!"

# Set Vault address
export VAULT_ADDR='http://localhost:8200'

# In dev mode, Vault is already initialized and unsealed.
# We need to get the root token from the logs if not set via VAULT_DEV_ROOT_TOKEN_ID.
# For simplicity in dev mode, Vault automatically provides a root token on startup.
# We'll use the default dev root token which is printed on startup if not explicitly set.
# Assuming the default token (or you might retrieve it from docker logs).
# For production, you'd use `vault operator init` and `vault operator unseal`.
echo "Logging into Vault using the initial root token (from dev mode)..."
# In dev mode, the root token is usually printed in the Docker logs.
# For this script, we assume a default token or fetch it from logs (more complex).
# For simplicity of this example with VAULT_DEV_MODE="true", the vault CLI can directly authenticate.
# The default root token for dev mode is 's.gP8X7...'. In a real scenario, this is dynamically generated.
# For a practical script against a 'dev' server, you might retrieve it from `docker logs <container_name>`
# Or, if `VAULT_DEV_ROOT_TOKEN_ID` was set, use that.
# Let's just log in directly as dev mode usually allows some initial access.

# Check if a VAULT_TOKEN is already set or retrieve it if possible from dev server
# A more robust approach for dev server:
VAULT_CONTAINER_ID=$(docker-compose ps -q vault)
if [ -z "$VAULT_CONTAINER_ID" ]; then
    echo "Vault container not found. Ensure docker-compose up -d has been run."
    exit 1
fi
echo "Fetching Vault root token from container logs..."
ROOT_TOKEN=$(docker logs $VAULT_CONTAINER_ID 2>&1 | grep 'Root Token:' | awk '{print $NF}' | head -n 1)

if [ -z "$ROOT_TOKEN" ]; then
    echo "Could not find Vault root token in logs. Please inspect your Vault container logs manually."
    echo "You might need to adjust this script if your dev Vault startup differs."
    exit 1
fi

export VAULT_TOKEN=$ROOT_TOKEN
echo "VAULT_TOKEN set for configuration."


# 1. Enable KV Secrets Engine v2
echo "Enabling KV Secrets Engine at path 'kv-v2'..."
vault secrets enable -path=kv-v2 kv-v2

# 2. Write a secret
echo "Writing secret 'my-app/db-creds'..."
vault kv put kv-v2/my-app/db-creds username=devuser password=devpassword

# 3. Enable OIDC Auth Method
echo "Enabling OIDC Auth Method at path 'oidc'..."
vault auth enable oidc

# 4. Configure OIDC for GitHub Actions
# oidc_discovery_url: GitHub Actions OIDC provider
# default_role: The default role to use if not specified by the client
echo "Configuring OIDC for GitHub Actions..."
vault write auth/oidc/config \
    oidc_discovery_url="https://token.actions.githubusercontent.com" \
    default_role="github-actions"

# 5. Define a Vault Policy for 'my-app'
echo "Creating Vault policy 'my-app-policy'..."
# This policy grants read access to the specific secret.
vault policy write my-app-policy - <<EOF
path "kv-v2/data/my-app/db-creds" {
  capabilities = ["read"]
}
EOF

# 6. Create an OIDC Role for GitHub Actions
# This role maps incoming OIDC JWTs to a Vault policy.
# bound_audiences: Must match the `jwt_audience` in your GitHub Actions workflow.
# bound_claims: Use claims from the JWT to restrict access (e.g., specific repo, ref).
# policies: The Vault policy/policies to assign to the authenticated entity.
# ttl: The maximum time the issued Vault token will be valid.
echo "Creating OIDC role 'github-actions'..."
vault write auth/oidc/role/github-actions \
    bound_audiences="my_vault_audience" \
    allowed_redirect_uris="http://localhost:8200/ui/vault/auth/oidc/callback" \
    # The 'allowed_redirect_uris' is typically for UI logins. For programmatic access like GH Actions, it's often not strictly needed
    # for the direct JWT exchange, but sometimes required by the OIDC provider's configuration.
    # For GitHub Actions, the OIDC flow is direct JWT exchange, not a redirect.
    # Let's keep it simple and focus on claims.
    bound_claims_type="glob" \
    # Example bound claims for your GitHub repo
    # This assumes your GitHub repo is `your-org/your-repo-name`
    # Replace `your-username/your-repo` with your actual GitHub username/organization and repository name
    # Example: if your repo is `my-github-org/vault-ci-secrets-management`
    # bound_claims='{"repository": "my-github-org/vault-ci-secrets-management"}' \
    policies="my-app-policy" \
    token_ttl="5m" \
    token_max_ttl="10m"

echo "Vault setup complete!"
echo "---------------------------------------------------------"
echo "Vault Address: $VAULT_ADDR"
echo "Root Token: $VAULT_TOKEN (Use carefully, for initial setup only)"
echo ""
echo "Secrets have been written to kv-v2/my-app/db-creds."
echo "OIDC auth method 'oidc' enabled and configured."
echo "Policy 'my-app-policy' created."
echo "OIDC role 'github-actions' created with 'my_vault_audience' and 'my-app-policy'."
echo "---------------------------------------------------------"
echo "To test the setup manually (e.g., list secrets):"
echo "export VAULT_ADDR='http://localhost:8200'"
echo "export VAULT_TOKEN=$VAULT_TOKEN"
echo "vault kv get kv-v2/my-app/db-creds"
echo ""
echo "Remember to update your GitHub Actions workflow (.github/workflows/ci.yml)"
echo "to use 'VAULT_ADDR' pointing to your Vault server and 'jwt_audience: my_vault_audience'."
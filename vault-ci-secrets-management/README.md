Securing CI/CD Pipelines with Vault for Secrets Management

Welcome to the `vault-ci-secrets-management` project! This repository demonstrates a robust method for managing sensitive environment variables in CI/CD pipelines using HashiCorp Vault. Hardcoding secrets directly into source code or CI/CD scripts is a major security vulnerability. Vault provides a secure, centralized system for storing, accessing, and managing secrets.

The core idea is that your CI/CD pipeline (in this example, GitHub Actions) will authenticate with Vault, retrieve only the necessary secrets, and then pass them as environment variables to your application during the build or deployment phase, without ever exposing them directly in logs or code.

Project Overview:
This example focuses on integrating HashiCorp Vault with GitHub Actions using Vault's OIDC (OpenID Connect) authentication method. OIDC is highly recommended for cloud-native CI/CD environments as it allows your CI/CD pipeline to authenticate to Vault without needing static credentials (like AppRole IDs and Secret IDs), relying instead on the CI/CD provider's inherent identity.

Architecture Flow:
1.  **Vault Setup**: A local Vault server is configured with a KV (Key-Value) secrets engine to store application secrets (e.g., database credentials). An OIDC authentication method is enabled and configured to trust GitHub Actions. A policy is created that grants read access to specific secrets. An OIDC role binds GitHub Action identity attributes (like repository or ref) to this policy.
2.  **GitHub Actions Authentication**: When a GitHub Actions workflow runs, it requests a JWT (JSON Web Token) from GitHub's OIDC provider.
3.  **Vault Authentication**: The GitHub Action workflow uses the `hashicorp/vault-action` to present this JWT to Vault's OIDC authentication endpoint.
4.  **Policy Enforcement**: Vault validates the JWT, and based on the configured OIDC role and associated policies, issues a temporary Vault token with specific permissions.
5.  **Secret Retrieval**: Using this temporary Vault token, the `vault-action` retrieves the requested secrets from the KV secrets engine.
6.  **Environment Variables**: The retrieved secrets are then injected as environment variables into the GitHub Actions job, making them available to subsequent steps (e.g., your application's build or test script).
7.  **Application Execution**: Your application script uses these environment variables, never directly interacting with Vault itself.

Project Structure:
-   `README.md`: This file, providing an overview and instructions.
-   `docker-compose.yml`: Defines a Docker container for running a local HashiCorp Vault server in dev mode for easy setup.
-   `scripts/vault_setup.sh`: A shell script to initialize and configure the local Vault instance. This includes enabling the KV secrets engine, writing a sample secret, enabling the OIDC auth method, configuring it for GitHub Actions, and defining a policy and role.
-   `app/main.py`: A simple Python script representing your application code. It's designed to read secrets from environment variables, simulating how a real application would consume them.
-   `.github/workflows/ci.yml`: The GitHub Actions workflow definition. This file demonstrates how to use `hashicorp/vault-action` to authenticate with Vault using OIDC and retrieve secrets.

Prerequisites:
Before you begin, ensure you have the following installed:
-   Docker and Docker Compose
-   Git
-   HashiCorp Vault CLI (optional, but useful for manual inspection)

Setup and Execution Steps:

Step 1: Start the Local Vault Server
Navigate to the root of this project in your terminal and start the Vault server using Docker Compose:

docker-compose up -d

This will pull the Vault Docker image and start a Vault server listening on http://localhost:8200.

Step 2: Configure Vault
Now, configure the Vault instance by running the setup script. This script will:
-   Initialize Vault (in dev mode, it's already unsealed and a root token is available).
-   Enable the KV secrets engine.
-   Write a sample secret (db-creds).
-   Enable the OIDC authentication method.
-   Configure OIDC to trust GitHub Actions' OIDC provider.
-   Create a Vault policy (`my-app-policy`) allowing read access to the secret.
-   Create an OIDC role (`my-app-role`) that binds specific GitHub Action identities (repository, ref) to the `my-app-policy`. Note the `bound_audiences` value, which must match the `jwt_audience` in your GitHub Action.

scripts/vault_setup.sh

Wait for the script to complete. It will output information about the Vault setup.

Step 3: Update GitHub Actions Workflow (if needed)
The `.github/workflows/ci.yml` file is pre-configured. Ensure the `jwt_audience` in the workflow matches the `bound_audiences` you configured in the `vault_setup.sh` script (by default, it's `my_vault_audience`).
The `VAULT_ADDR` in `ci.yml` is set to `http://<your-vault-public-ip-or-dns>:8200` which implies your Vault server needs to be publicly accessible for GitHub Actions to reach it. For this local demonstration, the GitHub Action will *simulate* access. In a real scenario, you'd either have a publicly hosted Vault or use a self-hosted runner.

Step 4: Trigger the GitHub Actions Pipeline
Push this project to a GitHub repository. Once pushed, navigate to the "Actions" tab in your GitHub repository. The workflow defined in `.github/workflows/ci.yml` will automatically trigger on a push to the `main` branch.

Observe the GitHub Actions run:
-   You will see a job named "Build and Deploy".
-   Within this job, the "Login to Vault and Retrieve Secrets" step will use `hashicorp/vault-action` to authenticate with Vault via OIDC and fetch `db-creds`.
-   The "Run Application" step will then execute `app/main.py`. You will see output showing that the `DB_USERNAME` and `DB_PASSWORD` were correctly passed as environment variables, demonstrating secure secret retrieval.

Key Takeaways:
-   **No Hardcoded Secrets**: Secrets are never stored directly in your repository.
-   **Vault as Central Secret Store**: Vault becomes the single source of truth for all secrets.
-   **Just-In-Time Access**: CI/CD pipelines get secrets only when needed, for the duration of the job.
-   **OIDC for Secure Authentication**: Leverage your CI/CD provider's identity to authenticate with Vault, eliminating static credentials in the pipeline.
-   **Least Privilege**: Vault policies ensure that the CI/CD pipeline only has access to the specific secrets it needs for a given task.

Cleanup (Optional):
To stop and remove the local Vault container:

docker-compose down -v

This cleans up the Docker container and its associated volume, removing all Vault data.
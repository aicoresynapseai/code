Automating Kubernetes Deployments with GitHub Actions

This project demonstrates a robust CI/CD pipeline using GitHub Actions to automate the process of building Docker images, pushing them to Docker Hub, and deploying them to a Kubernetes cluster.

## Project Overview

The pipeline automates the following steps:
1.  **Code Commit:** Triggered on pushes to the `main` branch.
2.  **Docker Image Build:** Builds a Docker image for a simple Node.js application.
3.  **Docker Hub Push:** Pushes the built image to your Docker Hub repository.
4.  **Kubernetes Deployment:** Updates the application deployment on a Kubernetes cluster.

## Prerequisites

Before you begin, ensure you have the following:

*   A GitHub account and a new repository for this project.
*   A Docker Hub account.
*   A running Kubernetes cluster (e.g., Minikube, Kind, GKE, EKS, AKS).
*   kubectl installed and configured to connect to your cluster.
*   Node.js and npm installed (for local testing of the app).

## Project Structure

This repository contains the following key files:

*   `app/`: Contains the source code for a simple Node.js Express application.
    *   `app/index.js`: The main application file.
    *   `app/package.json`: Node.js project dependencies.
    *   `app/Dockerfile`: Defines how to build the Docker image for the application.
*   `k8s/`: Contains the Kubernetes manifest files.
    *   `k8s/deployment.yaml`: Defines the Kubernetes Deployment for the application.
    *   `k8s/service.yaml`: Defines the Kubernetes Service to expose the application.
*   `.github/workflows/main.yml`: The GitHub Actions workflow definition file that orchestrates the CI/CD process.

## Setup Instructions

Follow these steps to set up and run the pipeline:

### 1. Create GitHub Repository and Upload Files

*   Create a new empty GitHub repository (e.g., `k8s-ci-cd-ghactions-demo`).
*   Clone this repository to your local machine.
*   Copy all the provided files into your local repository.
*   Commit and push all the files to your GitHub repository.

### 2. Configure GitHub Secrets

Navigate to your GitHub repository's Settings > Secrets and variables > Actions. Add the following repository secrets:

*   `DOCKER_USERNAME`: Your Docker Hub username.
*   `DOCKER_PASSWORD`: Your Docker Hub Personal Access Token (PAT). Generate one in Docker Hub (Account Settings > Security > New Access Token).
*   `KUBE_CONFIG_DATA`: The base64 encoded content of your Kubernetes Kubeconfig file.
    *   To get this, run: `kubectl config view --raw | base64 -w 0` (on Linux/macOS) or `kubectl config view --raw | certutil -encode - | findstr /v /c:-` (on Windows CMD) or `[System.Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes((kubectl config view --raw)))` (on PowerShell).
*   `DOCKER_IMAGE_NAME`: Your Docker Hub username followed by the image name, e.g., `your-docker-username/k8s-node-app`.
*   `K8S_NAMESPACE`: (Optional, but recommended) The Kubernetes namespace where you want to deploy your application, e.g., `default` or `my-app-ns`. If not set, it will deploy to the namespace defined in your `KUBE_CONFIG_DATA`'s context, usually `default`.

### 3. Adjust Kubernetes Manifests (Optional)

*   Open `k8s/deployment.yaml`.
*   Replace `your-docker-username/k8s-node-app:LATEST_IMAGE_TAG` with your actual Docker Hub image name. The `LATEST_IMAGE_TAG` part will be replaced by the GitHub Action. Ensure the `image:` line reflects your full image name correctly.

### 4. Trigger the Workflow

Once all files are committed and secrets are configured, any push to the `main` branch will automatically trigger the GitHub Actions workflow.

You can also manually trigger the workflow:
*   Go to the "Actions" tab in your GitHub repository.
*   Select the "CI/CD Pipeline" workflow.
*   Click "Run workflow" and then "Run workflow" again.

## Verifying the Deployment

After the workflow completes successfully:

1.  Check the GitHub Actions logs for the "Deploy to Kubernetes" step to ensure `kubectl apply` ran successfully.
2.  On your Kubernetes cluster, run:
    *   `kubectl get deployments -n <your-k8s-namespace>`
    *   `kubectl get services -n <your-k8s-namespace>`
    *   `kubectl get pods -n <your-k8s-namespace>`
    *   Replace `<your-k8s-namespace>` with the value you set for `K8S_NAMESPACE`.

3.  To access the application:
    *   If you're using Minikube/Kind, you might use `minikube service k8s-node-app-service -n <your-k8s-namespace>`.
    *   For cloud providers, check the `EXTERNAL-IP` of your service (`kubectl get service k8s-node-app-service -n <your-k8s-namespace>`). It might take a few moments for an external IP to be provisioned.

## Troubleshooting

*   **GitHub Secrets:** Double-check that all secrets are correctly named and have the right values. A common error is incorrect `KUBE_CONFIG_DATA` encoding.
*   **Docker Hub Login:** Ensure your Docker Hub username and PAT are correct and the PAT has "Read, Write, Delete" permissions for repositories.
*   **Kubernetes Permissions:** The Kubeconfig used for `KUBE_CONFIG_DATA` must have sufficient permissions to create/update Deployments and Services in the target namespace.
*   **Image Pull Issues:** If pods are stuck in `ImagePullBackOff`, ensure the image name in `k8s/deployment.yaml` matches what's pushed to Docker Hub and that Docker Hub credentials are correct.
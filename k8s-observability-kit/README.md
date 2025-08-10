Monitoring Microservices with Prometheus and Grafana on Kubernetes

This project provides a comprehensive example of how to set up robust monitoring for microservices running on Kubernetes using Prometheus for metric collection and Grafana for visualization. It includes sample Go microservices that expose Prometheus metrics, Helm charts for deploying Prometheus and Grafana, and pre-configured YAMLs to tie everything together.

The goal is to demonstrate:
*   Deploying sample microservices designed for observability.
*   Installing Prometheus using Helm.
*   Configuring Prometheus to automatically discover and scrape metrics from your microservices.
*   Installing Grafana using Helm.
*   Setting up Prometheus as a data source in Grafana.
*   Importing a pre-built Grafana dashboard for immediate insights.

### Project Structure

This repository contains the following directories:

*   `sample-app/`: Contains the source code and Dockerfile for a simple Go microservice that exposes Prometheus metrics.
*   `k8s/`: Kubernetes deployment and service manifests for the sample microservices.
*   `prometheus/`: Helm values file for configuring Prometheus.
*   `grafana/`: Helm values file for configuring Grafana, including a pre-defined dashboard.

### Prerequisites

Before you begin, ensure you have the following installed:

*   A Kubernetes cluster (e.g., Minikube, Kind, GKE, EKS, AKS).
*   `kubectl`: Kubernetes command-line tool.
*   `helm`: Kubernetes package manager.
*   `docker`: Docker engine (if building the sample microservice image locally).

### Setup and Deployment Steps

Follow these steps to deploy the monitoring stack and sample microservices.

#### 1. Build and Load Sample Microservice Images

First, build the Docker image for our sample microservice and make it available to your Kubernetes cluster.

Navigate to the `sample-app` directory:
    cd sample-app

Build the Docker image. We'll tag it as `local-registry/sample-microservice`.
    docker build -t local-registry/sample-microservice:latest .

If you are using Kind or Minikube, you need to load this image into the cluster's image cache:
    # For Kind cluster
    kind load docker-image local-registry/sample-microservice:latest

    # For Minikube cluster
    minikube image load local-registry/sample-microservice:latest

If you are using a remote cluster, you'll need to push this image to a container registry (e.g., Docker Hub, GCR, ECR) and update the `image:` field in `k8s/microservice-a-deployment.yaml` and `k8s/microservice-b-deployment.yaml` accordingly.

#### 2. Deploy Sample Microservices

Deploy the two sample microservices (`microservice-a` and `microservice-b`) to your Kubernetes cluster. These services are configured with annotations that Prometheus will use for discovery.

Navigate back to the root of the project:
    cd ..

Apply the Kubernetes manifests:
    kubectl apply -f k8s/

Verify that the microservices are running:
    kubectl get pods -l app=microservice-a
    kubectl get pods -l app=microservice-b

You should see pods in `Running` status.

#### 3. Deploy Prometheus using Helm

Add the Prometheus community Helm repository:
    helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
    helm repo update

Deploy Prometheus using the provided `prometheus-values.yaml` file. This configuration includes custom scrape rules to discover our sample microservices.

    helm install prometheus prometheus-community/prometheus -f prometheus/prometheus-values.yaml --namespace monitoring --create-namespace

Wait for Prometheus pods to be ready:
    kubectl get pods -n monitoring -l app=prometheus

#### 4. Deploy Grafana using Helm

Add the Grafana Helm repository:
    helm repo add grafana https://grafana.github.io/helm-charts
    helm repo update

Deploy Grafana using the provided `grafana-values.yaml` file. This configuration sets up Prometheus as a data source and imports a sample dashboard.

    helm install grafana grafana/grafana -f grafana/grafana-values.yaml --namespace monitoring

Wait for Grafana pod to be ready:
    kubectl get pods -n monitoring -l app.kubernetes.io/name=grafana

#### 5. Access Prometheus and Grafana UIs

##### Access Prometheus UI

To access the Prometheus UI, set up a port-forward:
    kubectl port-forward svc/prometheus-server 9090:80 -n monitoring

Open your browser and navigate to `http://localhost:9090`.
Go to "Status" -> "Targets" to see if `kubernetes-services` job is scraping `microservice-a` and `microservice-b`.

##### Access Grafana UI

To access the Grafana UI, set up a port-forward:
    kubectl port-forward svc/grafana 3000:80 -n monitoring

Open your browser and navigate to `http://localhost:3000`.
Login with:
*   Username: `admin`
*   Password: `prom-grafana-password` (defined in `grafana/grafana-values.yaml`)

After logging in, you should see the "Sample Microservices Overview" dashboard available under the Dashboards section. Click on it to see the metrics from your microservices. You can also navigate to "Connections" -> "Data sources" to confirm that the "Prometheus" data source is configured.

#### 6. Generate Some Traffic (Optional)

To see activity on your Grafana dashboard, you can generate some traffic to your microservices.

Port-forward to one of the microservices:
    kubectl port-forward svc/microservice-a 8080:8080

Then, in a new terminal, make some requests:
    curl http://localhost:8080/
    curl http://localhost:8080/data
    curl http://localhost:8080/
    # Repeat a few times

Check the Grafana dashboard, you should see the "HTTP Requests Per Second" graph updating.

### Cleanup

To remove all deployed components from your cluster:

1.  Delete Prometheus and Grafana Helm releases:
        helm uninstall prometheus -n monitoring
        helm uninstall grafana -n monitoring

2.  Delete the sample microservices:
        kubectl delete -f k8s/

3.  Delete the monitoring namespace:
        kubectl delete namespace monitoring

This will clean up all resources created by this project.
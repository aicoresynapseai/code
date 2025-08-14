This project demonstrates how to set up automated canary deployments using Flagger with Istio in a Kubernetes environment. It provides a sample application and all necessary configuration files to perform a canary release.

Project Name: flagger-canary-demo

Goal: To showcase a fully automated canary deployment process where a new version of an application is gradually rolled out, traffic is shifted incrementally, and performance metrics are monitored. If the new version performs well, it's automatically promoted; otherwise, it's rolled back.

Prerequisites:
- A Kubernetes cluster (version 1.20+ recommended).
- kubectl command-line tool configured to connect to your cluster.
- Helm 3 installed.
- Istio service mesh installed and configured on your cluster. Ensure the namespace you plan to use has Istio sidecar injection enabled (e.g., `kubectl label namespace <your-namespace> istio-injection=enabled`).

Setup Steps:

1.  **Install Istio (if not already present):**
    This project assumes Istio is already installed and functional. If not, please refer to the official Istio documentation for installation instructions. You can typically install it using istioctl or Helm.
    Ensure the namespace for the application has automatic sidecar injection enabled:
    Run: kubectl create namespace canary-demo
    Then: kubectl label namespace canary-demo istio-injection=enabled

2.  **Install Flagger:**
    Flagger is installed via Helm. First, add the Flagger Helm repository.
    Run: helm repo add flagger https://flagger.app
    Then: helm repo update
    Install Flagger into its own namespace (e.g., flagger-system).
    Run: helm install flagger flagger/flagger --namespace flagger-system --create-namespace --set meshProvider=istio --set metricsServer=http://prometheus-kube-prometheus-stack.monitoring:9090

    Note: The 'metricsServer' setting assumes you have Prometheus installed (e.g., via kube-prometheus-stack) and accessible at that service name and port within your cluster. Adjust if your Prometheus setup differs.

3.  **Deploy the Initial Application (v1):**
    Deploy the stable version of our sample application, along with its Kubernetes Service, Istio Gateway, and VirtualService.
    Apply the Kubernetes manifests:
    Run: kubectl apply -f flagger-canary-demo/app/namespace.yaml
    Run: kubectl apply -f flagger-canary-demo/app/deployment-v1.yaml
    Run: kubectl apply -f flagger-canary-demo/app/service.yaml
    Run: kubectl apply -f flagger-canary-demo/app/gateway.yaml
    Run: kubectl apply -f flagger-canary-demo/app/virtualservice.yaml

    Verify the application is running:
    Run: kubectl get pods -n canary-demo
    You should see 'podinfo-v1' pods running.

4.  **Configure Flagger Canary for the Application:**
    Apply the Flagger Canary custom resource definition that tells Flagger how to manage the 'podinfo' application's canary deployments.
    Apply the Canary manifest:
    Run: kubectl apply -f flagger-canary-demo/flagger/canary.yaml

    Flagger will now take control of the 'podinfo' deployment. Observe the changes:
    Run: kubectl get canary -n canary-demo
    Run: kubectl get deployments -n canary-demo
    You will notice Flagger creates new deployments like 'podinfo-primary'.

5.  **Trigger a Canary Deployment (Deploy v2):**
    Now, update the application to its new version (v2). This change to the 'podinfo' deployment manifest will trigger Flagger to start a new canary release.
    Apply the updated deployment manifest:
    Run: kubectl apply -f flagger-canary-demo/app/deployment-v2.yaml

    Observe the canary process:
    Run: watch kubectl get canary -n canary-demo

    You will see Flagger progressively shift traffic to the 'podinfo-canary' deployment, monitor metrics, and eventually promote it to 'podinfo-primary' (or roll back if metrics fail).
    You can also check the Flagger logs for more detailed insights:
    Run: kubectl logs -f deploy/flagger -n flagger-system

    To generate traffic and see the metrics in action, you can continuously send requests to the application's external IP/hostname.
    Find the Gateway's external IP:
    Run: kubectl get svc -n istio-system istio-ingressgateway -o jsonpath='{.status.loadBalancer.ingress[0].ip}'
    Then use curl or a load generator to hit this IP with the appropriate host header.
    Example: curl -H "Host: podinfo.example.com" http://<INGRESS_IP>/

Cleanup:
To remove all resources created by this demo:
Run the cleanup script:
Run: bash flagger-canary-demo/cleanup.sh

This will delete the deployments, services, Istio resources, Flagger canary, and the namespace.
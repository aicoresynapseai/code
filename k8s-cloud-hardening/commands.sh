# This script provides a step-by-step guide to deploy and verify the Kubernetes security configurations.

# --- Configuration Variables ---
NAMESPACE="secure-app-ns"

echo "--- Kubernetes Cloud Hardening Demo ---"
echo "Creating resources in namespace: $NAMESPACE"

# --- 1. Setup: Create Namespace ---
echo -e "\n--- 1. Creating Namespace ---"
kubectl create namespace $NAMESPACE
if [ $? -ne 0 ]; then
    echo "Namespace $NAMESPACE might already exist. Continuing..."
fi

# --- 2. RBAC (Role-Based Access Control) ---
echo -e "\n--- 2. Deploying RBAC resources ---"
kubectl apply -f rbac/01-service-account.yaml
kubectl apply -f rbac/02-role-pod-reader.yaml
kubectl apply -f rbac/03-rolebinding-pod-reader.yaml

echo -e "\n--- 2.1. Deploying Backend App with RBAC ServiceAccount ---"
kubectl apply -f apps/backend-deployment.yaml

echo -e "\n--- 2.2. Verifying RBAC permissions ---"
echo "Waiting for backend-app pod to be ready..."
kubectl wait --for=condition=ready pod -l app=backend -n $NAMESPACE --timeout=120s

BACKEND_POD=$(kubectl get pod -l app=backend -n $NAMESPACE -o jsonpath='{.items[0].metadata.name}')

echo -e "\n--- Attempting to list pods from backend-app (should succeed due to RBAC) ---"
kubectl exec -it $BACKEND_POD -n $NAMESPACE -- kubectl get pods # This should succeed
if [ $? -eq 0 ]; then
    echo "RBAC verification: Backend pod CAN list other pods (as expected)."
else
    echo "RBAC verification: Backend pod CANNOT list other pods (unexpected)."
fi

echo -e "\n--- Attempting to list deployments from backend-app (should fail - no permission) ---"
# This command requires 'list' on 'deployments' which is not granted to 'app-pod-reader-sa'
kubectl exec -it $BACKEND_POD -n $NAMESPACE -- kubectl get deployments # This should fail
if [ $? -ne 0 ]; then
    echo "RBAC verification: Backend pod CANNOT list deployments (as expected - least privilege)."
else
    echo "RBAC verification: Backend pod CAN list deployments (unexpected - potential over-privilege)."
fi

# --- 3. Network Policies ---
echo -e "\n--- 3. Deploying Network Policy: Default Deny All ---"
kubectl apply -f network-policies/01-deny-all-ingress-egress.yaml

echo -e "\n--- 3.1. Deploying Frontend App (to test network policies) ---"
kubectl apply -f apps/frontend-deployment.yaml

echo "Waiting for frontend-app pod to be ready..."
kubectl wait --for=condition=ready pod -l app=frontend -n $NAMESPACE --timeout=120s

FRONTEND_POD=$(kubectl get pod -l app=frontend -n $NAMESPACE -o jsonpath='{.items[0].metadata.name}')

echo -e "\n--- 3.2. Verifying network policy (Default Deny) ---"
echo "Attempting to curl backend from frontend (should FAIL due to default-deny)"
kubectl exec -it $FRONTEND_POD -n $NAMESPACE -- curl -I -m 5 backend-service # Should time out
if [ $? -ne 0 ]; then
    echo "Network Policy verification (Default Deny): Frontend CANNOT reach backend (as expected)."
else
    echo "Network Policy verification (Default Deny): Frontend CAN reach backend (unexpected!)."
fi

echo -e "\n--- 3.3. Deploying Network Policy: Allow Frontend to Backend ---"
kubectl apply -f network-policies/02-allow-frontend-to-backend.yaml

echo -e "\n--- 3.4. Verifying network policy (Frontend to Backend) ---"
echo "Attempting to curl backend from frontend (should now SUCCEED)"
# Give a moment for the network policy to propagate
sleep 5
kubectl exec -it $FRONTEND_POD -n $NAMESPACE -- curl -I -m 5 backend-service
if [ $? -eq 0 ]; then
    echo "Network Policy verification (Frontend to Backend): Frontend CAN reach backend (as expected)."
else
    echo "Network Policy verification (Frontend to Backend): Frontend CANNOT reach backend (unexpected!)."
fi

echo -e "\n--- 3.5. Verifying network policy (Egress to DNS) ---"
echo "Attempting to resolve an external domain from frontend (should FAIL without DNS egress policy)"
# This will likely fail as default-deny prevents DNS lookups
kubectl exec -it $FRONTEND_POD -n $NAMESPACE -- nslookup google.com
if [ $? -ne 0 ]; then
    echo "Network Policy verification: Frontend CANNOT resolve external DNS (as expected - no egress DNS policy yet)."
else
    echo "Network Policy verification: Frontend CAN resolve external DNS (unexpected!)."
fi

echo -e "\n--- 3.6. Deploying Network Policy: Allow Egress to DNS ---"
kubectl apply -f network-policies/03-allow-egress-to-dns.yaml

echo -e "\n--- 3.7. Verifying network policy (Egress to DNS) ---"
echo "Attempting to resolve an external domain from frontend (should now SUCCEED)"
sleep 5
kubectl exec -it $FRONTEND_POD -n $NAMESPACE -- nslookup google.com
if [ $? -eq 0 ]; then
    echo "Network Policy verification (Egress DNS): Frontend CAN resolve external DNS (as expected)."
else
    echo "Network Policy verification (Egress DNS): Frontend CANNOT resolve external DNS (unexpected!)."
fi

# --- 4. Secrets Management ---
echo -e "\n--- 4.1. Deploying Kubernetes Secret ---"
kubectl apply -f secrets/01-generic-secret.yaml

echo -e "\n--- 4.2. Deploying Secret Consumer App ---"
kubectl apply -f secrets/02-deployment-consuming-secret.yaml

echo "Waiting for secret-consumer-app pod to be ready..."
kubectl wait --for=condition=ready pod -l app=secret-consumer -n $NAMESPACE --timeout=120s

SECRET_CONSUMER_POD=$(kubectl get pod -l app=secret-consumer -n $NAMESPACE -o jsonpath='{.items[0].metadata.name}')

echo -e "\n--- 4.3. Verifying Secret consumption ---"
echo "Inspecting logs of secret-consumer-app (should show API Key and DB Password)"
kubectl logs $SECRET_CONSUMER_POD -n $NAMESPACE | grep "API Key"
kubectl logs $SECRET_CONSUMER_POD -n $NAMESPACE | grep "DB Password"
# You should see:
# API Key: mysupersecretapikey
# DB Password: securedbpassword
echo "Note: Kubernetes Secrets are base64 encoded, not truly encrypted. For production, consider external KMS/Vault."

# --- Cleanup ---
echo -e "\n--- Cleaning up resources ---"
echo "Deleting namespace $NAMESPACE and all its resources..."
kubectl delete namespace $NAMESPACE --ignore-not-found=true
echo "Cleanup complete."

echo -e "\n--- Demo Finished ---"
echo "Remember to explore each YAML file and the commands for deeper understanding."
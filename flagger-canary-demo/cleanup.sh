#!/bin/bash

# Cleanup script to remove all resources created by the Flagger Canary Demo.

echo "--- Deleting Flagger Canary resource..."
kubectl delete canary podinfo -n canary-demo --ignore-not-found

echo "--- Deleting Istio Gateway and VirtualService..."
kubectl delete gateway podinfo-gateway -n canary-demo --ignore-not-found
kubectl delete virtualservice podinfo -n canary-demo --ignore-not-found

echo "--- Deleting application Deployments and Services..."
# Note: Flagger creates 'podinfo-primary' and 'podinfo-canary' deployments.
# Deleting the original 'podinfo' deployment might not immediately remove them,
# but deleting the Canary resource above should trigger Flagger to clean them up.
# We'll explicitly delete any remaining ones for robustness.
kubectl delete deployment podinfo -n canary-demo --ignore-not-found
kubectl delete deployment podinfo-primary -n canary-demo --ignore-not-found
kubectl delete deployment podinfo-canary -n canary-demo --ignore-not-found
kubectl delete service podinfo -n canary-demo --ignore-not-found

echo "--- Deleting the 'canary-demo' namespace..."
kubectl delete namespace canary-demo --ignore-not-found

echo "--- Uninstalling Flagger Helm chart (optional, but good for full cleanup)..."
helm uninstall flagger -n flagger-system --ignore-not-found
kubectl delete namespace flagger-system --ignore-not-found # Delete Flagger's namespace

echo "Cleanup complete."
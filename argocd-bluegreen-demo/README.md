Title: Implementing Blue-Green Deployments in ArgoCD

This project provides a practical example of setting up GitOps-based blue-green deployments on Kubernetes using ArgoCD. Blue-green deployment is a release strategy that reduces downtime and risk by running two identical production environments (Blue and Green). At any given time, only one of the environments is "live" (e.g., serving production traffic), while the other stands by as an idle, test, or staging environment.

How This Demo Works:

1.  **Two Environments (Blue & Green):** We define base Kubernetes Deployment and Service manifests. Kustomize overlays are then used to create distinct "blue" and "green" versions of these applications, each with unique labels (e.g., `version: blue`, `version: green`) and potentially different image tags or configuration.

2.  **Stable Router Service:** A single Kubernetes Service, `my-app-router-service`, acts as the stable entry point for all incoming traffic. Its name and IP address remain constant.

3.  **Traffic Switching via Selector:** The `my-app-router-service` uses a `selector` to determine which set of application pods (blue or green) it routes traffic to. To perform a traffic switch, we simply update this selector in Git to point to the desired environment's pods.

4.  **ArgoCD & GitOps Automation:**
    *   ArgoCD continuously monitors this Git repository.
    *   To deploy a new version (e.g., move from Blue to Green):
        a.  The `argocd/application.yaml`'s `path` is initially set to the `kubernetes/overlays/blue` Kustomize directory. This deploys the blue application and configures the router to point to blue.
        b.  To "deploy green", you modify `argocd/application.yaml` to change its `path` to `kubernetes/overlays/green`.
        c.  When this change is committed and pushed to Git, ArgoCD detects it. It then applies the `green` overlay:
            *   The "green" Deployment and Service are created (or updated).
            *   The existing `my-app-router-service` is patched to change its `selector` to `version: green`.
            *   Traffic immediately switches to the green environment's pods.
            *   ArgoCD, with `prune: true` enabled, automatically cleans up the old blue deployment and service resources as they are no longer part of the active Kustomize overlay.

Project Structure:

*   `argocd/`: Contains the ArgoCD Application definition.
*   `kubernetes/base/`: Core, generic Kubernetes manifests (Deployment, Service, Router Service, Ingress).
*   `kubernetes/overlays/blue/`: Kustomize files to configure the "blue" environment and make the router point to it.
*   `kubernetes/overlays/green/`: Kustomize files to configure the "green" environment and make the router point to it.

Setup and Deployment Steps:

Prerequisites:
*   A running Kubernetes cluster.
*   ArgoCD installed and configured in your cluster (e.g., in the `argocd` namespace).
*   `kubectl` and `kustomize` CLI tools installed (optional for local testing of kustomize, but ArgoCD uses them).
*   Access to a Git repository where you'll push this code (e.g., GitHub, GitLab).

1.  **Clone this repository and Push to Your Git Repo:**
    Clone this repository locally. Copy the provided files into a new Git repository you control.
    Example workflow:
    git init
    git add .
    git commit -m "Initial blue-green demo"
    git remote add origin <YOUR_GITHUB_REPO_URL> # Replace with your actual repo URL
    git push -u origin main

    **Important:** Update the `repoURL` in `argocd/application.yaml` to point to your new Git repository. Also, adjust the `host` in `kubernetes/base/ingress.yaml` to a domain you control or can map in your local hosts file.

2.  **Initial Deployment (Blue Environment):**
    First, let's deploy the 'blue' version of the application.
    Ensure that `argocd/application.yaml` currently has `path: kubernetes/overlays/blue`.
    Apply the ArgoCD application manifest to your cluster:
    kubectl apply -f argocd/application.yaml -n argocd

    Go to the ArgoCD UI (typically exposed via a Service/Ingress in the `argocd` namespace). You should see a new application named `bluegreen-app`. Wait for it to sync and become healthy. This process deploys `my-app-blue` deployment, `my-app-blue-service`, and configures `my-app-router-service` to direct traffic to `my-app-blue` pods.

3.  **Verify Blue Environment:**
    Get the Ingress host/IP:
    kubectl get ingress my-app-ingress -n bluegreen-demo
    Access your application via the provided Ingress host/IP in your browser. You should see a page indicating "Hello from Blue Environment! (v1.0.0)".

4.  **Prepare for Green Deployment:**
    Imagine you have a new application version (v2.0.0) you want to roll out.
    Modify `kubernetes/overlays/green/deployment-patch.yaml` to use your new application image (it's currently `nginxdemos/hello:blue-green` but you could change it to a truly different image/tag if desired) and its corresponding message.

5.  **Switch Traffic to the Green Environment:**
    To perform the blue-green switch, you update the Git repository to tell ArgoCD to apply the `green` overlay instead of `blue`.
    Edit `argocd/application.yaml`.
    Change the `path` from `kubernetes/overlays/blue` to `kubernetes/overlays/green`.

    Commit and push this change to your Git repository:
    git add argocd/application.yaml
    git commit -m "Switch traffic to green environment"
    git push origin main

    ArgoCD will detect this change immediately (or within its sync interval). It will:
    *   Deploy the `my-app-green` deployment and `my-app-green-service`.
    *   Apply the patch to the existing `my-app-router-service` to change its `selector` to point to `my-app-green` pods.
    *   Due to `prune: true` in `syncPolicy`, ArgoCD will then remove the `my-app-blue` deployment and `my-app-blue-service` as they are no longer referenced by the active overlay.
    *   You should see traffic seamlessly switch.

6.  **Verify Green Environment:**
    Refresh your browser using the same Ingress host/IP. You should now see "Hello from Green Environment! (v2.0.0)".

7.  **Rollback (Optional):**
    To perform a rollback to the 'blue' environment, simply revert the change in `argocd/application.yaml`:
    Edit `argocd/application.yaml`. Change the `path` from `kubernetes/overlays/green` back to `kubernetes/overlays/blue`.

    Commit and push this change:
    git add argocd/application.yaml
    git commit -m "Rollback to blue environment"
    git push origin main

    ArgoCD will sync and switch traffic back to 'blue', cleaning up the 'green' resources in the process.

This setup provides a robust and automated way to manage deployments with minimal downtime and easy rollback capabilities, all driven by GitOps principles.
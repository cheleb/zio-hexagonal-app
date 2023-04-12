# ArgoCD 

ArgoCD is a declarative, GitOps continuous delivery tool for Kubernetes.

```shell
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
```

## Accessing the ArgoCD UI

Need to wait for the argocd-server to be ready before accessing the UI.

* retrieve the initial admin password

```shell
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d;
```

* port-forward the argocd-server service to your local machine

```shell
kubectl port-forward -n argocd services/argocd-server 8080:80
```


* login to the UI using `admin` and the password retrieved above http://localhost:8080


Now let proceed to [application environment setup](app-setup.md).

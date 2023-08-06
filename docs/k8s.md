# Kubernetes, minikube

For development purposes, we use minikube to run a local Kubernetes cluster. This is the easiest way to get started with Kubernetes.

## Enabling Insecure Registries

If you are using a local registry, you need to enable insecure registries in minikube. This is required because the registry is not using TLS.

Start minikube with the following options:

```shell
minikube start --cpus 6 --memory 8g --insecure-registry "10.0.0.0/24"
```



Then update addons

* enable the registry addon
* disable the storage-provisioner addon

```shell
minikube addons enable registry
```


![registry port](images/registry-vm-port.png)

Remember the port number, it will be used in the next step.


Bridge the registry to the host:

```shell
docker run --rm -it --network=host alpine ash -c "apk add socat && socat TCP-LISTEN:5000,reuseaddr,fork TCP:$(minikube ip):5000"
```

You can now push images to the registry and have access to the catalog:

```shell
curl localhost:55541/v2/_catalog
```


## Enabling volume with minikube multinode

```shell
minikube start --cpus 6 --memory 8g --insecure-registry "10.0.0.0/24" --nodes 4
```

```shell
minikube addons enable registry
minikube addons disable storage-provisioner
kubectl delete storageclass standard
```


```shell
kubectl apply -f k8s/kubevirt-hostpath-provisioner.yaml
```


For more information, see the minikube documentation:


https://minikube.sigs.k8s.io/docs/handbook/registry/#enabling-insecure-registries


Follow with the next step to install the [ArgoCD](argocd.md) operator.

## Appication priority

The application priority is used to determine the order in which applications may be OOMKilled by the Kubernetes scheduler.


* Guaranteed, won't be killed.
* Burstable, may be killed if really necessary.
* BestEffort, may be killed at any time.


## Developer experience 

* https://inlets.dev/blog/2022/06/24/fixing-kubectl-port-forward.html

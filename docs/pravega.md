

kubectl apply -f k8s/zookeeper.yaml


```bash
git clone git@github.com:pravega/zookeeper-operator.git
```


```bash
kubectl create -f config/crd/bases
```


```bash
kubectl create -n ziohexa -f zk-with-istio.yaml
```

kubectl apply -f ziohexa_ns_rbac.yaml 

kubectl create -n ziohexa -f zookeeper/manager.yaml

----

helm install zookeeper-operator pravega/zookeeper-operator -n ziohexa --version=0.2.15
helm --namespace ziohexa install zookeeper charts/zookeeper --values charts/zookeeper/values/minikube.yaml

helm install \
  cert-manager jetstack/cert-manager \
  --namespace cert-manager \
  --create-namespace \
  --version v1.11.0 \
  --set installCRDs=true

kubectl apply -f k8s/certificate.yaml 

helm install --namespace ziohexa bookkeeper-operator pravega/bookkeeper-operator --version=0.2.3 --set webhookCert.certName=selfsigned-cert-bk --set webhookCert.secretName=selfsigned-cert-tls-bk



 kubectl create -n ziohexa -f zk-with-istio.yaml

## 
 https://github.com/pravega/zookeeper-operator#installation-on-minikube

 https://cert-manager.io/docs/installation/helm/

 https://github.com/pravega/pravega-operator/blob/master/doc/longtermstorage.md#use-nfs-as-longtermstorage
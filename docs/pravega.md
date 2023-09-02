# Pravega

## Docker compose

```bash
docker-compose -f docker-compose.yml up -d
```

### Zookeeper

TBD




kubectl apply -f k8s/zookeeper.yaml


```bash
git clone git@github.com:pravega/zookeeper-operator.git
```


```bash
kubectl create -f config/crd/bases
# All namespaces
kubectl create -f config/rbac/all_ns_rbac.yaml
```


```bash
kubectl create -n ziohexa -f config/manager/manager.yaml

helm install zookeeper-operator pravega/zookeeper-operator -n ziohexa --version=0.2.15
helm --namespace ziohexa install zookeeper charts/zookeeper --values charts/zookeeper/values/minikube.yaml

helm install \
  cert-manager jetstack/cert-manager \
  --namespace cert-manager \
  --create-namespace \
  --version v1.11.0 \
  --set installCRDs=true

kubectl apply -f k8s/certificate.yaml 

helm install --namespace ziohexa bookkeeper-operator pravega/bookkeeper-operator --version=0.2.4 --set webhookCert.certName=selfsigned-cert-bk --set webhookCert.secretName=selfsigned-cert-tls-bk

helm install bookeeper pravega/bookkeeper --version=0.10.5 --set zookeeperUri=zookeeper-client:2181 --set pravegaClusterName=PRAVEGA_CLUSTER_NAME -n default

helm install pravega-operator pravega/pravega-operator --version=0.6.3 --set webhookCert.certName=selfsigned-cert-pv --set webhookCert.secretName=selfsigned-cert-tls-pv

helm install pravega pravega/pravega --version=0.10.5 --set zookeeperUri=zookeeper-client:2181 --set bookkeeperUri=bookkeeper-bookie-headless:3181 --set storage.longtermStorage.filesystem.pvc=pravega-tier2

 kubectl create -n ziohexa -f zk-with-istio.yaml

## 
 https://github.com/pravega/zookeeper-operator#installation-on-minikube

 https://cert-manager.io/docs/installation/helm/

 https://github.com/pravega/pravega-operator/blob/master/doc/longtermstorage.md#use-nfs-as-longtermstorage
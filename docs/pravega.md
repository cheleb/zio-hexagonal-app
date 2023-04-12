

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
 kubectl create -n ziohexa -f zk-with-istio.yaml
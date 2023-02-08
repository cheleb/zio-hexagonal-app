
# Setup for Ziohexa

* namespace

```shell
kubectl create namespace ziohexa
```
* secret for postgresql

```shell
kubectl -n ziohexa  create secret generic postgresql-secrets --from-literal=POSTGRES_PASSWORD=*************
```
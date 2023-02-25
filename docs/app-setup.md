
# Setup for Ziohexa

* namespace

```shell
kubectl create namespace ziohexa
```
* secret for postgresql

```shell
kubectl -n ziohexa  create secret generic postgresql-secrets --from-literal=POSTGRES_PASSWORD=*************
```


Now all is ready to deploy the app.

```shell
git clone git@github.com:cheleb/zio-hexagonal-app-config.git
cd zio-hexagonal-app-config
kubectl apply -f application.yaml
```

Forwards the port 8888 to access the app:

```shell
kubectl port-forward -n ziohexa services/cal-server-svc 8888:8888
```


Et voilà, the app is deployed and ready to use.


1. ArgoCD UI to see the app deployed: http://localhost:8080/applications
2. App UI to access the app: http://localhost:8888

Soon ...

3. Swagger UI to access the app: http://localhost:8888/swagger-ui/index.html?url=/api-docs/swagger.json
4. Prometheus: http://localhost:9090/graph
5. Grafana: http://localhost:3000
6. Kiali: http://localhost:20001/kiali/console
7. Jaeger: http://localhost:16686
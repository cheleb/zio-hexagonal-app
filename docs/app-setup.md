
# Setup for Ziohexa

Now that:

* the cluster is up and running
* argocd is installed and configured with the image updater

We can proceed to deploy the app.

* namespace

```shell
kubectl create namespace ziohexa
```
* secret for 

This app uses a postgresql database, we need to create a secret to store the password.

```shell
kubectl -n ziohexa  create secret generic postgresql-secrets --from-literal=POSTGRES_PASSWORD=*************
```

## Build and deploy the app

```shell
git clone git@github.com:cheleb/zio-hexagonal-app.git
cd zio-hexagonal-app
```

By default

1. Check tag

The repository must be clean (aka no changes) and the tag must be the same as the one in the helm files.

```shell
git describe --tags
```

Must out put a "clean" tag such as v0.2.2

Do what ever needed (add, reset, commit, tag ...) to have a clean repo and the correct tag.

2. Publish the image

```shell
NODE_OPTIONS='--openssl-legacy-provider' sbt Docker/publish
```

Some

Double check that the [repository](https://registry.orb.local/v2/_catalog) at https://registry.orb.local/v2/_catalog 

* [cal-server](https://registry.orb.local/v2/cheleb/cal-server/tags/list) at https://registry.orb.local/v2/cheleb/cal-server/tags/list
* [currency-service](https://registry.orb.local/v2/cheleb/currency-service/tags/list) at https://registry.orb.local/v2/cheleb/currency-service/tags/list



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

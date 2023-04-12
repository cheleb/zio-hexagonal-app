# Istio 

Istio is an open platform to connect, manage, and secure microservices. Istio provides an easy way to create a network of deployed services with load balancing, service-to-service authentication, monitoring, and more, without requiring any changes in service code.

## Download Istio

https://github.com/istio/istio/releases

And untar as $ISTIO_HOME

## Install Istio

```bash
istioctl install --set profile=demo -y
```

You may need to add the istio bin directory to your PATH environment variable on a Mac:

```bash
export PATH=$PATH:$ISTIO_HOME/bin
```


You may need to remove istio from MacOS quarantine:

```bash
xattr -r -d com.apple.quarantine $ISTIO_HOME
```

```bash
istioctl version
```
```
client version: 1.17.1
control plane version: 1.17.1
data plane version: 1.17.1 (2 proxies)
```

## Install addons (Jaeger, Kiali, Grafana, Prometheus...)

```bash
kubectl apply -f ./samples/addons
```


### Before
```bash
> $ kubectl get pod -n ziohexa     
```

```bash
NAME                          READY   STATUS    RESTARTS      AGE
cal-server-f85645549-sdqhx    1/1     Running   0             40m
currencies-6947677ccb-h2pzm   1/1     Running   0             40m
postgresql-sfs-0              1/1     Running   2 (75m ago)   7d18h
```

### Apply the Istio sidecar injection to ziohexa namespace

```bash
kubectl label namespace ziohexa istio-injection=enabled
```
### Restart the application

### After

```bash
```

```
NAME                          READY   STATUS    RESTARTS      AGE
cal-server-54dfdf44d9-xfl5h   2/2     Running   0             27s
currencies-7778b5cff9-57p8j   2/2     Running   2 (16s ago)   27s
postgresql-sfs-0              2/2     Running   0             27s
```


## Performance impact

https://pklinker.medium.com/performance-impacts-of-an-istio-service-mesh-63957a0000b

# Istio 

## Download Istio

https://github.com/istio/istio/releases

## Install Istio

```bash
istioctl install --set profile=demo -y
```

## Install addons (Jaeger, Kiali, Grafana, Prometheus...)

```bash
$ kubectl apply -f ./samples/addons
```


### Before
```
> $ kubectl get pod -n ziohexa     
NAME                          READY   STATUS    RESTARTS      AGE
cal-server-f85645549-sdqhx    1/1     Running   0             40m
currencies-6947677ccb-h2pzm   1/1     Running   0             40m
postgresql-sfs-0              1/1     Running   2 (75m ago)   7d18h
```



## Performance impact

https://pklinker.medium.com/performance-impacts-of-an-istio-service-mesh-63957a0000b

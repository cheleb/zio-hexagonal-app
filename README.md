## ZIO Hexagonal application example

This is a simple example of a hexagonal application using ZIO.

### Requirements

* JDK 19 
* sbt 1.8.2
* Scala 3.2.2
* kubernetes

### Usage

This is a normal sbt project. You can compile code with `sbt compile`, run it with `sbt run`, and `sbt console` will start a Scala 3 REPL.

### Running the application

### Sbt launch

The application can be run with `sbt run`. It will start a web server on port 8080. You can then use `curl` to interact with the application:

### Kubernetes launch

The application can be deployed on a kubernetes cluster. You can use the provided `k8s` folder to deploy it. You can use the `k8s/deploy.sh` script to deploy the application on your cluster. You can use the `k8s/undeploy.sh` script to undeploy the application from your cluster.

See the [k8s](docs/k8s.md) documentation for more information.




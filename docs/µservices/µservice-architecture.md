# µservice Architecture

* system operation: high-level operations that are performed on the system, an abstraction of a request system must handle.
* Decomposition as servics, corresponding to business capabilities or DDD subdomains.
* Define services API, including:
  * operations
  * data types
  * error handling
  * IPC mechanism


## Obstacle to decomposition:

* Network latency
* Synchroneous communication
* Coupling between services to maintain consistency

## Tricks to overcome obstacles

Concept of self-contained transaction/service, which is a transaction that can be executed in a single service, without any external dependencies.

## Service Discovery

* Service registry
* Service discovery
* Service location
* Service routing
* Service load balancing
* Service monitoring
* Service health checking
* Service fault tolerance
* Service failover
* Service failback
* Service load shedding

## Service Discovery Patterns

* Client-side discovery
* Server-side discovery
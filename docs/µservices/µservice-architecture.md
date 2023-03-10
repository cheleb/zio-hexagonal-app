# µservice Architecture

* system operation: high-level operations that are performed on the system, an abstraction of a request system must handle.
* Decomposition as servics, corresponding to business capabilities or DDD subdomains.
* Define services API, including:
  * operations
  * data types
  * error handling
  * IPC mechanism


Obstacle to decomposition:

* Network latency
* Synchroneous communication
* Coupling between services to maintain consistency
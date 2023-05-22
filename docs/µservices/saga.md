# Distributed transaction management with Sagas

Sagas are a pattern for distributed transaction management. They are a good fit for µservices architectures, where you have multiple services that need to be updated in a coordinated way. Sagas are also a good fit for long-running business transactions that involve multiple steps, such as booking a hotel room and a flight.

Different transaction types:

* compensable transactions
* pivot transactions
* retriable transactions

## Choreography vs Orchestration

### Choreography
 Each service is responsible for its own transactional integrity. The services communicate with each other using events. Each service listens for events and reacts to them by performing its own transactional work and emitting new events. The services are loosely coupled because they don’t need to know about each other. The choreography is emergent from the events that are emitted and reacted to by the services.
 Service must kwon how to react to events from other services.

### Orchestration
 There is a central coordinator that is responsible for the transactional integrity of the entire business transaction. The coordinator tells the services what to do by sending them commands. The services are tightly coupled to the coordinator because they need to know how to interpret the commands that are sent to them.

 Service are not coupled at all to each other, but they are coupled to the orchestrator.

## Contermeasures 

* semantic locking
* commutative operations
* pessimistic view (reordering saga steps to minimize the time the saga is in an inconsistent state)
* re-read (versioning)
* version file (record reqquests when they are received, and then some subsequent request is received, the version file is checked to see if the request is still to be processed)
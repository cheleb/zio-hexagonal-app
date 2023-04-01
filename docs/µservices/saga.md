# Distributed transaction management with Sagas

Sagas are a pattern for distributed transaction management. They are a good fit for µservices architectures, where you have multiple services that need to be updated in a coordinated way. Sagas are also a good fit for long-running business transactions that involve multiple steps, such as booking a hotel room and a flight.

Different transaction types:

* compensable transactions
* pivot transactions
* retriable transactions


## Contermeasures 

* semantic locking
* commutative operations
* pessimistic view (reordering saga steps to minimize the time the saga is in an inconsistent state)
* re-read (versioning)
* version file (record reqquests when they are received, and then some subsequent request is received, the version file is checked to see if the request is still to be processed)
# µservice versionning

Before we start, let's define what a version is:

> A version is a unique identifier for a specific release of a software product.

In the context of µservices, a version is a unique identifier for a specific release of a µservice.

## Why versioning?

Versioning is a way to keep track of the changes made to a µservice. It is also a way to ensure that the µservice is backward compatible with the previous version.

## How to version?

There are several ways to version a µservice. The most common are:

* Semantic versioning
* Date versioning
* Build number versioning

### Semantic versioning

Semantic versioning is a versioning scheme that is widely used in the software industry. It is based on the following format:

```MAJOR.MINOR.PATCH```

* MAJOR: when you make incompatible API changes
* MINOR: when you add functionality in a backwards-compatible manner
* PATCH: when you make backwards-compatible bug fixes

### Date versioning

Date versioning is a versioning scheme that is based on the date of the release. It is based on the following format:

```YYYY.MM.DD```

### Build number versioning

Build number versioning is a versioning scheme that is based on the build number of the release. It is based on the following format:

```BUILD_NUMBER```

## How to version in a µservice architecture?

In a µservice architecture, versioning is a way to ensure that the µservice is backward compatible with the previous version. It is also a way to keep track of the changes made to a µservice.

## How to version in a µservice architecture?

### REST API

* URL versioning (e.g. ```/api/v1/```)
* Header versioning (e.g. ```Accept: application/vnd.mycompany.myapp.v1+json```)


### gRPC API

gRPC API versioning is based on the following concepts:

* Package versioning (e.g. ```package myapp.v1;```)
* Service versioning (e.g. ```service MyServiceV1 { ... }```)
* Payload versioning (e.g. ```message MyMessageV1 { ... }```)

In all cases, the version is part of the name of the package, service or payload.
* Package versioning is the most common way to version a gRPC API.
* Payload compatibility is ensured by the use of [protobuf](https://developers.google.com/protocol-buffers/).
# Objects in DDD

Objects are the basic building blocks of a domain model. In DDD they are 

* Entities
* Value Objects
* Aggregates

## Entities

Entities are objects that have a unique identity. They are identified by an ID. The ID is usually a value object.

Entities are usually designed as such when we care about the identity of the object and not about its values:

- "Who/which they are" rather than "What they are".

Entities are usually mutable. They can change their state over time. 

Attributes of an entity should strictly limit to the attributes that are relevant to the support the behavior of the entity.

## Value Objects

Value objects are objects that are identified by their values.
They are immutable hence:

* are compared by their values.
* can be safely shared between different objects.

Value objects are usually designed as such when we care about the values of the object and not about its identity:

- "What they are" rather than "Who/which they are".

# Aggregates

Aggregates are a cluster of objects that are treated as a unit for the purpose of data changes.

Aggregates rules:

* Reference only the aggregate root.

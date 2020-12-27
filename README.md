# miniConnect

Minimalistic DB connector framework and JDBC bridge 

## TODO:

- Redesign `MiniValue`
- - to be serializable between client and server
- - - in a java-independent way
- - structure:
- - - header (type description, etc.)
- - - content (byte array)
- - add a value interpreter (only the end user need it)
- - how to handle large values?
- - - lazy/async loading?
- Complete the basic API
- Design and implement the client-server protocol
- Create dummy implementation
- - First create a minimalistic version for high-level testing
- - Create a sample implementation with a handy set of query types (with in-memory arrays)
- Create a JDBC implementation
- Design and implement the client-server protocol

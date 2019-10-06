# RMIaddressbookWithMOM
RMI Client-Server project that consists of 4 classes and a DB. 

AddressBook is an interface.
AddressBookImpl is the class that implements the AddressBook. AddressBookImpl depicts the middleware between the client and server. 
RPCServer is the server.
RPCClient is the client.

Communication established through Message Oriented Middleware (MOM).

Connection is established between client and server through a message queue. 
The message queue provides the needed data for creating, inserting, updating and deleting data.

The project is a contact application that processes a contact's data through SQL commands. The contacts are saved in a local DB.

Run RPCServer and RPCClient as Java Applications.

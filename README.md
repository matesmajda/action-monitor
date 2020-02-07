# action-monitor

This project is a chat-like application where users can send messages to each other.

### Requirements

You need Java 8 installed to run this project.
You can replace mvn commands with `mvnw.cmd` in Windows or `./mvnw` in Linux if you don't have Maven installed.

### Running the application

`mvn spring-boot:run`

Running from IntelliJ:
Enable `Delegate IDE build/run actions to Maven` option in `Settings (Preferences)` -> `Build, Execution, Deployment` -> `Build Tools` -> `Maven` -> `Runner` tab.
It is required for generating the META-INF/build.properties file. 

### Running unit tests
`mvn test`

I used Mockito to mock the dependencies of the services.

Jacoco reports are generated here: `target/site/jacoco/com.bv.actionmonitor.status/index.html`

### Running integration tests
`mvn integration-test`

### Usage

The application exposes two REST endpoints:

-   `GET http://localhost:8080/status` a health check endpoint returning "OK"
-   `GET http://localhost:8080/version` an endpoint returning the actual version of the application in JSON format

    See them on `http://localhost:8080/swagger-ui.html`

And one websocket endpoint:

-   `GET http://localhost:8080/messages` an endpoint for sending messages between users. 
    The client can upgrade to websocket protocol on this endpoint, however it is is wrapped in SockJS so clients without websocket support can use it too.

    You can try it here: `http://localhost:8080/app.html`.
    It uses basic authentication, the default users are: `user1`, `user2`, `user3`, password is `pass` for each of them.

### Experimenting and monitoring

- Swagger endpoint for REST endpoints: `http://localhost:8080/swagger-ui.html`

- Ui for websocket: `http://localhost:8080/app.html` Auth: `user1:pass`, `user2:pass`, `user3:pass`

- Queue monitoring (and a lot more): `http://localhost:8081/hawtio/`

- DB web console: `http://localhost:8080/h2-console`
     - Driver Class: `org.h2.Driver`
     - JDBC URL: `jdbc:h2:mem:testdb`
     - User Name: `sa`
     - Password: (leave it empty)

### Architecture

##### Major components: ([See diagram](diagram.png))
1. MessageController websocket endpoint that is the entry point for the messages
2. embedded H2 database as the persistent message store
3. embedded ActiveMQ as the message broker

##### Websocket:
The websocket is wrapped in by SockJS so any client that does not support websocket natively may use the system.
STOMP is used as the communication protocol over the websocket which is supported by Spring and message brokers too.

##### Message model:
- On client side there are two properties: `recipient` and `content`.
The `recipient` is the part of the destination in a STOMP header, while the `content` is the BODY of the STOMP message.
- On server side the message is extended with the `sender` which is the username of the authenticated user and `date` that is the time of receiving the message.

##### Message flow:
1. the message is validated with Spring validation.
2. the message is assembled and sanitized: HTML escaping is applied against XSS attacks.
3. the message is stored in the database.
4. the message is sent to the message broker to the message queue of the recipient.
5. the message broker ensures that the message is delivered to the recipient if it is subscribed to the queue

##### Exception handling:
- Exceptions are logged to the console.
- In case of a message can not be inserted to the database, the message will not be sent to the message broker. The sender is not notified about the error.

### Possible improvements
- Implement proper error handling, for example notifying the sender in case of any server-side failure
- Creating multiple type of messages
- Creating separate configuration for different environments.
- Creating proper authentication
- Creating stress tests 
- Finetuning database and ActiveMQ: for example purging inactive destinations automatically

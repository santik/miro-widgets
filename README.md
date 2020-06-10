## Solution

### Chosen technologies
- Java 11
- SpringBoot
- JUnit
- Swagger
- H2

### Application

Type of the storage can be changed in application.properties `app.storage.type`. Valid values: `inmemory` and `jpa`.

Data can be retrieved via REST API. 
For convenience swagger was set up. Url is http://localhost:8080/api/swagger-ui.html (when running the service)

### Testing
Application has a high coverage with UnitTests.
`mvn clean test` will run all the tests

### Running
`mvn clean package` - this will run tests and create jar file

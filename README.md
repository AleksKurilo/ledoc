The application is back-end of Ledoc application version 4.
To launch the application do the following steps:

1. Clone the repository to your PC.
2. Download and install PostgreSQL server version 10 or higher.
3. Launch PostgreSQL server.
4. Making use of Postgres command line tools or Postgres client such as pgAdmin, create user "ledoc" with password "ledoc".
5. Create database "ledocdb" and set user "ledoc" as its owner.
6. Create schema "main" in database "ledocdb".
7. Open command line on the root directory of the cloned project.
8. Run "mvn clean install" command (make sure you have Apache Maven build tool installed).
9. Enter "target" directory from command line.
10. Find the JAR file. Execute "java -jar [JAR_name].jar" command (make sure port 8080 is not occupied by another process/application). The server is starting.
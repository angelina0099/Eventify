# Eventify

Eventify is a menu-driven Java console application for managing events. It includes in-memory event management, text-file import and export, and optional PostgreSQL storage through JDBC or Hibernate.

## Features

- Add, list, search, update, and delete events
- Filter events by location and sort them by name or date
- Save and load events using `events.txt`
- Manage persistent events in PostgreSQL using JDBC or Hibernate

## Requirements

- JDK 25
- Maven
- PostgreSQL for the JDBC and Hibernate menus

The in-memory and file features do not require a database.

## Build

From the project directory, run:

```shell
mvn clean package
```

## Run

Run `org.example.Main` from your IDE, or use Maven:

```shell
mvn org.codehaus.mojo:exec-maven-plugin:3.5.0:java -Dexec.mainClass=org.example.Main
```

## PostgreSQL configuration

The JDBC and Hibernate features connect to `jdbc:postgresql://localhost:5433/eventify_db`. Create the `eventify_db` database and make sure PostgreSQL is listening on port `5433` before selecting either database option.

The database user defaults to `postgres`. Set `EVENTIFY_DB_USER` to use another user, and provide the password with either the `EVENTIFY_DB_PASSWORD` environment variable or a local `database.properties` file in the project directory:

```properties
db.password=your-password
```

`database.properties` is excluded from Git. The JDBC menu creates its table when needed; Hibernate updates its mapped schema on startup.

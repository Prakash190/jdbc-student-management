# JDBC Student Management System

A console-based Java mini project that demonstrates JDBC database connectivity and complete CRUD operations with MySQL.

## Features

- Add a student
- List all students
- Find a student by ID
- Search students by name
- Update a student's details
- Delete a student
- Uses `PreparedStatement`, transactions, validation, and try-with-resources

## Tech stack

- Java 17+
- JDBC
- MySQL 8+
- Maven (for downloading the JDBC driver)

## Prerequisites

1. Install Java 17 or newer.
2. Install and start MySQL Server.
3. Install Maven, or download the MySQL JDBC driver manually.

## Database configuration

The application creates the `student_manager` database and its `students` table automatically (provided your MySQL user has permission).

Set these environment variables before running if your MySQL credentials differ from the defaults:

```powershell
$env:JDBC_URL = "jdbc:mysql://localhost:3306/student_manager?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC"
$env:DB_USER = "root"
$env:DB_PASSWORD = "your_password"
```

If no variables are set, it connects to `root` with an empty password on `localhost:3306`.

## Run with Maven

```powershell
mvn compile
mvn dependency:build-classpath -Dmdep.outputFile=classpath.txt
$cp = "target/classes;$(Get-Content classpath.txt)"
java -cp $cp com.prakash.jdbc.Application
```

## Run without Maven

1. Download MySQL Connector/J and place its JAR in a `lib` folder.
2. Compile and run:

```powershell
javac -d out (Get-ChildItem -Recurse -Filter *.java src/main/java | ForEach-Object FullName)
java -cp "out;lib/mysql-connector-j-8.4.0.jar" com.prakash.jdbc.Application
```

## Project structure

```text
src/main/java/com/prakash/jdbc/
├── Application.java   # Console menu and input validation
├── Database.java      # Connection settings and schema initialization
├── Student.java       # Domain model
└── StudentDao.java    # JDBC CRUD queries
```

## JDBC concepts demonstrated

- `DriverManager` and `Connection`
- `PreparedStatement` to prevent SQL injection
- `ResultSet` mapping
- Database transactions for write operations
- Try-with-resources for safe cleanup


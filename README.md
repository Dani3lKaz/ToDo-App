# ToDoApp

A robust and modern To-Do List application built with JavaFX, designed to help you manage your tasks effectively.

## Features

- **User Authentication**: Secure Login and Registration system to keep your data private.
- **Task Management**: Create, view, edit, and delete tasks with ease.
- **Project Organization**: Organize your tasks into specific projects.
- **Database Persistence**: Your data is safely stored in a generic relational SQL database (MySQL configuration provided).
- **Email Notifications**: Integration with Jakarta Mail for notifications (configured in dependencies).
- **Clean UI**: A user-friendly interface built with JavaFX controls and FXML.

## Technology Stack

- **Language**: [Java 25](https://openjdk.org/projects/jdk/25/)
- **UI Framework**: [JavaFX 21](https://openjfx.io/)
- **Build Tool**: [Maven](https://maven.apache.org/)
- **Database**: [MySQL 8.0](https://www.mysql.com/)
- **Testing**: [JUnit 5](https://junit.org/junit5/) & [H2 Database](https://www.h2database.com/) (for test scope)

## Prerequisites

Before you begin, ensure you have the following installed on your system:

- **Java Development Kit (JDK) 25** or higher.
- **Maven** build tool.
- **MySQL Server** (local or remote instance).

## Configuration

1.  **Database Setup**:
    Create a database named `todo_app` in your MySQL server. You can do this via command line or a GUI tool like MySQL Workbench.
    ```sql
    CREATE DATABASE todo_app;
    ```

2.  **Application Config**:
    Locate the configuration file at `src/main/resources/db.properties`.
    Update the file with your local database credentials:

    ```properties
    url=jdbc:mysql://localhost:3306/todo_app
    user=YOUR_USERNAME_HERE
    password=YOUR_PASSWORD_HERE
    ```

    > [!NOTE]
    > The default configuration comes with placeholder values. **You must update these to match your local MySQL setup for the application to connect successfully.**

## Installation & Running

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/Dani3lKaz/ToDo-App.git
    cd ToDo-App
    ```

2.  **Build the project**:
    ```bash
    mvn clean install
    ```

3.  **Run the application**:
    Use the Maven JavaFX plugin to start the app:
    ```bash
    mvn javafx:run
    ```

## Development

The source code is organized as follows:
- `src/main/java/ToDoApp`: Contains the main application source code.
    - `dao`: Data Access Objects for database interactions.
    - `model`: Data models/entities.
    - `ui`: UI controllers and view management.
    - `utils`: Utility classes (e.g., database connection helper).
- `src/main/resources`: Contains configuration files (`db.properties`), images, and styles (`style.css`).

## License

This project is open-source.

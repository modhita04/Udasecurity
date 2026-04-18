# Java Application Deployment Project

## Overview

This project is a modular Java application that implements a home security system with image analysis capabilities. The system evaluates sensor activity and image data to determine alarm states. The application has been refactored into a multi-module Maven project and enhanced to meet all functional, testing, and build requirements.

## Project Structure

The application is organized into two main modules:

* **SecurityService**: Handles alarm logic, sensor state changes, and system status.
* **ImageService**: Responsible for image analysis, including optional integration with AWS Rekognition.

A parent project manages shared configuration and dependencies.

## Features

* Fully functional application that compiles and runs without errors
* Graphical user interface supporting all required operations
* Modular architecture with clear separation of concerns
* Alarm system logic based on sensor input and image analysis
* Optional integration with AWS Rekognition for real image processing

## Build and Run

### Compile the Project

```bash
mvn compile
```

### Run Unit Tests

```bash
mvn test
```

### Package as Executable JAR

```bash
mvn package
```

### Run the Application

```bash
java -jar target/<your-jar-name>.jar
```

## Code Quality

* All high-priority issues identified by SpotBugs have been resolved
* Static analysis report is generated during the build process
* Code follows standard Java conventions and best practices

To generate the SpotBugs report:

```bash
mvn install site
```

## Maven Configuration

* Dependencies are correctly scoped across modules
* Shared dependencies are maintained in the parent POM
* No duplicate dependencies across modules
* Maven Surefire Plugin is configured to support unit testing with Mockito

## Module System

* Each module includes a module descriptor (`module-info.java`)
* Dependencies and required packages are explicitly declared
* Necessary packages are opened for reflection where required (e.g., Gson serialization)

## Unit Testing

* Comprehensive unit tests are implemented for the SecurityService
* All application requirements are covered by tests 
* Mockito is used to isolate dependencies
* Parameterized tests are used where applicable
* Test names clearly describe behavior, conditions, and expected outcomes

## Key Testing Practices

* Full coverage of all non-trivial methods
* All branching conditions are tested
* Dependencies such as ImageService and Repository are mocked
* Tests do not rely on external systems

## Optional Enhancements

* AWS Rekognition integration for image analysis
* Integration testing using a fake repository implementation
* Improved test coverage through end-to-end scenarios

## Conclusion

This project demonstrates strong adherence to modular design, testing practices, and build management using Maven. It satisfies all functional and non-functional requirements, with a focus on maintainability, scalability, and code quality.

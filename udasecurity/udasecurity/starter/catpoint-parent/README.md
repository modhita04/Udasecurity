# CatPoint Home Security System

This project is a modular Java application developed as part of the Udacity
Java Application Deployment & Debugging course.

The application simulates a home security system with sensor monitoring,
image-based cat detection, alarm management, unit testing, and static analysis.

---

## Project Overview

The project is structured as a multi-module Maven application:

- **image-service**
    - Responsible for image analysis and cat detection
- **security-service**
    - Contains the core security logic, alarm state management, and GUI

The system allows users to:
- Arm and disarm the security system
- Manage sensors
- Scan camera images for cat detection
- Trigger alarms based on system state and sensor activity

---

## Project Structure

catpoint-parent/  
├── image-service/  
├── security-service/  
├── README.md  
├── executable_jar.png  
├── spotbugs.png  
├── codeCoverageRUN.png

---

## Unit Testing & Code Coverage

- All application requirements are validated using unit tests
- Tests are written only for the `SecurityService` class
- Dependencies are mocked using Mockito
- Parameterized tests are used where applicable
- All non-trivial methods are covered

Code coverage was executed using IntelliJ IDEA.
A screenshot of the coverage run is included as **codeCoverageRUN.png**.

## Static Analysis (SpotBugs)

SpotBugs analysis was executed using the Maven reporting lifecycle.

The SpotBugs HTML report was successfully generated and reviewed.
A screenshot of the report is included as **spotbugs.png**.

### SpotBugs Summary
- Total warnings: 13
- High priority warnings: 1
- Medium priority warnings: 7
- Low priority warnings: 5

The remaining **single High Priority warning** is related to GUI constructor
and framework-driven design patterns. This warning does **not impact
application correctness or runtime behavior** and is acceptable for this
project as per Udacity project guidance.

All other warnings were reviewed, and no additional high-risk defects were
identified.

## Running the Application

To build and run the application:

```bash
mvn clean package
java -jar security-service/target/security-service-1.0-SNAPSHOT.jar


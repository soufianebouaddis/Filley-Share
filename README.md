# filley

Web application to share files (upload and download) across devices on the same network — smoother than using Bluetooth XD.

This repository is a Maven-based Java web application that uses Spring Boot (3.5.7) and Vaadin (24.9.4) for the UI. It provides an easy way to upload and download files over the local network from different devices. The project uses H2 by default for development and includes file upload configuration and a Vaadin frontend under `src/main/frontend`.

## Quick facts

- GroupId: `os.org`
- ArtifactId: `filley`
- Version: `0.0.1-SNAPSHOT`
- Java: 21
- Spring Boot: 3.5.7
- Vaadin: 24.9.4

## What you'll find here

- `src/main/java` — application sources (main class: `os.org.filley.FilleyApplication`)
- `src/main/resources/application.yaml` — runtime configuration
- `src/main/frontend` — Vaadin frontend scaffolding and index
- `uploads/` — configured upload directory (created/used at runtime)
- `pom.xml` — Maven build and dependency management

## Quickstart (development)

Requirements: JDK 21 and Maven installed.

1. Build (compile + package):

```bash
mvn -DskipTests package
```

2. Run the app from Maven (dev):

```bash
mvn spring-boot:run
```

Or run the generated jar:

```bash
mvn -DskipTests package
java -jar target/filley-0.0.1-SNAPSHOT.jar
```

Default server port is 8089 (see `src/main/resources/application.yaml`).

## Vaadin frontend

- The Vaadin application entry is managed via `src/main/frontend` and the Vaadin Spring Boot starter. During production builds the `production` Maven profile in `pom.xml` runs the Vaadin frontend build steps (`vaadin-maven-plugin`) to prepare a production bundle.

Build production artifacts (includes frontend build):

```bash
mvn -Pproduction -DskipTests package
```

To enable Vaadin production mode at runtime, set the property `vaadin.productionMode=true` in the configuration or pass it on the command line.

## Configuration highlights

- Server port: `server.port: 8089`
- H2 (in-memory): configured at `jdbc:h2:mem:fileshare` with the console enabled at `/h2-console`
- File upload settings (limits) and upload directory:

  - `spring.servlet.multipart.max-file-size` — 100MB
  - `spring.servlet.multipart.max-request-size` — 100MB
  - `file.upload-dir` — `./uploads` (relative to project root; ensure writable)

All values are in `src/main/resources/application.yaml` and can be overridden via environment variables or command-line `--` properties.


There are sample tests under `src/test/java` (e.g. `FilleyApplicationTests`).

## Project structure

Top-level layout:

```
pom.xml
README.md
src/main/java/...             # Java sources
src/main/resources/...        # application.yaml and static resources
src/main/frontend/...         # Vaadin frontend
uploads/                      # runtime uploads directory
target/                       # build output
```

## Notes & tips

- The application uses H2 by default for ease of development. For production, switch to a persistent datasource and update `spring.datasource` properties.
- If you see Vaadin dev-server artifacts in `target/`, these are produced during dev runs or frontend builds.

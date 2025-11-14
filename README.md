# PII Log Scanner

A Java 21 CLI application to scan log files for PII data using configurable regex patterns.

## Features

- Configurable log file path and PII pattern config file (YAML/JSON)
- Reads log file line-by-line and reports PII hits
- Easy to extend with new patterns

## Usage

### Build

```cli
mvn clean package
```

### Run

```java
java -jar target/pii-log-scanner-1.0-SNAPSHOT.jar --log /path/to/logfile.log --config /path/to/patterns.yaml
```

### Example config (YAML)

```yaml
patterns:
  - "\\b\d{3}-\d{2}-\d{4}\\b" # SSN
  - "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}" # Email
```

### Example config (JSON)

```json
{
  "patterns": [
    "\\b\d{3}-\d{2}-\d{4}\\b",
    "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"
  ]
}
```

## Requirements

- Java 21
- Maven

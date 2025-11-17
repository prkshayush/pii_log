
# PII Log Scanner

A Java CLI application to scan log files for PII data using configurable regex patterns or keywords. Supports scanning all log files in a directory and saving results in a configurable output directory.

## Features

- Scan a single log file or all `.log` files in a directory
- Configurable output directory; results saved as `detected-(original log file name).log`
- Supports both regex patterns (YAML/JSON) and keyword lists (YAML/JSON)
- Easy to extend with new patterns or keywords

## Usage

### Build

```bash
mvn clean package
```

### Run (Single Log File)

```bash
java -jar target/pii-log-scanner-1.0-SNAPSHOT.jar --log /path/to/logfile.log --config /path/to/patterns.yaml --output /path/to/outputdir
```

### Run (Directory of Log Files)

```bash
java -jar target/pii-log-scanner-1.0-SNAPSHOT.jar --log /path/to/logs/ --config /path/to/patterns.yaml --output /path/to/outputdir
```

### Run with Keyword List

```bash
java -jar target/pii-log-scanner-1.0-SNAPSHOT.jar --log /path/to/logs/ --config /path/to/keywords.yaml --output /path/to/outputdir
```

### Example patterns config (YAML)

```yaml
patterns:
  - "\\b\d{3}-\d{2}-\d{4}\\b" # SSN
  - "[A-Z]{5}[0-9]{4}[A-Z]" # PAN
  - "account\\s*no\\s*[:=]?\\s*\\d{9,18}" # Account Number
```

### Example keywords config (YAML)

```yaml
keywords:
  - password
  - passwrd
  - decodePassword
  - cred
  - account no
  # ...add more as needed
```

## Requirements

- Java 21
- Maven

## Output

- Results are saved in the output directory as `detected-(original log file name).log` for each scanned log file.

## Testing

1. Place sample log files in a directory (e.g., `/path/to/logs/`).
2. Prepare your config file (`patterns.yaml` or `keywords.yaml`).
3. Run the scanner as shown above.
4. Check the output directory for results.

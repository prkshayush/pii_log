package com.piilog;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.io.*;
import java.util.*;
import java.util.regex.*;

@Command(name = "pii-log-scanner", mixinStandardHelpOptions = true, version = "1.0",
        description = "Scans log files for PII data using configurable regex patterns.")
public class PiiLogScanner implements Runnable {
    @Option(names = {"-l", "--log"}, description = "Path to log file", required = true)
    private String logFilePath;

    @Option(names = {"-c", "--config"}, description = "Path to config file (YAML/JSON)", required = true)
    private String configFilePath;

    @Option(names = {"-o", "--output"}, description = "Path to output PII log file", required = false)
    private String outputFilePath = "pii-detected.log";

    public static void main(String[] args) {
        int exitCode = new CommandLine(new PiiLogScanner()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        boolean piiFound = false;
        try {
            List<Pattern> patterns = ConfigLoader.loadPatterns(configFilePath);
            piiFound = scanLogFileAndSavePII(logFilePath, patterns, outputFilePath);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("FLAG: " + (piiFound ? 1 : 0));
        System.exit(piiFound ? 1 : 0);
    }

    private boolean scanLogFileAndSavePII(String logPath, List<Pattern> patterns, String outputPath) throws IOException {
        boolean piiDetected = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(logPath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                for (Pattern pattern : patterns) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        writer.write("Line " + lineNum + ": " + line);
                        writer.newLine();
                        piiDetected = true;
                        break;
                    }
                }
            }
        }
        return piiDetected;
    }
}

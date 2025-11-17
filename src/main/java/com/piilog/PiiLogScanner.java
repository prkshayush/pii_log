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
    @Option(names = {"-l", "--log"}, description = "Path to log file or directory", required = true)
    private String logPath;

    @Option(names = {"-c", "--config"}, description = "Path to config file (YAML/JSON)", required = true)
    private String configFilePath;

    @Option(names = {"-o", "--output"}, description = "Path to output directory", required = false)
    private String outputDirPath = "pii-detected-logs";

    public static void main(String[] args) {
        int exitCode = new CommandLine(new PiiLogScanner()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        boolean piiFound = false;
        try {
            File logFileOrDir = new File(logPath);
            File outputDir = new File(outputDirPath);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            List<File> logFiles = new ArrayList<>();
            if (logFileOrDir.isDirectory()) {
                File[] files = logFileOrDir.listFiles((dir, name) -> name.endsWith(".log"));
                if (files != null) {
                    logFiles.addAll(Arrays.asList(files));
                }
            } else {
                logFiles.add(logFileOrDir);
            }
            if (logFiles.isEmpty()) {
                throw new IllegalArgumentException("No log files found in the specified path.");
            }
            if (configFilePath.contains("keywords")) {
                List<String> keywords = ConfigLoader.loadKeywords(configFilePath);
                for (File logFile : logFiles) {
                    String outputFileName = "detected-" + logFile.getName();
                    File outputFile = new File(outputDir, outputFileName);
                    boolean found = scanLogFileWithKeywords(logFile.getAbsolutePath(), keywords, outputFile.getAbsolutePath());
                    piiFound = piiFound || found;
                }
            } else {
                List<Pattern> patterns = ConfigLoader.loadPatterns(configFilePath);
                for (File logFile : logFiles) {
                    String outputFileName = "detected-" + logFile.getName();
                    File outputFile = new File(outputDir, outputFileName);
                    boolean found = scanLogFileAndSavePII(logFile.getAbsolutePath(), patterns, outputFile.getAbsolutePath());
                    piiFound = piiFound || found;
                }
            }
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

    private boolean scanLogFileWithKeywords(String logPath, List<String> keywords, String outputPath) throws IOException {
        boolean piiDetected = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(logPath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                for (String keyword : keywords) {
                    if (line.toLowerCase().contains(keyword.toLowerCase())) {
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

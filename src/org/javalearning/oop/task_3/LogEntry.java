package org.javalearning.oop.task_3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;

enum HttpMethod {
    GET, POST, PUT, DELETE
}

public class LogEntry {

    private final String ipAddr;
    private final LocalDateTime time;
    private final HttpMethod method;
    private final String path;
    private final int responseCode;
    private final int responseSize;
    private final String referer;
    private final String userAgent;

    public LogEntry(String logLine) {

        String[] parts = logLine.split(" ");

        this.ipAddr = parts[0];
        System.out.println("IP Address: " + ipAddr);

        String dateTimeStr = parts[3].substring(1) + " " + parts[4].substring(0, 5);
        this.time = LocalDateTime.parse(dateTimeStr, java.time.format.DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH));
        System.out.println("Time: " + time);

        this.method = HttpMethod.valueOf(parts[5].substring(1));
        System.out.println("Method: " + method);

        this.path = parts[6];
        System.out.println("Path: " + path);

        this.responseCode = Integer.parseInt(parts[8]);
        System.out.println("Response Code: " + responseCode);

        this.responseSize = Integer.parseInt(parts[9]);
        System.out.println("Response Size: " + responseSize);

        this.referer = parts[10].equals("-") ? "" : parts[10].substring(1, parts[10].length() - 1);
        System.out.println("Referer: " + referer);

        StringBuilder userAgentBuilder = new StringBuilder();
        for (int i = 11; i < parts.length; i++) {
            userAgentBuilder.append(parts[i]).append(" ");
        }
        String userAgentStr = userAgentBuilder.toString().trim();
        this.userAgent = userAgentStr.equals("\"-\"") ? "-" : userAgentStr;
        System.out.println("User Agent: " + userAgent);
    }

    public LogEntry(String ipAddr, LocalDateTime time, HttpMethod method, String path, int responseCode, int responseSize, String referer, String userAgent) {
        this.ipAddr = ipAddr;
        this.time = time;
        this.method = method;
        this.path = path;
        this.responseCode = responseCode;
        this.responseSize = responseSize;
        this.referer = referer;
        this.userAgent = userAgent;
    }

    public static void parseLogFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                new LogEntry(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getResponseSize() {
        return responseSize;
    }

    public String getReferer() {
        return referer;
    }

    public String getUserAgent() {
        return userAgent;
    }
    public static void main(String[] args) {

        parseLogFile("src/access.log");
    }
}

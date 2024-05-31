package org.javalearning.stream.api.task_2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

// Enum для типов HTTP-запросов
enum HttpMethod {
    GET, POST, PUT, DELETE
}

// Класс org.test.LogEntry для представления строки лога
public class LogEntry {
    // Поля класса org.test.LogEntry
    private final String ipAddr;
    private final LocalDateTime time;
    private final HttpMethod method;
    private final String path;
    private final int responseCode;
    private final int responseSize;
    private final String referer;
    private final String userAgent;
    private final String operatingSystem;

    // Конструктор для разбора строки и установки значений свойств
    public LogEntry(String logLine) {
        // Пример разбора строки logLine и установки значений:
        String[] parts = logLine.split(" ");

        // Парсинг IP-адреса
        this.ipAddr = parts[0];
        System.out.println("IP Address: " + ipAddr);

        // Парсинг даты и времени
        String dateTimeStr = parts[3].substring(1) + " " + parts[4].substring(0, 5);
        this.time = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH));
        System.out.println("Time: " + time);

        // Парсинг метода запроса
        this.method = HttpMethod.valueOf(parts[5].substring(1));
        System.out.println("Method: " + method);

        // Парсинг пути запроса
        this.path = parts[6];
        System.out.println("Path: " + path);

        // Парсинг кода ответа
        this.responseCode = Integer.parseInt(parts[8]);
        System.out.println("Response Code: " + responseCode);

        // Парсинг размера ответа
        this.responseSize = Integer.parseInt(parts[9]);
        System.out.println("Response Size: " + responseSize);

        // Парсинг referer
        this.referer = parts[10].equals("-") ? "" : parts[10].substring(1, parts[10].length() - 1);
        System.out.println("Referer: " + referer);

        // Парсинг User-Agent
        StringBuilder userAgentBuilder = new StringBuilder();
        for (int i = 11; i < parts.length; i++) {
            userAgentBuilder.append(parts[i]).append(" ");
        }
        String userAgentStr = userAgentBuilder.toString().trim();
        this.userAgent = userAgentStr.equals("\"-\"") ? "-" : userAgentStr;
        System.out.println("User Agent: " + userAgent);

        this.operatingSystem = parseBrowserSystem(userAgentStr);
        System.out.println("Browser: " + operatingSystem);

    }

    public LogEntry(String ipAddr, LocalDateTime time, HttpMethod method, String path, int responseCode, int responseSize, String referer, String userAgent, String operatingSystem) {
        this.ipAddr = ipAddr;
        this.time = time;
        this.method = method;
        this.path = path;
        this.responseCode = responseCode;
        this.responseSize = responseSize;
        this.referer = referer;
        this.userAgent = userAgent;
        this.operatingSystem = operatingSystem;
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

    static String parseBrowserSystem(String userAgent) {
        if (userAgent.contains("Edge")) {
            return "Edge";
        } else if (userAgent.contains("Firefox")) {
            return "Firefox";
        } else if (userAgent.contains("Chrome")) {
            return "Chrome";
        } else if (userAgent.contains("Opera")) {
            return "Opera";
        } else {
            return "Other";
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
    public String getOperatingSystem() {
        return operatingSystem;
    }

    public static void main(String[] args) {
        parseLogFile("src/access.log");
    }
}

package org.javalearning.stream.api.task_2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class UserAgent {

    private final String ipAddr;
    private final LocalDateTime time;
    private final int responseCode;
    private final String userAgent;
    private final String operatingSystem;
    private final String browser;
    private final boolean isBot;
    private final String referer;

    // Конструктор для разбора строки User-Agent и установки значений свойств
    public UserAgent(String userAgentString) {

        String[] parts = userAgentString.split(" ");

        this.ipAddr = parts[0];
//        System.out.println("IP Address: " + ipAddr);

        // Парсинг даты и времени
        String dateTimeStr = parts[3].substring(1) + " " + parts[4].substring(0, 5);
        this.time = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH));
//        System.out.println("Time: " + time);

        // Парсинг кода ответа
        this.responseCode = Integer.parseInt(parts[8]);
//        System.out.println("Response Code: " + responseCode);

        // Парсинг User-Agent
        StringBuilder userAgentBuilder = new StringBuilder();
        for (int i = 11; i < parts.length; i++) {
            userAgentBuilder.append(parts[i]).append(" ");
        }
        String userAgentStr = userAgentBuilder.toString().trim();
        this.userAgent = userAgentStr.equals("\"-\"") ? "-" : userAgentStr;
//        System.out.println("User Agent: " + userAgent);

        this.operatingSystem = parseOsSystem(userAgentStr);
//        System.out.println("Operating System: " + operatingSystem);

        this.browser = parseBrowserSystem(userAgentStr);
//        System.out.println("Browser: " + browser);

        this.isBot = userAgentString.toLowerCase().contains("bot");
//        System.out.println("Бот: " + isBot);

        // Парсинг referer
        this.referer = parts[10].equals("-") ? "" : parts[10].substring(1, parts[10].length() - 1);
        System.out.println("Referer: " + referer);
    }

    public UserAgent(String ipAddr,LocalDateTime time,int responseCode,String userAgent, String operatingSystem,String browser,Boolean isBot,String referer) {
        this.ipAddr = ipAddr;
        this.time = time;
        this.responseCode = responseCode;
        this.userAgent = userAgent;
        this.operatingSystem = operatingSystem;
        this.browser = browser;
        this.isBot = isBot;
        this.referer = referer;
    }
    public static int[] parseLogFile(String filename) {
        int botCount = 0;
        int userCount = 0;
        int totalLines =0;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                UserAgent userAgent = new UserAgent(line);

                if (userAgent != null) {
                    totalLines++;
                    if (userAgent.getIsBot()) {
                        botCount++;
                    } else {
                        userCount++;
                    }
                }

                if (line.length() > 1024) {
                    throw new LineTooLongException("Строка в файле превышает 1024 символа");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new int[]{totalLines, botCount, userCount};
    }

    public static boolean isBot(String userAgent) {
        return userAgent.toLowerCase().contains("bot");
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

    static String parseOsSystem(String userAgent) {
        if (userAgent.contains("Windows")) {
            return "Windows";
        } else if (userAgent.contains("Mac OS")) {
            return "Mac OS";
        } else if (userAgent.contains("Linux")) {
            return "Linux";
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

    public int getResponseCode() {
        return responseCode;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }
    public String getBrowser() {
        return browser;
    }
    public String getReferer() {
        return referer;
    }
    public boolean getIsBot() {
        return isBot;
    }

    public static void main(String[] args) {

        String filename = "src/access.log";
        int[] requestCounts = parseLogFile(filename);

        int totalLines = requestCounts[0];
        int botCount = requestCounts[1];
        int userCount = requestCounts[2];

        System.out.println("\nВсего запросов в логе: " + totalLines);
        System.out.println("Количество запросов от ботов: " + botCount);
        System.out.println("Количество запросов от пользователей: " + userCount);
    }
}

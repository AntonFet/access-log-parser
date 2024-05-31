package org.javalearning.stream.api.task_1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.javalearning.stream.api.task_1.UserAgent.parseBrowserSystem;
import static org.javalearning.stream.api.task_1.UserAgent.parseOsSystem;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private int errorRequestsCount;
    private HashMap<String, Integer> userVisitsCount;
    private HashMap<String, HashSet<String>> userIPs;

    public Statistics() {
        totalTraffic = 0;
        minTime = null;
        maxTime = null;
        errorRequestsCount = 0;
        userVisitsCount = new HashMap<>();
        userIPs = new HashMap<>();
    }

    public void addEntry(UserAgent entry) {

        if (entry.getResponseCode() >= 400 && entry.getResponseCode() < 600) {
            errorRequestsCount++;
        }

        String ip = entry.getIpAddr();
        userVisitsCount.put(ip, userVisitsCount.getOrDefault(ip, 0) + 1);
        userIPs.putIfAbsent(ip, new HashSet<>());
        userIPs.get(ip).add(entry.getUserAgent());


        if (minTime == null || entry.getTime().isBefore(minTime)) {
            minTime = entry.getTime();
        }
        if (maxTime == null || entry.getTime().isAfter(maxTime)) {
            maxTime = entry.getTime();
        }

    }

    public double getAverageErrorRequestsPerHour() {
        if (minTime == null || maxTime == null) {
            return 0;
        }

        long hours = java.time.Duration.between(minTime, maxTime).toHours();
        return (double) errorRequestsCount / hours;
    }

    public double getAverageVisitsPerHour() {
        if (minTime == null || maxTime == null) {
            return 0;
        }

        long hours = java.time.Duration.between(minTime, maxTime).toHours();
        int userRequestsCount = 0;

        for (Map.Entry<String, HashSet<String>> entry : userIPs.entrySet()) {
            String ip = entry.getKey();
            if (!isIPBot(ip)) {
                userRequestsCount += userVisitsCount.get(ip);
            }
        }

        return (double) userRequestsCount / hours;
    }

    public double getAverageVisitsPerUser() {
        if (userIPs.isEmpty()) {
            return 0;
        }

        return (double) userVisitsCount.size() / userIPs.size();
    }

    private boolean isIPBot(String ip) {
        HashSet<String> userAgents = userIPs.get(ip);
        for (String userAgent : userAgents) {
            if (UserAgent.isBot(userAgent)) {
                return true;
            }
        }
        return false;
    }

    public static List<UserAgent> parseLogFile(String filename) {
        List<UserAgent> entries = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {

                if (line.length() > 1024) {
                    throw new LineTooLongException("Строка в файле превышает 1024 символа");
                }

                String[] parts = line.split(" ");
                String ipAddr = parts[0];

                String dateTimeStr = parts[3].substring(1) + " " + parts[4].substring(0, 5);
                LocalDateTime time = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH));

                int responseCode = Integer.parseInt(parts[8]);

                StringBuilder userAgentBuilder = new StringBuilder();
                for (int i = 11; i < parts.length; i++) {
                    userAgentBuilder.append(parts[i]).append(" ");
                }
                String userAgentStr = userAgentBuilder.toString().trim();
                String userAgent = userAgentStr.equals("\"-\"") ? "-" : userAgentStr;

                String operatingSystem = parseOsSystem(userAgentStr);

                String browser = parseBrowserSystem(userAgentStr);

                Boolean isBot = userAgentStr.toLowerCase().contains("bot");

                UserAgent entry = new UserAgent(ipAddr,time,responseCode,userAgent,operatingSystem,browser,isBot);
                entries.add(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public static void main(String[] args) {
        Statistics stats = new Statistics();
        List<UserAgent> entries = parseLogFile("src/access.log");
        for (UserAgent entry : entries) {
            stats.addEntry(entry);
        }

        System.out.printf("\nСреднее количество посещений сайта за час: %.2f\n", stats.getAverageVisitsPerHour());
        System.out.printf("Среднее количество ошибочных запросов в час: %.2f\n", stats.getAverageErrorRequestsPerHour());
        System.out.printf("Среднее количество посещений одним пользователем: %.2f\n", stats.getAverageVisitsPerUser());
    }
}

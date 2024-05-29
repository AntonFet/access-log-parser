package org.task_3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private HashSet<String> pages;
    private static HashMap<String, Integer> osStats;

    public Statistics() {
        totalTraffic = 0;
        minTime = null;
        maxTime = null;
        pages = new HashSet<>();
        osStats = new HashMap<>();
    }

    public void addEntry(LogEntry entry) {
        if (totalTraffic + entry.getResponseSize() < 0) {
            throw new ArithmeticException("Переполнение totalTraffic");
        }

        totalTraffic += entry.getResponseSize();

        if (entry.getResponseCode() == 200) {
            pages.add(entry.getPath());
        }

        String os = extractOSFromUserAgent(entry.getOperatingSystem());
        if (os != null) {
            osStats.put(os, osStats.getOrDefault(os, 0) + 1);
        }

        if (minTime == null || entry.getTime().isBefore(minTime)) {
            minTime = entry.getTime();
        }
        if (maxTime == null || entry.getTime().isAfter(maxTime)) {
            maxTime = entry.getTime();
        }
    }

    public double getTrafficRate() {
        if (minTime == null || maxTime == null) {
            return 0;
        }

        long hours = java.time.Duration.between(minTime, maxTime).toHours();
        double trafficInMegabytes = (double) totalTraffic / (1024 * 1024);
        if (hours == 0) {
            return 0;
        }

        return (double) trafficInMegabytes / hours;
    }

    public HashSet<String> getAllPages() {
        return pages;
    }

    public HashMap<String, Double> getOSStatistics() {
        HashMap<String, Double> osPercentage = new HashMap<>();
        int totalOSCount = osStats.values().stream().mapToInt(Integer::intValue).sum();

        for (Map.Entry<String, Integer> entry : osStats.entrySet()) {
            double percentage = (double) entry.getValue() / totalOSCount;
            osPercentage.put(entry.getKey(), percentage);
        }

        return osPercentage;
    }

    private String extractOSFromUserAgent(String userAgent) {
        String[] parts = userAgent.split("\\s+");
        return parts.length > 0 ? parts[0] : null;
    }

    public static List<LogEntry> parseLogFile(String filename) {
        List<LogEntry> entries = new ArrayList<>();
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

                HttpMethod method = HttpMethod.valueOf(parts[5].substring(1));
                String path = parts[6];
                int responseCode = Integer.parseInt(parts[8]);
                int responseSize = Integer.parseInt(parts[9]);
                String referer = parts[10].equals("-") ? "" : parts[10].substring(1, parts[10].length() - 1);

                StringBuilder userAgentBuilder = new StringBuilder();
                for (int i = 11; i < parts.length; i++) {
                    userAgentBuilder.append(parts[i]).append(" ");
                }
                String userAgentStr = userAgentBuilder.toString().trim();
                String userAgent = userAgentStr.equals("\"-\"") ? "-" : userAgentStr;

                String operatingSystem = LogEntry.parseOperatingSystem(userAgentStr);

                LogEntry entry = new LogEntry(ipAddr,time,method,path,responseCode,responseSize,referer,userAgent,operatingSystem);
                entries.add(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public static void main(String[] args) {
        Statistics stats = new Statistics();
        List<LogEntry> entries = parseLogFile("src/access.log");
        for (LogEntry entry : entries) {
            stats.addEntry(entry);
        }

        System.out.printf("\nСредний объем трафика в час (MB): %.2f\n", stats.getTrafficRate());

        HashSet<String> allPages = stats.getAllPages();
        System.out.println("\nСписок всех существующих страниц сайта:\n");
        for (String page : allPages) {
            System.out.println(page);
        }

        System.out.println("\nСтатистика операционных систем пользователей сайта:\n");
        for (Map.Entry<String, Double> entry : stats.getOSStatistics().entrySet()) {
            System.out.printf("%s: %.2f%%\n", entry.getKey(), entry.getValue() * 100);
        }

    }
}

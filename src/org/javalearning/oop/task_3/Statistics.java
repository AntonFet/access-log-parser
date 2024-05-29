package org.javalearning.oop.task_3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;

    public Statistics() {
        totalTraffic = 0;
        minTime = null;
        maxTime = null;
    }

    public void addEntry(LogEntry entry) {
        if (totalTraffic + entry.getResponseSize() < 0) {
            throw new ArithmeticException("Переполнение totalTraffic");
        }

        totalTraffic += entry.getResponseSize();

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
                LocalDateTime time = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH)); // Предполагается, что формат времени корректный

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
                LogEntry entry = new LogEntry(ipAddr,time,method,path,responseCode,responseSize,referer,userAgent);
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
    }
}

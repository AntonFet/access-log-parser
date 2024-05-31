package org.javalearning.stream.api.task_2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.javalearning.stream.api.task_2.UserAgent.parseBrowserSystem;
import static org.javalearning.stream.api.task_2.UserAgent.parseOsSystem;
import static org.javalearning.stream.api.task_2.UserAgent.isBot;


public class Statistics {
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private int errorRequestsCount;
    private HashMap<String, Integer> userVisitsCount;
    private HashMap<String, HashSet<String>> userIPs;
    private HashMap<Integer, Integer> visitsPerSecond;
    private HashSet<String> referringDomains;
    private int maxVisitsPerUser;

    public Statistics() {

        minTime = null;
        maxTime = null;
        errorRequestsCount = 0;
        userVisitsCount = new HashMap<>();
        userIPs = new HashMap<>();
        visitsPerSecond = new HashMap<>();
        referringDomains = new HashSet<>();
        maxVisitsPerUser = 0;
    }

    public void addEntry(UserAgent entry) {

        if (entry.getResponseCode() >= 400 && entry.getResponseCode() < 600) {
            errorRequestsCount++;
        }

        String ip = entry.getIpAddr();

        if (!isBot(entry.getUserAgent())) {
            userVisitsCount.put(ip, userVisitsCount.getOrDefault(ip, 0) + 1);
        }

        userIPs.putIfAbsent(ip, new HashSet<>());
        userIPs.get(ip).add(entry.getUserAgent());

        int second = entry.getTime().getSecond();

        if (!isBot(entry.getUserAgent())) {
            visitsPerSecond.put(second, visitsPerSecond.getOrDefault(second, 0) + 1);
        }

        String refererDomain = extractDomain(entry.getReferer());
        if (refererDomain != null) {
            referringDomains.add(refererDomain);
        }

        if (minTime == null || entry.getTime().isBefore(minTime)) {
            minTime = entry.getTime();
        }
        if (maxTime == null || entry.getTime().isAfter(maxTime)) {
            maxTime = entry.getTime();
        }

    }

    private String extractDomain(String referer) {
        if (referer != null && !referer.isEmpty()) {
            String[] parts = referer.split("/");
            if (parts.length >= 3) {
                return parts[2];
            }
        }
        return null;
    }

        public int getPeakVisitsPerSecond() {
        int peakVisits = 0;
        for (int visits : visitsPerSecond.values()) {
            if (visits > peakVisits) {
                peakVisits = visits;
            }
        }
        return peakVisits;
    }

    public List<String> getReferringDomains() {
        return new ArrayList<>(referringDomains);
    }

    public int getMaxVisitsPerUser() {
        for (Map.Entry<String, Integer> entry : userVisitsCount.entrySet()) {
           String ip = entry.getKey();
            if (!isIPBot(ip)) {
                int visits = entry.getValue();
                if (visits > maxVisitsPerUser) {
                    maxVisitsPerUser = visits;
                }
            }
        }
        return maxVisitsPerUser;
    }

    private boolean isIPBot(String ip) {
        HashSet<String> userAgents = userIPs.get(ip);
        for (String userAgent : userAgents) {
            if (UserAgent.isBot(userAgent)) {
                return false;
            }
        }
        return true;
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

                String referer = parts[10].equals("-") ? "" : parts[10].substring(1, parts[10].length() - 1);

                UserAgent entry = new UserAgent(ipAddr,time,responseCode,userAgent,operatingSystem,browser,isBot,referer);
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

        System.out.println("\nПиковая посещаемость сайта в секунду: " + stats.getPeakVisitsPerSecond());

        System.out.println("Список сайтов с ссылками на текущий сайт:");
        for (String domain : stats.getReferringDomains()) {
            System.out.println(domain);
        }

        System.out.printf("Максимальная посещаемость одним пользователем: %d\n", stats.getMaxVisitsPerUser());
    }
}

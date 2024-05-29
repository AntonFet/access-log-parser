package org.javalearning.collections.task_1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class UserAgent {
    private final String osType;
    private final String browser;

    public UserAgent(String userAgentString) {
        if (userAgentString.equals("-")) {
            this.osType = "-";
            this.browser = "-";
        } else {
            // Извлечение типа операционной системы
            if (userAgentString.contains("Windows")) {
                this.osType = "Windows";
            } else if (userAgentString.contains("Mac OS")) {
                this.osType = "macOS";
            } else if (userAgentString.contains("Linux")) {
                this.osType = "Linux";
            } else {
                this.osType = "Other";
            }
            System.out.println("OS: " + osType);

            // Извлечение браузера
            if (userAgentString.contains("Edge")) {
                this.browser = "Edge";
            } else if (userAgentString.contains("Firefox")) {
                this.browser = "Firefox";
            } else if (userAgentString.contains("Chrome")) {
                this.browser = "Chrome";
            } else if (userAgentString.contains("Opera")) {
                this.browser = "Opera";
            } else {
                this.browser = "Other";
            }
            System.out.println("Browser: " + browser);
        }
    }
    public static void parseLogFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                new UserAgent(line);
                if (line.length() > 1024) {
                    throw new LineTooLongException("Строка в файле превышает 1024 символа");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getOsType() {
        return osType;
    }

    public String getBrowser() {
        return browser;
    }
    public static void main(String[] args) {
        parseLogFile("src/access.log");
    }
}

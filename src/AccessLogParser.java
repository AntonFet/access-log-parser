import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AccessLogParser {
    private String filename;

    public AccessLogParser(String filename) {
        this.filename = filename;
    }

    public void parse() {
        int yandexbotCount = 0;
        int googlebotCount = 0;
        int totalRequests = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                totalRequests++;
                if (line.length() > 1024) {
                    throw new IllegalArgumentException("Строка в файле превышает 1024 символа");
                }

                String[] parts = line.split("\"");
                if (parts.length >= 6) {
                    String userAgentFragment = parts[5];
                    String[] userAgentParts = userAgentFragment.split("\\(");
                    if (userAgentParts.length >= 2) {
                        String botInfo = userAgentParts[1].split(";")[0].trim();
                        if (botInfo.equals("YandexBot")) {
                            yandexbotCount++;
                        } else if (botInfo.equals("Googlebot")) {
                            googlebotCount++;
                        }
                    }
                }
            }

            if (totalRequests > 0) {
                double yandexbotRatio = (double) yandexbotCount / totalRequests * 100;
                double googlebotRatio = (double) googlebotCount / totalRequests * 100;
                System.out.printf("Доля запросов от YandexBot: %.2f%%\n", yandexbotRatio);
                System.out.printf("Доля запросов от Googlebot: %.2f%%\n", googlebotRatio);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        AccessLogParser parser = new AccessLogParser("src/access.log");
        parser.parse();
    }
}

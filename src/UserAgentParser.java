
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class UserAgentParser {
    public static void main(String[] args) {
        String logFilePath = "src/access.log"; // Путь к файлу лога

        int totalLines = 0;
        int yandexbotCount = 0;
        int googlebotCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                totalLines++;

                // Извлечение User-Agent из строки лога
                String userAgent = extractUserAgent(line);

                // Проверка наличия YandexBot и GoogleBot в User-Agent
                if (userAgent != null) {
                    if (userAgent.contains("YandexBot")) {
                        yandexbotCount++;
                    } else if (userAgent.contains("Googlebot")) {
                        googlebotCount++;
                    }
                }
                if (line.length() > 1024) {
                    throw new LineTooLongException("Строка в файле превышает 1024 символа");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Вычисление процентного соотношения
        double yandexbotPercentage = ((double) yandexbotCount / totalLines) * 100;
        double googlebotPercentage = ((double) googlebotCount / totalLines) * 100;

        // Вывод результатов
        System.out.println("Всего строк: " + totalLines);
        System.out.printf("Доля запросов от YandexBot: %.2f%%\n", yandexbotPercentage);
        System.out.printf("Доля запросов от Googlebot: %.2f%%\n", googlebotPercentage);
    }

    private static String extractUserAgent(String line) {
        String[] parts = line.split("\"");
        if (parts.length >= 6) {
            return parts[5];
        }
        return null;
    }
}

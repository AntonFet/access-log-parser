import java.io.BufferedReader;
import java.io.FileReader;

public class Main {
    public static void main(String[] args) {
        try {
            FileReader fileReader = new FileReader("src/access.log");
            BufferedReader reader = new BufferedReader(fileReader);
            String line;

            int totalLines = 0;
            int maxLength = 0;
            int minLength = Integer.MAX_VALUE;

            while ((line = reader.readLine()) != null) {
                int length = line.length();
                totalLines++;
                if (length > maxLength) {
                    maxLength = length;
                }
                if (length < minLength) {
                    minLength = length;
                }

                if (length > 1024) {
                    throw new LineTooLongException("Line longer than 1024 characters encountered");
                }
            }

            System.out.println("Общее количество строк в файле: " + totalLines);
            System.out.println("Длина самой длинной строки в файле: " + maxLength);
            System.out.println("Длина самой короткой строки в файле: " + minLength);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        }
    }
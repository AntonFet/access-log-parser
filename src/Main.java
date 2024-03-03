import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        System.out.println("Введите первое число:");
        int firstNumber=new Scanner(System.in).nextInt();
        System.out.println("Введите второе число:");
        int secondNumber=new Scanner(System.in).nextInt();

        double sum=(double) firstNumber+secondNumber;
        double difference=(double) firstNumber-secondNumber;
        double multiplication=(double) firstNumber*secondNumber;
        double quotient=(double) firstNumber/secondNumber;

        System.out.println("Сумма: "+ sum);
        System.out.println("Разность: "+ difference);
        System.out.println("Произведение: "+ multiplication);
        System.out.println("Частное: "+ quotient);

        }
    }
import domain.Coffee;
import domain.NonCoffee;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        new CafeApp(scanner).run();
    }
}

class CafeApp {

    private final Scanner scanner;

    private final Coffee[] coffees = {
            new Coffee(1, "아메리카노",    10, 3000, 3, true),
            new Coffee(2, "라떼",         10, 4000, 3, false),
            new Coffee(3, "시그니처 라떼",  8, 4500, 3, true),
            new Coffee(4, "카푸치노",       5, 4500, 3, false),
            new Coffee(5, "에스프레소",    10, 2500, 1, false)
    };

    private final NonCoffee[] nonCoffees = {
            new NonCoffee(1, "오렌지", 10, 4000, 2, true),
            new NonCoffee(4, "수박",    5, 5500, 2, false),
            new NonCoffee(3, "딸기",    8, 5000, 3, false),
            new NonCoffee(2, "유자",   10, 4500, 3, true),
            new NonCoffee(5, "레몬",   10, 4000, 3, true)
    };

    // Scanner를 생성자로 주입받음
    public CafeApp(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run() {
        while (true) {
            System.out.println("음료를 선택해주세요(1. 커피, 2. 논커피): ");
            int category = scanner.nextInt();

            if (category == 1) {
                orderCoffee();
                break;
            } else if (category == 2) {
                orderNonCoffee();
                break;
            } else {
                System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private void orderCoffee() {
        System.out.println("커피 메뉴입니다.");
        for (Coffee coffee : coffees) {
            coffee.displayInfo();
            System.out.println("디카페인 가능: " + (coffee.isDecafAvailable() ? "O" : "X"));
        }

        System.out.println("주문할 커피 번호를 선택해주세요: ");
        int id = scanner.nextInt();

        if (id < 1 || id > coffees.length) {
            System.out.println("잘못된 번호입니다.");
            return;
        }

        Coffee orderCoffee = coffees[id - 1];

        while (true) {
            try {
                System.out.print("온도 선택 (1. 핫 / 2. 아이스): ");
                int temperature = scanner.nextInt();

                System.out.print("디카페인 여부 (true/false): ");
                boolean isDecaf = scanner.nextBoolean();

                orderCoffee.order(temperature, isDecaf);
                break;
            } catch (RuntimeException e) {
                System.out.println("[오류] " + e.getMessage() + " 다시 선택해주세요.");
            }
        }

        payment(orderCoffee.getPrice());
    }

    private void orderNonCoffee() {
        System.out.println("논커피 메뉴입니다.");
        for (NonCoffee nonCoffee : nonCoffees) {
            nonCoffee.displayInfo();
            System.out.println("에이드 가능: " + (nonCoffee.isAdeAvailable() ? "O" : "X"));
        }

        System.out.print("주문할 음료를 선택해주세요: ");
        int id = scanner.nextInt();

        if (id < 1 || id > nonCoffees.length) {
            System.out.println("잘못된 번호입니다.");
            return;
        }

        NonCoffee orderNonCoffee = nonCoffees[id - 1];

        while (true) {
            try {
                System.out.print("온도 선택 (1. 핫 / 2. 아이스): ");
                int temperature = scanner.nextInt();

                System.out.print("에이드 여부 (true/false): ");
                boolean isAde = scanner.nextBoolean();

                orderNonCoffee.order(temperature, isAde);
                break;
            } catch (RuntimeException e) {
                System.out.println("[오류] " + e.getMessage() + " 다시 선택해주세요.");
            }
        }

        payment(orderNonCoffee.getPrice());
    }

    private void payment(int price) {
        System.out.println("음료 금액은 " + price + "원입니다.");
        while (true) {
            System.out.print("지불할 금액을 입력해주세요: ");
            int input = scanner.nextInt();
            if (input >= price) {
                int change = input - price;
                if (change > 0) {
                    System.out.println("거스름돈: " + change + "원");
                }
                System.out.println("주문 완료!");
                break;
            } else {
                System.out.println("금액이 부족합니다. 다시 입력해주세요.");
            }
        }
    }
}
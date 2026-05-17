package domain;

public class Drink extends Product {
    private int tempOption;  // 1: hot only, 2: ice only, 3: both

    public Drink(int id, String name, int count, int price, int tempOption) {
        super(id, name, count, price);
        this.tempOption = tempOption;
    }

    public boolean isHotAvailable() {
        return tempOption == 1 || tempOption == 3;
    }

    public boolean isIceAvailable() {
        return tempOption == 2 || tempOption == 3;
    }

    public void order(int temperature) {
        if (!isInStock()) {
            throw new RuntimeException("재고가 없습니다.");
        }
        if (temperature == 1 && !isHotAvailable()) {
            throw new RuntimeException("핫 선택이 불가합니다.");
        }
        if (temperature == 2 && !isIceAvailable()) {
            throw new RuntimeException("아이스 선택이 불가합니다.");
        }
    }
}
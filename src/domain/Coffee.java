package domain;

public class Coffee extends Drink {
    private boolean isDecaf;

    public Coffee(int id, String name, int count, int price, int isHot, boolean isDecaf) {
        super(id, name, count, price, isHot);
        this.isDecaf = isDecaf;
    }

    public boolean isDecafAvailable() {
        return isDecaf;
    }

    public void order(int temperature, boolean isDecaf) {
        super.order(temperature);
        if (isDecaf && !isDecafAvailable()) {
            throw new RuntimeException("디카페인 선택이 불가합니다.");
        }
        decreaseCount();
        System.out.println("커피 주문 완료");
    }
}
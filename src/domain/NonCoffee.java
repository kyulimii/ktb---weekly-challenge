package domain;

public class NonCoffee extends Drink {
    private boolean isAde;

    public NonCoffee(int id, String name, int count, int price, int isHot, boolean isAde) {
        super(id, name, count, price, isHot);
        this.isAde = isAde;
    }

    public boolean isAdeAvailable() {
        return isAde;
    }

    public void order(int temperature, boolean isAde) {
        super.order(temperature);
        if (isAde && temperature == 1) {
            throw new RuntimeException("에이드는 핫 선택이 불가합니다.");
        }
        if (isAde && !isAdeAvailable()) {
            throw new RuntimeException("에이드 선택이 불가합니다.");
        }
        decreaseCount();
        if (isAde) {
            System.out.println(getName() + " 에이드 주문 완료");
        } else {
            System.out.println(getName() + " 주스 주문 완료");
        }
    }
}

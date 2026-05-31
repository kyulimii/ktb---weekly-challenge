package domain;

public abstract class Product {
    private int id;
    private String name;
    private int count;
    private int price;

    public Product(int id, String name, int count, int price) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.price = price;
    }

    public int getPrice() { return price; }
    public String getName() { return name; }

    public void displayInfo() {
        System.out.print("메뉴 번호: " + id + ", 이름: " + name + ", 가격: " + price + "원, ");
    }

    public boolean isInStock() {
        return count > 0; // 재고가 0 초과일 때만 True
    }

    public void decreaseCount() {
        count--;
    }
}
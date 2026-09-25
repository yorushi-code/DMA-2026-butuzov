package dev.yorushi.dma.task4.logic;

/** Pricing of the pizza order (P22). */
public final class Orders {

    private Orders() {
    }

    public enum Size {
        SMALL(450), MEDIUM(650), LARGE(850);

        public final int price;

        Size(int price) {
            this.price = price;
        }
    }

    public static final int DELIVERY = 150;
    /** Orders from this amount are delivered free. */
    public static final int FREE_DELIVERY_FROM = 1000;

    public static int delivery(Size size, int count) {
        return size.price * count >= FREE_DELIVERY_FROM ? 0 : DELIVERY;
    }

    public static int total(Size size, int count) {
        return size.price * count + delivery(size, count);
    }
}

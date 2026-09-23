package dev.yorushi.dma.task2.themes;

import dev.yorushi.dma.task2.Report;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/** Theme 6 — inheritance and polymorphism. */
public final class Theme6Inheritance {

    private Theme6Inheritance() {
    }

    // ---- T6.1 --------------------------------------------------------------

    /** T6.1: base screen with lifecycle hooks shared by every concrete screen. */
    public abstract static class BaseScreen {
        private boolean open;

        public abstract String title();

        public String onOpen() {
            open = true;
            return title() + " opened";
        }

        public String onClose() {
            open = false;
            return title() + " closed";
        }

        public boolean isOpen() {
            return open;
        }
    }

    public static final class LoginScreen extends BaseScreen {
        @Override
        public String title() {
            return "Login";
        }

        @Override
        public String onOpen() {
            return super.onOpen() + ", focus on the login field";
        }
    }

    public static final class HomeScreen extends BaseScreen {
        @Override
        public String title() {
            return "Home";
        }

        @Override
        public String onOpen() {
            return super.onOpen() + ", refreshing the feed";
        }
    }

    public static final class SettingsScreen extends BaseScreen {
        @Override
        public String title() {
            return "Settings";
        }

        @Override
        public String onClose() {
            return "preferences saved, " + super.onClose();
        }
    }

    // ---- T6.2 --------------------------------------------------------------

    /** T6.2: analytics event; each subclass formats its own log line. */
    public abstract static class AnalyticsEvent {
        public abstract String toLogLine();
    }

    public static final class ClickEvent extends AnalyticsEvent {
        private final String elementId;

        public ClickEvent(String elementId) {
            this.elementId = elementId;
        }

        @Override
        public String toLogLine() {
            return "click element=" + elementId;
        }
    }

    public static final class PurchaseEvent extends AnalyticsEvent {
        private final String sku;
        private final double amount;

        public PurchaseEvent(String sku, double amount) {
            this.sku = sku;
            this.amount = amount;
        }

        @Override
        public String toLogLine() {
            return String.format(Locale.ROOT, "purchase sku=%s amount=%.2f revenue=true", sku, amount);
        }
    }

    public static final class ScreenViewEvent extends AnalyticsEvent {
        private final String screen;
        private final long durationMs;

        public ScreenViewEvent(String screen, long durationMs) {
            this.screen = screen;
            this.durationMs = durationMs;
        }

        @Override
        public String toLogLine() {
            return "screen_view name=" + screen + " time=" + durationMs + "ms";
        }
    }

    /** T6.2: accepts any event and relies on dynamic dispatch for the format. */
    public static final class AnalyticsService {
        private final List<String> sent = new ArrayList<>();

        public void track(AnalyticsEvent event) {
            sent.add(event.toLogLine());
        }

        public List<String> sent() {
            return sent;
        }
    }

    // ---- T6.3 --------------------------------------------------------------

    /** T6.3: phone sensor with a polymorphic reading. */
    public abstract static class DeviceSensor {
        public abstract String readData();
    }

    public static final class GyroscopeSensor extends DeviceSensor {
        private final double[] radiansPerSecond;

        public GyroscopeSensor(double x, double y, double z) {
            this.radiansPerSecond = new double[] {x, y, z};
        }

        @Override
        public String readData() {
            return String.format(Locale.ROOT, "gyro x=%.2f y=%.2f z=%.2f rad/s",
                    radiansPerSecond[0], radiansPerSecond[1], radiansPerSecond[2]);
        }
    }

    public static final class LightSensor extends DeviceSensor {
        private final float lux;

        public LightSensor(float lux) {
            this.lux = lux;
        }

        @Override
        public String readData() {
            return String.format(Locale.ROOT, "light %.0f lx", lux);
        }
    }

    // ---- T6.4 --------------------------------------------------------------

    /** T6.4: UI element rendered through the common supertype. */
    public abstract static class UiComponent {
        protected final int id;

        protected UiComponent(int id) {
            this.id = id;
        }

        public abstract String render();
    }

    public static final class ButtonComponent extends UiComponent {
        private final String text;

        public ButtonComponent(int id, String text) {
            super(id);
            this.text = text;
        }

        @Override
        public String render() {
            return "Button[" + id + "] '" + text + "'";
        }
    }

    public static final class ImageComponent extends UiComponent {
        private final String url;

        public ImageComponent(int id, String url) {
            super(id);
            this.url = url;
        }

        @Override
        public String render() {
            return "Image[" + id + "] " + url;
        }
    }

    /** T6.4: draws every component without knowing its concrete type. */
    public static List<String> drawScreen(List<? extends UiComponent> components) {
        List<String> frame = new ArrayList<>();
        for (UiComponent component : components) {
            frame.add(component.render());
        }
        return frame;
    }

    // ---- T6.5 --------------------------------------------------------------

    /** T6.5: subscription plan with a polymorphic monthly price. */
    public abstract static class Subscription {
        protected static final double BASE_MONTHLY = 299.0;

        public abstract double monthlyCost();

        public double costFor(int months) {
            return monthlyCost() * months;
        }
    }

    public static final class MonthlySubscription extends Subscription {
        @Override
        public double monthlyCost() {
            return BASE_MONTHLY;
        }
    }

    /** Each extra member costs 40% of a base plan. */
    public static final class FamilySubscription extends Subscription {
        private final int members;

        public FamilySubscription(int members) {
            if (members < 1 || members > 6) {
                throw new IllegalArgumentException("family plan supports 1..6 members");
            }
            this.members = members;
        }

        @Override
        public double monthlyCost() {
            return BASE_MONTHLY + (members - 1) * BASE_MONTHLY * 0.4;
        }
    }

    /** Billed yearly with a 25% discount. */
    public static final class AnnualDiscountSubscription extends Subscription {
        @Override
        public double monthlyCost() {
            return BASE_MONTHLY * 0.75;
        }
    }

    public static void demo(Report out) {
        out.section("Theme 6. Inheritance and polymorphism");
        List<BaseScreen> screens = Arrays.asList(new LoginScreen(), new HomeScreen(), new SettingsScreen());
        StringBuilder lifecycle = new StringBuilder();
        for (BaseScreen screen : screens) {
            lifecycle.append(screen.onOpen()).append("; ").append(screen.onClose()).append(" | ");
        }
        out.item("T6.1", lifecycle.substring(0, lifecycle.length() - 3));
        AnalyticsService analytics = new AnalyticsService();
        analytics.track(new ClickEvent("btn_buy"));
        analytics.track(new PurchaseEvent("sku-17", 1497.0));
        analytics.track(new ScreenViewEvent("Cart", 5200));
        out.item("T6.2", String.join(" / ", analytics.sent()));
        List<DeviceSensor> sensors = Arrays.asList(new GyroscopeSensor(0.12, -0.03, 1.57), new LightSensor(320f));
        StringBuilder readings = new StringBuilder();
        for (DeviceSensor sensor : sensors) {
            readings.append(sensor.readData()).append("; ");
        }
        out.item("T6.3", readings.substring(0, readings.length() - 2));
        out.item("T6.4", String.join(" | ", drawScreen(Arrays.asList(
                new ButtonComponent(1, "Pay"), new ImageComponent(2, "https://cdn.example/banner.webp")))));
        out.item("T6.5", String.format(Locale.ROOT, "per month: monthly=%.2f, family of 4=%.2f, annual=%.2f",
                new MonthlySubscription().monthlyCost(), new FamilySubscription(4).monthlyCost(),
                new AnnualDiscountSubscription().monthlyCost()));
    }
}

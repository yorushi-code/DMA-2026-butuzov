package dev.yorushi.dma.task4.logic;

import java.util.ArrayList;
import java.util.List;

/** The arithmetic behind the calculator screens, kept free of Android so it runs in plain JUnit. */
public final class Calc {

    private Calc() {
    }

    /** P06: one human year of a dog counts as seven. */
    public static double dogYears(double humanYears) {
        return humanYears * 7;
    }

    /** P09 */
    public static double cmToInches(double cm) {
        return cm / 2.54;
    }

    /** P10: works for negative numbers too, where {@code n % 2} is -1 for odd values. */
    public static boolean isEven(long n) {
        return n % 2 == 0;
    }

    /** P11: weight / (height in metres)^2. */
    public static double bmi(double weightKg, double heightCm) {
        double metres = heightCm / 100;
        return weightKg / (metres * metres);
    }

    public enum BmiVerdict { UNDERWEIGHT, NORMAL, OVERWEIGHT }

    /** P11: WHO thresholds 18.5 and 25. */
    public static BmiVerdict bmiVerdict(double bmi) {
        if (bmi < 18.5) {
            return BmiVerdict.UNDERWEIGHT;
        }
        return bmi < 25 ? BmiVerdict.NORMAL : BmiVerdict.OVERWEIGHT;
    }

    /** P12: litres per 100 km. */
    public static double fuelPer100Km(double km, double litres) {
        return litres / km * 100;
    }

    /** P13 */
    public static double celsiusToFahrenheit(double c) {
        return c * 1.8 + 32;
    }

    /** P13 */
    public static double celsiusToKelvin(double c) {
        return c + 273.15;
    }

    public enum Op { PLUS, MINUS, TIMES, DIVIDE }

    /** P14: division by zero is an error rather than Infinity. */
    public static double apply(double a, double b, Op op) {
        switch (op) {
            case PLUS:
                return a + b;
            case MINUS:
                return a - b;
            case TIMES:
                return a * b;
            default:
                if (b == 0) {
                    throw new ArithmeticException("division by zero");
                }
                return a / b;
        }
    }

    /** P15: {discount amount, final price}. */
    public static double[] discount(double price, double percent) {
        double amount = price * percent / 100;
        return new double[] {amount, price - amount};
    }

    /** P16: fixed rates, roubles per unit. */
    public static final double RUB_PER_USD = 90.0;
    public static final double RUB_PER_EUR = 98.0;

    public static double rubToUsd(double rub) {
        return round2(rub / RUB_PER_USD);
    }

    public static double rubToEur(double rub) {
        return round2(rub / RUB_PER_EUR);
    }

    public static double round2(double value) {
        return Math.round(value * 100) / 100.0;
    }

    /** P19: {hours, minutes}, minutes rounded to the nearest whole minute. */
    public static long[] travelTime(double km, double kmPerHour) {
        long minutes = Math.round(km / kmPerHour * 60);
        return new long[] {minutes / 60, minutes % 60};
    }

    /** Project 2: 10% tip. {tip, total, per person}. */
    public static double[] tip(double bill, int persons) {
        double tip = bill * 0.10;
        double total = bill + tip;
        return new double[] {tip, total, total / persons};
    }

    /** P26: annuity payment. A zero rate is simply the principal spread evenly. */
    public static double monthlyPayment(double principal, double annualRatePercent, int months) {
        double r = annualRatePercent / 100 / 12;
        if (r == 0) {
            return principal / months;
        }
        return principal * r / (1 - Math.pow(1 + r, -months));
    }

    /** One row of the P26 schedule. */
    public static final class Payment {
        public final int month;
        public final double payment;
        public final double interest;
        public final double principal;
        public final double balance;

        Payment(int month, double payment, double interest, double principal, double balance) {
            this.month = month;
            this.payment = payment;
            this.interest = interest;
            this.principal = principal;
            this.balance = balance;
        }
    }

    /** P26: the last payment absorbs rounding so the balance ends at exactly zero. */
    public static List<Payment> schedule(double principal, double annualRatePercent, int months) {
        double r = annualRatePercent / 100 / 12;
        double payment = monthlyPayment(principal, annualRatePercent, months);
        List<Payment> rows = new ArrayList<>();
        double balance = principal;
        for (int m = 1; m <= months; m++) {
            double interest = balance * r;
            double toPrincipal = m == months ? balance : payment - interest;
            balance -= toPrincipal;
            rows.add(new Payment(m, toPrincipal + interest, interest, toPrincipal, Math.max(0, balance)));
        }
        return rows;
    }

    /** P28: may exceed 100 when the goal is beaten; the progress bar caps itself. */
    public static int stepsPercent(int done, int target) {
        return (int) Math.round(done * 100.0 / target);
    }
}

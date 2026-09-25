package dev.yorushi.dma.task4.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;
import org.junit.Test;

public class LogicTest {

    private static final double EPS = 1e-9;

    @Test
    public void T4_2_nameNeedsTwoCharacters() {
        assertFalse(Texts.isNameLongEnough("A"));
        assertFalse(Texts.isNameLongEnough("  A "));
        assertFalse(Texts.isNameLongEnough(null));
        assertTrue(Texts.isNameLongEnough("Al"));
    }

    @Test
    public void PR1_milestonesChangeColour() {
        assertEquals("#E8F5E9", Clicker.milestoneColor(10));
        assertEquals("#FFF9C4", Clicker.milestoneColor(50));
        assertNull(Clicker.milestoneColor(11));
    }

    @Test
    public void PR2_tipIsTenPercentSplitByGuests() {
        double[] r = Calc.tip(1000, 4);
        assertEquals(100, r[0], EPS);
        assertEquals(1100, r[1], EPS);
        assertEquals(275, r[2], EPS);
    }

    @Test
    public void P02_dieStaysBetweenOneAndSix() {
        Random random = new Random(1);
        boolean[] seen = new boolean[7];
        for (int i = 0; i < 1000; i++) {
            int v = Randoms.rollDie(random);
            assertTrue(v >= 1 && v <= 6);
            seen[v] = true;
        }
        for (int face = 1; face <= 6; face++) {
            assertTrue("face " + face, seen[face]);
        }
    }

    @Test
    public void P03_answerIgnoresCaseAndSpaces() {
        assertTrue(Texts.isCorrectAnswer("  moscow ", "Moscow"));
        assertFalse(Texts.isCorrectAnswer("Paris", "Moscow"));
    }

    @Test
    public void P05_reverse() {
        assertEquals("olleh", Texts.reverse("hello"));
    }

    @Test
    public void P06_dogYears() {
        assertEquals(21, Calc.dogYears(3), EPS);
    }

    @Test
    public void P07_countsLettersOnly() {
        assertEquals(10, Texts.countLetters("Hello, world 42"));
    }

    @Test
    public void P09_cmToInches() {
        assertEquals(10, Calc.cmToInches(25.4), EPS);
    }

    @Test
    public void P10_parityIncludingNegatives() {
        assertTrue(Calc.isEven(0));
        assertTrue(Calc.isEven(-4));
        assertFalse(Calc.isEven(-3));
        assertFalse(Calc.isEven(7));
    }

    @Test
    public void P11_bmiAndVerdict() {
        assertEquals(22.857, Calc.bmi(70, 175), 1e-3);
        assertEquals(Calc.BmiVerdict.UNDERWEIGHT, Calc.bmiVerdict(18.4));
        assertEquals(Calc.BmiVerdict.NORMAL, Calc.bmiVerdict(18.5));
        assertEquals(Calc.BmiVerdict.OVERWEIGHT, Calc.bmiVerdict(25));
    }

    @Test
    public void P12_fuelPer100Km() {
        assertEquals(8, Calc.fuelPer100Km(250, 20), EPS);
    }

    @Test
    public void P13_temperatures() {
        assertEquals(212, Calc.celsiusToFahrenheit(100), EPS);
        assertEquals(273.15, Calc.celsiusToKelvin(0), EPS);
    }

    @Test
    public void P14_fourOperationsAndDivisionByZero() {
        assertEquals(5, Calc.apply(2, 3, Calc.Op.PLUS), EPS);
        assertEquals(-1, Calc.apply(2, 3, Calc.Op.MINUS), EPS);
        assertEquals(6, Calc.apply(2, 3, Calc.Op.TIMES), EPS);
        assertEquals(2.5, Calc.apply(5, 2, Calc.Op.DIVIDE), EPS);
        assertThrows(ArithmeticException.class, () -> Calc.apply(1, 0, Calc.Op.DIVIDE));
    }

    @Test
    public void P15_discount() {
        double[] r = Calc.discount(2000, 15);
        assertEquals(300, r[0], EPS);
        assertEquals(1700, r[1], EPS);
    }

    @Test
    public void P16_currencyRoundedToKopecks() {
        assertEquals(11.11, Calc.rubToUsd(1000), EPS);
        assertEquals(10.2, Calc.rubToEur(1000), EPS);
    }

    @Test
    public void P17_pinHasNoRepeatedNeighbours() {
        Random random = new Random(7);
        for (int i = 0; i < 500; i++) {
            String pin = Randoms.pin(i % 2 == 0 ? 4 : 6, random);
            assertEquals(i % 2 == 0 ? 4 : 6, pin.length());
            for (int j = 1; j < pin.length(); j++) {
                assertNotEquals(pin, pin.charAt(j - 1), pin.charAt(j));
            }
        }
    }

    @Test
    public void P19_travelTime() {
        long[] t = Calc.travelTime(150, 60);
        assertEquals(2, t[0]);
        assertEquals(30, t[1]);
    }

    @Test
    public void P20_passwordStrengthBoundaries() {
        assertEquals(Texts.Strength.WEAK, Texts.passwordStrength("12345"));
        assertEquals(Texts.Strength.MEDIUM, Texts.passwordStrength("123456"));
        assertEquals(Texts.Strength.MEDIUM, Texts.passwordStrength("1234567890"));
        assertEquals(Texts.Strength.STRONG, Texts.passwordStrength("12345678901"));
    }

    @Test
    public void P22_pizzaTotalWithDelivery() {
        assertEquals(450 + 150, Orders.total(Orders.Size.SMALL, 1));
        assertEquals(850 * 2, Orders.total(Orders.Size.LARGE, 2));
    }

    @Test
    public void P26_annuityAndScheduleEndsAtZero() {
        assertEquals(8884.88, Calc.monthlyPayment(100_000, 12, 12), 0.01);
        assertEquals(1000, Calc.monthlyPayment(12_000, 0, 12), EPS);
        List<Calc.Payment> rows = Calc.schedule(100_000, 12, 12);
        assertEquals(12, rows.size());
        assertEquals(0, rows.get(11).balance, 1e-6);
        double paidPrincipal = 0;
        for (Calc.Payment p : rows) {
            paidPrincipal += p.principal;
        }
        assertEquals(100_000, paidPrincipal, 1e-6);
    }

    @Test
    public void P28_stepsPercent() {
        assertEquals(75, Calc.stepsPercent(7500, 10000));
        assertEquals(120, Calc.stepsPercent(12000, 10000));
    }
}

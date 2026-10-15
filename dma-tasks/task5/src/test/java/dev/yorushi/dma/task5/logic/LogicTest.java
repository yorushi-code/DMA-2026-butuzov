package dev.yorushi.dma.task5.logic;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** The logic behind the interactive items, on the JVM. */
public class LogicTest {

    @Test
    public void m13_passwordRulesOneByOne() {
        assertFalse(PasswordRules.longEnough("Abc1234"));
        assertTrue(PasswordRules.longEnough("Abc12345"));
        assertFalse(PasswordRules.hasDigit("Abcdefgh"));
        assertTrue(PasswordRules.hasDigit("Abcdefg1"));
        assertFalse(PasswordRules.mixedCase("abcdefg1"));
        assertFalse(PasswordRules.mixedCase("ABCDEFG1"));
        assertTrue(PasswordRules.mixedCase("aBCDEFG1"));
    }

    @Test
    public void m13_emptyPasswordsDoNotCountAsMatching() {
        assertFalse(PasswordRules.matches("", ""));
        assertFalse(PasswordRules.matches("Abcdefg1", "Abcdefg2"));
        assertTrue(PasswordRules.matches("Abcdefg1", "Abcdefg1"));
        assertTrue(PasswordRules.allMet("Abcdefg1", "Abcdefg1"));
        assertFalse(PasswordRules.allMet("abcdefg1", "abcdefg1"));
    }

    @Test
    public void m38_selectionIsNumberedInPickOrder() {
        SelectionOrder order = new SelectionOrder();
        order.toggle(4);
        order.toggle(0);
        order.toggle(7);
        assertEquals(1, order.position(4));
        assertEquals(2, order.position(0));
        assertEquals(3, order.position(7));
        assertEquals(0, order.position(5));
        assertEquals(3, order.size());
    }

    @Test
    public void m38_unpickingRenumbersTheRest() {
        SelectionOrder order = new SelectionOrder();
        order.toggle(4);
        order.toggle(0);
        order.toggle(7);
        order.toggle(0);
        assertEquals(1, order.position(4));
        assertEquals(0, order.position(0));
        assertEquals(2, order.position(7));
        order.toggle(0);
        assertEquals(3, order.position(0));
    }

    @Test
    public void m37_m47_timerFormat() {
        assertEquals("0:00", Countdown.format(0));
        assertEquals("0:07", Countdown.format(7));
        assertEquals("1:00", Countdown.format(60));
        assertEquals("12:05", Countdown.format(725));
        assertEquals("0:00", Countdown.format(-3));
    }

    @Test
    public void m47_secondsLeftRoundUpUntilTheEnd() {
        assertEquals(60, Countdown.secondsLeft(60_000));
        assertEquals(60, Countdown.secondsLeft(59_001));
        assertEquals(1, Countdown.secondsLeft(1));
        assertEquals(0, Countdown.secondsLeft(0));
        assertEquals(0, Countdown.secondsLeft(-250));
    }

    @Test
    public void m08_tariffFee() {
        assertEquals(200 + 5 * 30 + 20 * 12, Tariff.monthly(500, 20));
        assertEquals(200 + 30 + 5 * 12, Tariff.monthly(100, 5));
        assertEquals(200 + 20 * 30 + 100 * 12, Tariff.monthly(2000, 100));
    }

    @Test
    public void m12_rangeFromTypedText() {
        assertArrayEquals(new int[] {5000, 30000}, PriceRange.normalize("5000", "30000", 0, 50000, 500));
        // Out of range, swapped, unsnapped, spaced and empty input.
        assertArrayEquals(new int[] {0, 50000}, PriceRange.normalize("-100", "70000", 0, 50000, 500));
        assertArrayEquals(new int[] {1000, 20000}, PriceRange.normalize("20 000", "1000", 0, 50000, 500));
        assertArrayEquals(new int[] {1500, 2000}, PriceRange.normalize("1620", "1880", 0, 50000, 500));
        assertArrayEquals(new int[] {0, 50000}, PriceRange.normalize("", "abc", 0, 50000, 500));
    }
}

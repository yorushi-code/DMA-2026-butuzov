package dev.yorushi.dma.task4;

import dev.yorushi.dma.task4.practice.BmiActivity;
import dev.yorushi.dma.task4.practice.CalculatorActivity;
import dev.yorushi.dma.task4.practice.CapitalsQuizActivity;
import dev.yorushi.dma.task4.practice.CharCountActivity;
import dev.yorushi.dma.task4.practice.CmToInchActivity;
import dev.yorushi.dma.task4.practice.CurrencyActivity;
import dev.yorushi.dma.task4.practice.DiceActivity;
import dev.yorushi.dma.task4.practice.DiscountActivity;
import dev.yorushi.dma.task4.practice.DogAgeActivity;
import dev.yorushi.dma.task4.practice.FlashlightActivity;
import dev.yorushi.dma.task4.practice.FuelActivity;
import dev.yorushi.dma.task4.practice.GlossaryActivity;
import dev.yorushi.dma.task4.practice.LoanActivity;
import dev.yorushi.dma.task4.practice.LoginActivity;
import dev.yorushi.dma.task4.practice.MasterFormActivity;
import dev.yorushi.dma.task4.practice.NoteEditActivity;
import dev.yorushi.dma.task4.practice.ParityActivity;
import dev.yorushi.dma.task4.practice.PasswordActivity;
import dev.yorushi.dma.task4.practice.PinActivity;
import dev.yorushi.dma.task4.practice.PizzaActivity;
import dev.yorushi.dma.task4.practice.PortfolioActivity;
import dev.yorushi.dma.task4.practice.QuizFirstActivity;
import dev.yorushi.dma.task4.practice.QuizOneActivity;
import dev.yorushi.dma.task4.practice.ReverseActivity;
import dev.yorushi.dma.task4.practice.SecretToggleActivity;
import dev.yorushi.dma.task4.practice.StepsFormActivity;
import dev.yorushi.dma.task4.practice.TemperatureActivity;
import dev.yorushi.dma.task4.practice.TicketFormActivity;
import dev.yorushi.dma.task4.practice.TrafficLightActivity;
import dev.yorushi.dma.task4.practice.TravelTimeActivity;
import dev.yorushi.dma.task4.project.CardFormActivity;
import dev.yorushi.dma.task4.project.ClickerActivity;
import dev.yorushi.dma.task4.project.TipActivity;
import dev.yorushi.dma.task4.theme.GreetingActivity;
import dev.yorushi.dma.task4.theme.IntentStartActivity;
import dev.yorushi.dma.task4.theme.LayoutActivity;
import dev.yorushi.dma.task4.theme.ResourcesActivity;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Every screen of the menu, in assignment order. The instrumented tests open each of them. */
public final class Catalog {

    private Catalog() {
    }

    public static final class Entry {
        public final String items;
        public final int title;
        public final int section;
        public final Class<?> activity;

        Entry(String items, int title, int section, Class<?> activity) {
            this.items = items;
            this.title = title;
            this.section = section;
            this.activity = activity;
        }
    }

    public static final List<Entry> ENTRIES = Collections.unmodifiableList(Arrays.asList(
            new Entry("T3.1–T3.5", R.string.t3_title, R.string.section_themes, LayoutActivity.class),
            new Entry("T4.1–T4.5", R.string.t4_title, R.string.section_themes, GreetingActivity.class),
            new Entry("T5.1–T5.5", R.string.t5_title, R.string.section_themes, ResourcesActivity.class),
            new Entry("T6.1–T6.5", R.string.t6_title, R.string.section_themes, IntentStartActivity.class),
            new Entry("PR1", R.string.pr1_title, R.string.section_projects, ClickerActivity.class),
            new Entry("PR2", R.string.pr2_title, R.string.section_projects, TipActivity.class),
            new Entry("PR3", R.string.pr3_title, R.string.section_projects, CardFormActivity.class),
            new Entry("P01", R.string.p01_title, R.string.section_level1, TrafficLightActivity.class),
            new Entry("P02", R.string.p02_title, R.string.section_level1, DiceActivity.class),
            new Entry("P03", R.string.p03_title, R.string.section_level1, QuizOneActivity.class),
            new Entry("P04", R.string.p04_title, R.string.section_level1, SecretToggleActivity.class),
            new Entry("P05", R.string.p05_title, R.string.section_level1, ReverseActivity.class),
            new Entry("P06", R.string.p06_title, R.string.section_level1, DogAgeActivity.class),
            new Entry("P07", R.string.p07_title, R.string.section_level1, CharCountActivity.class),
            new Entry("P08", R.string.p08_title, R.string.section_level1, FlashlightActivity.class),
            new Entry("P09", R.string.p09_title, R.string.section_level1, CmToInchActivity.class),
            new Entry("P10", R.string.p10_title, R.string.section_level1, ParityActivity.class),
            new Entry("P11", R.string.p11_title, R.string.section_level2, BmiActivity.class),
            new Entry("P12", R.string.p12_title, R.string.section_level2, FuelActivity.class),
            new Entry("P13", R.string.p13_title, R.string.section_level2, TemperatureActivity.class),
            new Entry("P14", R.string.p14_title, R.string.section_level2, CalculatorActivity.class),
            new Entry("P15", R.string.p15_title, R.string.section_level2, DiscountActivity.class),
            new Entry("P16", R.string.p16_title, R.string.section_level2, CurrencyActivity.class),
            new Entry("P17", R.string.p17_title, R.string.section_level2, PinActivity.class),
            new Entry("P18", R.string.p18_title, R.string.section_level2, CapitalsQuizActivity.class),
            new Entry("P19", R.string.p19_title, R.string.section_level2, TravelTimeActivity.class),
            new Entry("P20", R.string.p20_title, R.string.section_level2, PasswordActivity.class),
            new Entry("P21", R.string.p21_title, R.string.section_level3, LoginActivity.class),
            new Entry("P22", R.string.p22_title, R.string.section_level3, PizzaActivity.class),
            new Entry("P23", R.string.p23_title, R.string.section_level3, QuizFirstActivity.class),
            new Entry("P24", R.string.p24_title, R.string.section_level3, MasterFormActivity.class),
            new Entry("P25", R.string.p25_title, R.string.section_level3, NoteEditActivity.class),
            new Entry("P26", R.string.p26_title, R.string.section_level3, LoanActivity.class),
            new Entry("P27", R.string.p27_title, R.string.section_level3, TicketFormActivity.class),
            new Entry("P28", R.string.p28_title, R.string.section_level3, StepsFormActivity.class),
            new Entry("P29", R.string.p29_title, R.string.section_level3, GlossaryActivity.class),
            new Entry("P30", R.string.p30_title, R.string.section_level3, PortfolioActivity.class)));
}

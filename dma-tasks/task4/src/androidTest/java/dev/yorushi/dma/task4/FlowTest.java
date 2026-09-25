package dev.yorushi.dma.task4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.app.LocaleManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.LocaleList;
import android.widget.TextView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import dev.yorushi.dma.task4.practice.BmiActivity;
import dev.yorushi.dma.task4.practice.CalculatorActivity;
import dev.yorushi.dma.task4.practice.CapitalsQuizActivity;
import dev.yorushi.dma.task4.practice.CharCountActivity;
import dev.yorushi.dma.task4.practice.GlossaryActivity;
import dev.yorushi.dma.task4.practice.LoanActivity;
import dev.yorushi.dma.task4.practice.LoginActivity;
import dev.yorushi.dma.task4.practice.MasterFormActivity;
import dev.yorushi.dma.task4.practice.NoteEditActivity;
import dev.yorushi.dma.task4.practice.PasswordActivity;
import dev.yorushi.dma.task4.practice.PinActivity;
import dev.yorushi.dma.task4.practice.PizzaActivity;
import dev.yorushi.dma.task4.practice.PortfolioActivity;
import dev.yorushi.dma.task4.practice.QuizFirstActivity;
import dev.yorushi.dma.task4.practice.QuizOneActivity;
import dev.yorushi.dma.task4.practice.StepsFormActivity;
import dev.yorushi.dma.task4.practice.TemperatureActivity;
import dev.yorushi.dma.task4.practice.TicketFormActivity;
import dev.yorushi.dma.task4.project.CardFormActivity;
import dev.yorushi.dma.task4.project.ClickerActivity;
import dev.yorushi.dma.task4.project.TipActivity;
import dev.yorushi.dma.task4.theme.GreetingActivity;
import dev.yorushi.dma.task4.theme.IntentStartActivity;
import dev.yorushi.dma.task4.theme.LayoutActivity;
import dev.yorushi.dma.task4.theme.ResourcesActivity;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * The screens driven the way a user would: type, tap, read the result. The
 * expected texts are the English resources; the emulator runs in en-US.
 */
// "try": each scenario is held open for Espresso and never read, which javac
// reports for every try-with-resources below.
@SuppressWarnings("try")
@RunWith(AndroidJUnit4.class)
public class FlowTest {

    private static void type(int id, String text) {
        onView(withId(id)).perform(scrollTo(), replaceText(text));
    }

    private static void tap(int id) {
        onView(withId(id)).perform(scrollTo(), click());
    }

    private static void shows(int id, String text) {
        onView(withId(id)).check(matches(withText(text)));
    }

    // ------------------------------------------------------------ themes

    @Test
    public void T3_orientationSwitchesAndFieldsClear() {
        try (ActivityScenario<LayoutActivity> ignored = ActivityScenario.launch(LayoutActivity.class)) {
            type(R.id.etName, "Semyon");
            type(R.id.etAge, "19");
            tap(R.id.btnOrientation);
            shows(R.id.tvOrientation, "Button row: horizontal");
            Shots.take("t3_layout");
            tap(R.id.btnClear);
            shows(R.id.etName, "");
        }
    }

    @Test
    public void T4_validationGreetingHeaderCounter() {
        try (ActivityScenario<GreetingActivity> ignored = ActivityScenario.launch(GreetingActivity.class)) {
            type(R.id.etName, "A");
            tap(R.id.btnGreet);
            onView(withId(R.id.etName)).check(matches(hasErrorText("Name is too short")));
            type(R.id.etName, "Semyon");
            tap(R.id.btnGreet);
            shows(R.id.tvResult, "Hello, Semyon!");
            tap(R.id.btnChangeHeader);
            shows(R.id.tvHeader, "Text changed successfully!");
            tap(R.id.btnCount);
            tap(R.id.btnCount);
            shows(R.id.tvCount, "2");
            Shots.take("t4_greeting");
        }
    }

    @Test
    public void T5_resourcesAndRussianLocale() {
        try (ActivityScenario<ResourcesActivity> ignored = ActivityScenario.launch(ResourcesActivity.class)) {
            type(R.id.etName, "Semyon");
            tap(R.id.btnWelcome);
            shows(R.id.tvWelcome, "Hello, Semyon!");
            shows(R.id.tvLanguage, "Interface language: English");
            Shots.take("t5_resources");
        }
        // T5.5: the same app with the per-app language set to Russian (API 33+).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Context context = ApplicationProvider.getApplicationContext();
            LocaleManager locales = context.getSystemService(LocaleManager.class);
            try {
                locales.setApplicationLocales(new LocaleList(Locale.forLanguageTag("ru")));
                try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
                    Shots.take("t5_menu_ru");
                }
                // The expected text comes from values-ru itself, read through a Russian context.
                Configuration ru = new Configuration(context.getResources().getConfiguration());
                ru.setLocales(new LocaleList(Locale.forLanguageTag("ru")));
                Context russian = context.createConfigurationContext(ru);
                String expected = russian.getString(R.string.t5_language, russian.getString(R.string.language_name));
                assertNotEquals(context.getString(R.string.t5_language, context.getString(R.string.language_name)),
                        expected);
                try (ActivityScenario<ResourcesActivity> ignored = ActivityScenario.launch(ResourcesActivity.class)) {
                    shows(R.id.tvLanguage, expected);
                }
            } finally {
                locales.setApplicationLocales(LocaleList.getEmptyLocaleList());
            }
        }
    }

    @Test
    public void T6_guestBooleanAndDoubleReachTheThirdScreen() {
        try (ActivityScenario<IntentStartActivity> ignored = ActivityScenario.launch(IntentStartActivity.class)) {
            tap(R.id.cbAgree);
            type(R.id.etBalance, "1500.5");
            tap(R.id.btnNext);
            shows(R.id.tvHello, "Hello, Guest!");
            Shots.take("t6_second");
            tap(R.id.btnAbout);
            shows(R.id.tvAgreed, "Rules accepted: yes");
            shows(R.id.tvBalance, "Balance: 1500.50");
            Shots.take("t6_about");
        }
    }

    // ---------------------------------------------------------- projects

    @Test
    public void PR1_tenClicksTurnTheScreenGreen() {
        try (ActivityScenario<ClickerActivity> scenario = ActivityScenario.launch(ClickerActivity.class)) {
            for (int i = 0; i < 10; i++) {
                onView(withId(R.id.btnClickMe)).perform(click());
            }
            shows(R.id.tvCounter, "10");
            scenario.onActivity(a -> assertEquals(Color.parseColor("#E8F5E9"),
                    ((ColorDrawable) a.findViewById(R.id.root).getBackground()).getColor()));
            Shots.take("pr1_clicker");
        }
    }

    @Test
    public void PR2_tipSplitBetweenGuests() {
        try (ActivityScenario<TipActivity> ignored = ActivityScenario.launch(TipActivity.class)) {
            type(R.id.etTotalBill, "1000");
            type(R.id.etPersonsCount, "4");
            tap(R.id.btnCalculate);
            shows(R.id.tvTipAmount, "Tip (10%): 100.00");
            shows(R.id.tvPerPerson, "Per guest: 275.00");
            Shots.take("pr2_tip");
        }
    }

    @Test
    public void PR3_cardShowsTheEnteredData() {
        try (ActivityScenario<CardFormActivity> ignored = ActivityScenario.launch(CardFormActivity.class)) {
            type(R.id.etName, "Semyon Butuzov");
            type(R.id.etGroup, "P24-3.2");
            type(R.id.etSkill, "Java");
            tap(R.id.btnGenerateCard);
            shows(R.id.tvName, "Semyon Butuzov");
            shows(R.id.tvSkill, "Loves: Java");
            Shots.take("pr3_card");
        }
    }

    // ---------------------------------------------------------- practice

    @Test
    public void P03_answerIsCheckedIgnoringCase() {
        try (ActivityScenario<QuizOneActivity> ignored = ActivityScenario.launch(QuizOneActivity.class)) {
            type(R.id.etAnswer, "paris ");
            tap(R.id.btnCheck);
            shows(R.id.tvVerdict, "Correct!");
            Shots.take("p03_quiz");
        }
    }

    @Test
    public void P07_lettersCountedLive() {
        try (ActivityScenario<CharCountActivity> ignored = ActivityScenario.launch(CharCountActivity.class)) {
            type(R.id.etText, "Hello, world 42");
            shows(R.id.tvCount, "Letters: 10 · characters: 15");
        }
    }

    @Test
    public void P11_bmiWithVerdict() {
        try (ActivityScenario<BmiActivity> ignored = ActivityScenario.launch(BmiActivity.class)) {
            type(R.id.etWeight, "70");
            type(R.id.etHeight, "175");
            tap(R.id.btnCalc);
            shows(R.id.tvResult, "BMI 22.86: normal");
            Shots.take("p11_bmi");
        }
    }

    @Test
    public void P13_belowAbsoluteZeroIsRejected() {
        try (ActivityScenario<TemperatureActivity> ignored = ActivityScenario.launch(TemperatureActivity.class)) {
            type(R.id.etCelsius, "-300");
            tap(R.id.btnToK);
            onView(withId(R.id.etCelsius)).check(matches(hasErrorText("Below absolute zero")));
            type(R.id.etCelsius, "100");
            tap(R.id.btnToF);
            shows(R.id.tvResult, "100 °C = 212 °F");
        }
    }

    @Test
    public void P14_divisionByZeroAndMultiplication() {
        try (ActivityScenario<CalculatorActivity> ignored = ActivityScenario.launch(CalculatorActivity.class)) {
            type(R.id.etA, "5");
            type(R.id.etB, "0");
            tap(R.id.btnDivide);
            shows(R.id.tvResult, "Division by zero is not allowed");
            type(R.id.etA, "6");
            type(R.id.etB, "7");
            tap(R.id.btnTimes);
            shows(R.id.tvResult, "Result: 42");
            Shots.take("p14_calculator");
        }
    }

    @Test
    public void P17_sixDigitPin() {
        try (ActivityScenario<PinActivity> scenario = ActivityScenario.launch(PinActivity.class)) {
            tap(R.id.rb6);
            tap(R.id.btnGenerate);
            scenario.onActivity(a -> assertEquals(6, ((TextView) a.findViewById(R.id.tvPin)).getText().length()));
            Shots.take("p17_pin");
        }
    }

    @Test
    public void P18_allRightAnswersScoreFour() {
        int[] right = {R.id.btnOption1, R.id.btnOption2, R.id.btnOption3, R.id.btnOption1};
        try (ActivityScenario<CapitalsQuizActivity> ignored = ActivityScenario.launch(CapitalsQuizActivity.class)) {
            for (int id : right) {
                tap(id);
            }
            shows(R.id.tvCountry, "Finished! Score 4 of 4");
            Shots.take("p18_capitals");
        }
    }

    @Test
    public void P20_strengthFollowsLength() {
        try (ActivityScenario<PasswordActivity> ignored = ActivityScenario.launch(PasswordActivity.class)) {
            type(R.id.etPassword, "abc");
            shows(R.id.tvStrength, "Weak");
            type(R.id.etPassword, "correct-horse");
            shows(R.id.tvStrength, "Strong");
            Shots.take("p20_password");
        }
    }

    @Test
    public void P21_wrongPasswordThenAccount() {
        try (ActivityScenario<LoginActivity> ignored = ActivityScenario.launch(LoginActivity.class)) {
            type(R.id.etLogin, "admin");
            type(R.id.etPassword, "0000");
            tap(R.id.btnLogin);
            shows(R.id.tvError, "Wrong login or password");
            type(R.id.etPassword, "1234");
            tap(R.id.btnLogin);
            shows(R.id.tvWelcome, "Welcome, admin!");
            Shots.take("p21_cabinet");
        }
    }

    @Test
    public void P22_twoLargePizzasGetFreeDelivery() {
        try (ActivityScenario<PizzaActivity> ignored = ActivityScenario.launch(PizzaActivity.class)) {
            tap(R.id.rbLarge);
            type(R.id.etCount, "2");
            type(R.id.etAddress, "Lenina 1");
            tap(R.id.btnOrder);
            shows(R.id.tvTotal, "Total: 1700");
            shows(R.id.tvDelivery, "Delivery: free from 1000");
            Shots.take("p22_receipt");
        }
    }

    @Test
    public void P23_twoRightAnswers() {
        try (ActivityScenario<QuizFirstActivity> ignored = ActivityScenario.launch(QuizFirstActivity.class)) {
            tap(R.id.rbOption0);
            tap(R.id.btnNext);
            tap(R.id.rbOption1);
            tap(R.id.btnFinish);
            shows(R.id.tvResult, "Your score: 2 of 2");
            Shots.take("p23_quiz");
        }
    }

    @Test
    public void P24_cardCarriesThePhone() {
        try (ActivityScenario<MasterFormActivity> ignored = ActivityScenario.launch(MasterFormActivity.class)) {
            type(R.id.etName, "Ivan Petrov");
            type(R.id.etJob, "Plumber");
            type(R.id.etPhone, "+79990001122");
            tap(R.id.btnCreate);
            shows(R.id.tvPhone, "+79990001122");
            shows(R.id.tvJob, "Plumber");
        }
    }

    @Test
    public void P25_readingModeAndBack() {
        try (ActivityScenario<NoteEditActivity> ignored = ActivityScenario.launch(NoteEditActivity.class)) {
            type(R.id.etNote, "Buy milk");
            tap(R.id.btnRead);
            shows(R.id.tvNote, "Buy milk");
            tap(R.id.btnEdit);
            shows(R.id.etNote, "Buy milk");
        }
    }

    @Test
    public void P26_paymentAndTwelveRowSchedule() {
        try (ActivityScenario<LoanActivity> ignored = ActivityScenario.launch(LoanActivity.class)) {
            type(R.id.etAmount, "100000");
            type(R.id.etRate, "12");
            type(R.id.etMonths, "12");
            tap(R.id.btnCalc);
            shows(R.id.tvPayment, "Monthly payment: 8884.88");
            Shots.take("p26_loan");
            tap(R.id.btnSchedule);
            onView(withId(R.id.tvRows)).check(matches(withText(containsString(" 12 "))));
            Shots.take("p26_schedule");
        }
    }

    @Test
    public void P27_ticketCarriesTheRoute() {
        try (ActivityScenario<TicketFormActivity> ignored = ActivityScenario.launch(TicketFormActivity.class)) {
            type(R.id.etFrom, "Moscow");
            type(R.id.etTo, "Tula");
            tap(R.id.btnIssue);
            shows(R.id.tvRoute, "Moscow → Tula");
            Shots.take("p27_ticket");
        }
    }

    @Test
    public void P28_seventyFivePercent() {
        try (ActivityScenario<StepsFormActivity> ignored = ActivityScenario.launch(StepsFormActivity.class)) {
            type(R.id.etTarget, "10000");
            type(R.id.etDone, "7500");
            tap(R.id.btnShow);
            shows(R.id.tvPercent, "75%");
            Shots.take("p28_steps");
        }
    }

    @Test
    public void P29_termOpensItsDescription() {
        try (ActivityScenario<GlossaryActivity> ignored = ActivityScenario.launch(GlossaryActivity.class)) {
            tap(R.id.btnTerm1);
            shows(R.id.tvTerm, "Intent");
            Shots.take("p29_term");
        }
    }

    @Test
    public void P30_skillsSection() {
        try (ActivityScenario<PortfolioActivity> ignored = ActivityScenario.launch(PortfolioActivity.class)) {
            Shots.take("p30_portfolio");
            tap(R.id.btnSkills);
            shows(R.id.tvSection, "My skills");
        }
    }
}

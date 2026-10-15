package dev.yorushi.dma.task5;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasFocus;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.espresso.action.Tap;
import androidx.test.espresso.matcher.ViewMatchers.Visibility;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import dev.yorushi.dma.task5.logic.Tariff;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;

/** User scenarios on the interactive items; each ends with a screenshot of the checked state. */
@RunWith(AndroidJUnit4.class)
public class BehaviorTest {

    private static String text(ActivityScenario<ScreenActivity> scenario, int id) {
        String[] out = new String[1];
        scenario.onActivity(a -> out[0] = ((TextView) a.findViewById(id)).getText().toString());
        return out[0];
    }

    @Test
    public void t2_2_buttonLockedWhileSending() {
        try (ActivityScenario<ScreenActivity> s = Screens.open(R.layout.t2_2_button_overlay)) {
            onView(withId(R.id.submit)).perform(click());
            onView(withId(R.id.submit)).check(matches(not(isEnabled())));
            onView(withId(R.id.submit_progress)).check(matches(isDisplayed()));
            Shots.take("b_t2_2_sending");
            SystemClock.sleep(Behaviors.SUBMIT_DELAY_MS + 500);
            onView(withId(R.id.submit)).check(matches(isEnabled()));
            onView(withId(R.id.submit_progress)).check(matches(withEffectiveVisibility(Visibility.GONE)));
            onView(withId(R.id.submit_result)).check(matches(withText(R.string.t2_2_done)));
        }
    }

    @Test
    public void t2_4_fragmentInTheContainer() {
        try (ActivityScenario<ScreenActivity> s = Screens.open(R.layout.t2_4_fragment_container)) {
            onView(withId(R.id.fragment_text)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void t4_5_oneCallHidesTheGroup() {
        try (ActivityScenario<ScreenActivity> s = Screens.open(R.layout.t4_5_group)) {
            onView(withId(R.id.group_toggle)).perform(click());
            for (int id : new int[] {R.id.group_item_1, R.id.group_item_2, R.id.group_item_3}) {
                onView(withId(id)).check(matches(withEffectiveVisibility(Visibility.GONE)));
            }
            Shots.take("b_t4_5_hidden");
            onView(withId(R.id.group_toggle)).perform(click());
            onView(withId(R.id.group_item_3)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void t6_3_stubInflatedOnDemand() {
        try (ActivityScenario<ScreenActivity> s = Screens.open(R.layout.t6_3_view_stub)) {
            onView(withId(R.id.simulate_offline)).perform(click());
            onView(withId(R.id.no_internet)).check(matches(isDisplayed()));
            Shots.take("b_t6_3_offline");
            onView(withId(R.id.retry)).perform(click());
            onView(withId(R.id.no_internet)).check(matches(withEffectiveVisibility(Visibility.GONE)));
            // A second time the inflated view is reused, not inflated again.
            onView(withId(R.id.simulate_offline)).perform(click());
            onView(withId(R.id.no_internet)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void m08_feeFollowsTheSliders() {
        try (ActivityScenario<ScreenActivity> s = Screens.open(R.layout.m08_tariff)) {
            s.onActivity(a -> {
                ((Slider) a.findViewById(R.id.tariff_minutes_slider)).setValue(1200);
                ((Slider) a.findViewById(R.id.tariff_gb_slider)).setValue(50);
            });
            onView(withId(R.id.tariff_minutes)).check(matches(withText("1200 мин")));
            onView(withId(R.id.tariff_price)).check(matches(withText(Tariff.monthly(1200, 50) + " ₽/мес")));
        }
    }

    @Test
    public void m11_quickSumFillsTheAmount() {
        try (ActivityScenario<ScreenActivity> s = Screens.open(R.layout.m11_phone_transfer)) {
            onView(withId(R.id.quick_1000)).perform(scrollTo(), click());
            onView(withId(R.id.transfer_amount)).check(matches(withText("1000")));
            Shots.take("b_m11_quick_sum");
        }
    }

    @Test
    public void m12_fieldsAndSliderStayInSync() {
        try (ActivityScenario<ScreenActivity> s = Screens.open(R.layout.m12_filter)) {
            onView(withId(R.id.filter_from)).check(matches(withText("5000")));
            // Both bounds typed (swapped and off-step), then focus leaves "from": one commit.
            onView(withId(R.id.filter_from)).perform(replaceText("42000"));
            onView(withId(R.id.filter_to)).perform(replaceText("1620"));
            onView(withId(R.id.filter_from)).perform(click());
            onView(withId(R.id.filter_to)).perform(click());
            closeSoftKeyboard();
            onView(withId(R.id.filter_from)).check(matches(withText("1500")));
            s.onActivity(a -> {
                List<Float> v = ((RangeSlider) a.findViewById(R.id.filter_range)).getValues();
                assertEquals(1500f, v.get(0), 0.1f);
                assertEquals(42000f, v.get(1), 0.1f);
            });
            onView(withId(R.id.filter_reset)).perform(click());
            onView(withId(R.id.filter_to)).check(matches(withText("30000")));
        }
    }

    @Test
    public void m13_rulesLightUpAsYouType() {
        try (ActivityScenario<ScreenActivity> s = Screens.open(R.layout.m13_password)) {
            onView(withId(R.id.pass_save)).check(matches(not(isEnabled())));
            onView(withId(R.id.pass_new)).perform(replaceText("Night2026"));
            onView(withId(R.id.pass_save)).check(matches(not(isEnabled())));
            onView(withId(R.id.pass_repeat)).perform(replaceText("Night2026"));
            closeSoftKeyboard();
            onView(withId(R.id.pass_save)).check(matches(isEnabled()));
            Shots.take("b_m13_rules_met");
        }
    }

    @Test
    public void m18_attachmentRowAppears() {
        try (ActivityScenario<ScreenActivity> s = Screens.open(R.layout.m18_feedback)) {
            onView(withId(R.id.feedback_attach)).perform(click());
            onView(withId(R.id.feedback_file)).check(matches(isDisplayed()));
            onView(withId(R.id.feedback_file_remove)).perform(click());
            onView(withId(R.id.feedback_file)).check(matches(withEffectiveVisibility(Visibility.GONE)));
        }
    }

    @Test
    public void m19_focusMovesOnAndBack() {
        try (ActivityScenario<ScreenActivity> s = Screens.open(R.layout.m19_sms_code)) {
            onView(withId(R.id.sms_digit_1)).perform(typeText("4"));
            onView(withId(R.id.sms_digit_2)).check(matches(hasFocus()));
            onView(withId(R.id.sms_digit_2)).perform(typeText("8"));
            onView(withId(R.id.sms_digit_3)).perform(typeText("1"));
            onView(withId(R.id.sms_digit_4)).check(matches(hasFocus()));
            closeSoftKeyboard();
            Shots.take("b_m19_three_digits");
        }
    }

    @Test
    public void m23_balanceHides() {
        try (ActivityScenario<ScreenActivity> s = Screens.open(R.layout.m23_wallet)) {
            onView(withId(R.id.wallet_eye)).perform(click());
            onView(withId(R.id.wallet_balance)).check(matches(withText(R.string.m23_hidden)));
            Shots.take("b_m23_hidden");
            onView(withId(R.id.wallet_eye)).perform(click());
            onView(withId(R.id.wallet_balance)).check(matches(withText(R.string.m23_balance)));
        }
    }

    @Test
    public void m31_marqueeRunsAndPlayToggles() {
        try (ActivityScenario<ScreenActivity> s = Screens.open(R.layout.m31_player)) {
            s.onActivity(a -> assertTrue(a.findViewById(R.id.player_title).isSelected()));
            onView(withId(R.id.player_play)).perform(click());
            s.onActivity(a -> assertEquals(a.getString(R.string.cd_play),
                    a.findViewById(R.id.player_play).getContentDescription().toString()));
        }
    }

    @Test
    public void m36_tapTogglesTheControls() {
        try (ActivityScenario<ScreenActivity> s = Screens.open(R.layout.m36_video_controls)) {
            onView(withId(R.id.video_pause)).check(matches(isDisplayed()));
            // Tap the video off-centre: the centre is the pause button.
            onView(withId(R.id.video_surface)).perform(tapAt(0.2f, 0.4f));
            onView(withId(R.id.video_pause)).check(matches(withEffectiveVisibility(Visibility.GONE)));
            onView(withId(R.id.video_fullscreen)).check(matches(withEffectiveVisibility(Visibility.GONE)));
            Shots.take("b_m36_controls_hidden");
            onView(withId(R.id.video_surface)).perform(tapAt(0.2f, 0.4f));
            onView(withId(R.id.video_pause)).check(matches(isDisplayed()));
        }
    }

    /** A tap at a point given as fractions of the view's width and height. */
    private static GeneralClickAction tapAt(float fx, float fy) {
        return new GeneralClickAction(Tap.SINGLE, v -> {
            int[] xy = new int[2];
            v.getLocationOnScreen(xy);
            return new float[] {xy[0] + v.getWidth() * fx, xy[1] + v.getHeight() * fy};
        }, Press.FINGER, 0, 0);
    }

    /** Drags from the view's centre to a point the given number of its widths to the left. */
    private static GeneralSwipeAction dragLeft(float widths) {
        CoordinatesProvider from = v -> centre(v);
        CoordinatesProvider to = v -> {
            float[] c = centre(v);
            return new float[] {c[0] - v.getWidth() * widths, c[1]};
        };
        return new GeneralSwipeAction(Swipe.SLOW, from, to, Press.FINGER);
    }

    private static float[] centre(View v) {
        int[] xy = new int[2];
        v.getLocationOnScreen(xy);
        return new float[] {xy[0] + v.getWidth() / 2f, xy[1] + v.getHeight() / 2f};
    }

    @Test
    public void m37_swipeLeftCancelsTheRecording() {
        try (ActivityScenario<ScreenActivity> s = Screens.open(R.layout.m37_voice_record)) {
            SystemClock.sleep(1300);
            assertFalse("the timer is running", text(s, R.id.voice_timer).equals("0:00"));
            Shots.take("b_m37_recording");
            onView(withId(R.id.voice_mic)).perform(dragLeft(Behaviors.CANCEL_DRAG + 1));
            onView(withId(R.id.voice_status)).check(matches(withText(R.string.m37_cancelled)));
            onView(withId(R.id.voice_timer)).check(matches(withText("0:00")));
        }
    }

    @Test
    public void m38_photosNumberedInPickOrder() {
        try (ActivityScenario<ScreenActivity> s = Screens.open(R.layout.m38_gallery)) {
            onView(withId(R.id.gallery_5)).perform(click());
            onView(withId(R.id.gallery_1)).perform(click());
            onView(withId(R.id.gallery_9)).perform(click());
            onView(withId(R.id.gallery_badge_5)).check(matches(withText("1")));
            onView(withId(R.id.gallery_badge_1)).check(matches(withText("2")));
            onView(withId(R.id.gallery_badge_9)).check(matches(withText("3")));
            onView(withId(R.id.gallery_count)).check(matches(withText("Выбрано: 3")));
            Shots.take("b_m38_three_picked");
            onView(withId(R.id.gallery_1)).perform(click());
            onView(withId(R.id.gallery_badge_1)).check(matches(withEffectiveVisibility(Visibility.GONE)));
            onView(withId(R.id.gallery_badge_9)).check(matches(withText("2")));
        }
    }

    @Test
    public void m40_playsInsideTheFeed() {
        try (ActivityScenario<ScreenActivity> s = Screens.open(R.layout.m40_podcast)) {
            onView(withId(R.id.podcast_play_1)).perform(click());
            onView(withId(R.id.podcast_progress_1)).check(matches(isDisplayed()));
            s.onActivity(a -> assertEquals(a.getString(R.string.cd_pause),
                    a.findViewById(R.id.podcast_play_1).getContentDescription().toString()));
            Shots.take("b_m40_playing");
        }
    }

    @Test
    public void m41_oneTariffAtATime() {
        try (ActivityScenario<ScreenActivity> s = Screens.open(R.layout.m41_taxi)) {
            onView(withId(R.id.tariff_3)).perform(click());
            s.onActivity(a -> {
                assertTrue(((MaterialCardView) a.findViewById(R.id.tariff_3)).isChecked());
                assertFalse(((MaterialCardView) a.findViewById(R.id.tariff_2)).isChecked());
            });
            Shots.take("b_m41_business");
        }
    }

    @Test
    public void m47_resendLockedWhileTheCodeIsValid() {
        try (ActivityScenario<ScreenActivity> s = Screens.open(R.layout.m47_confirm_otp)) {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            onView(withId(R.id.confirm_timer)).check(matches(withText(startsWith("Код действует ещё 1:00"))));
            SystemClock.sleep(2100);
            onView(withId(R.id.confirm_timer)).check(matches(withText(startsWith("Код действует ещё 0:5"))));
            onView(withId(R.id.confirm_resend)).check(matches(not(isEnabled())));
        }
    }

    @Test
    public void m49_stepsForwardAndBack() {
        try (ActivityScenario<ScreenActivity> s = Screens.open(R.layout.m49_insurance)) {
            onView(withId(R.id.ins_step)).check(matches(withText("Шаг 1 из 4")));
            onView(withId(R.id.ins_back)).check(matches(not(isEnabled())));
            onView(withId(R.id.ins_next)).perform(click(), click(), click());
            onView(withId(R.id.ins_step)).check(matches(withText("Шаг 4 из 4")));
            onView(withId(R.id.ins_next)).check(matches(withText(R.string.m49_finish)));
            Shots.take("b_m49_last_step");
            onView(withId(R.id.ins_back)).perform(click());
            onView(withId(R.id.ins_step)).check(matches(withText("Шаг 3 из 4")));
        }
    }

    @Test
    public void m50_sliderAndAccordion() {
        try (ActivityScenario<ScreenActivity> s = Screens.open(R.layout.m50_product)) {
            onView(withId(R.id.product_counter)).check(matches(withText("1 / 3")));
            onView(withId(R.id.product_pager)).perform(swipeLeft());
            onView(withId(R.id.product_counter)).check(matches(withText("2 / 3")));
            onView(withId(R.id.product_description_header)).perform(scrollTo(), click());
            onView(withId(R.id.product_description)).check(matches(isDisplayed()));
            Shots.take("b_m50_description_open");
        }
    }
}

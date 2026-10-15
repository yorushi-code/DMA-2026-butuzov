package dev.yorushi.dma.task5;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import java.util.List;

/**
 * Every item of the assignment, in order: the five exercises of each of topics
 * 1-6 and the 50 mixed screens. Topic 7 (optimisation) has no exercises of its
 * own; it is applied to item 50 and described in docs/task5.
 */
public final class Catalog {

    private Catalog() {
    }

    /** Code an item needs on top of its layout: click handlers, timers, adapters. */
    public interface Behavior {
        void attach(ScreenActivity screen);
    }

    public static final class Entry {
        @StringRes public final int section;
        /** "1.3" for a topic exercise, "17" for a mixed item. */
        public final String label;
        @StringRes public final int title;
        @LayoutRes public final int layout;
        /** Draws its own top bar over the content, so the action bar is hidden. */
        public final boolean immersive;
        public final Behavior behavior;

        Entry(int section, String label, int title, int layout, boolean immersive, Behavior behavior) {
            this.section = section;
            this.label = label;
            this.title = title;
            this.layout = layout;
            this.immersive = immersive;
            this.behavior = behavior;
        }
    }

    private static Entry e(int section, String label, int title, int layout) {
        return new Entry(section, label, title, layout, false, null);
    }

    private static Entry e(int section, String label, int title, int layout, Behavior behavior) {
        return new Entry(section, label, title, layout, false, behavior);
    }

    private static Entry full(int section, String label, int title, int layout, Behavior behavior) {
        return new Entry(section, label, title, layout, true, behavior);
    }

    public static final List<Entry> ENTRIES = List.of(
            e(R.string.section_t1, "1.1", R.string.t1_1, R.layout.t1_1_pin_row),
            e(R.string.section_t1, "1.2", R.string.t1_2, R.layout.t1_2_split),
            e(R.string.section_t1, "1.3", R.string.t1_3, R.layout.t1_3_search),
            e(R.string.section_t1, "1.4", R.string.t1_4, R.layout.t1_4_titles),
            e(R.string.section_t1, "1.5", R.string.t1_5, R.layout.t1_5_login),

            e(R.string.section_t2, "2.1", R.string.t2_1, R.layout.t2_1_avatar_badge),
            e(R.string.section_t2, "2.2", R.string.t2_2, R.layout.t2_2_button_overlay, Behaviors::submitOverlay),
            e(R.string.section_t2, "2.3", R.string.t2_3, R.layout.t2_3_fab, Behaviors::fab),
            e(R.string.section_t2, "2.4", R.string.t2_4, R.layout.t2_4_fragment_container, Behaviors::fragmentContainer),
            e(R.string.section_t2, "2.5", R.string.t2_5, R.layout.t2_5_video_preview),

            e(R.string.section_t3, "3.1", R.string.t3_1, R.layout.t3_1_chat_bubble),
            e(R.string.section_t3, "3.2", R.string.t3_2, R.layout.t3_2_top_bar),
            e(R.string.section_t3, "3.3", R.string.t3_3, R.layout.t3_3_product),
            e(R.string.section_t3, "3.4", R.string.t3_4, R.layout.t3_4_music_item),
            e(R.string.section_t3, "3.5", R.string.t3_5, R.layout.t3_5_baseline),

            e(R.string.section_t4, "4.1", R.string.t4_1, R.layout.t4_1_ratio_banner),
            e(R.string.section_t4, "4.2", R.string.t4_2, R.layout.t4_2_packed_chain),
            e(R.string.section_t4, "4.3", R.string.t4_3, R.layout.t4_3_barrier),
            e(R.string.section_t4, "4.4", R.string.t4_4, R.layout.t4_4_guidelines),
            e(R.string.section_t4, "4.5", R.string.t4_5, R.layout.t4_5_group, Behaviors::groupToggle),

            e(R.string.section_t5, "5.1", R.string.t5_1, R.layout.t5_1_stories),
            e(R.string.section_t5, "5.2", R.string.t5_2, R.layout.t5_2_license),
            e(R.string.section_t5, "5.3", R.string.t5_3, R.layout.t5_3_keyboard_form),
            e(R.string.section_t5, "5.4", R.string.t5_4, R.layout.t5_4_no_scrollbar),
            e(R.string.section_t5, "5.5", R.string.t5_5, R.layout.t5_5_nested_lists),

            e(R.string.section_t6, "6.1", R.string.t6_1, R.layout.t6_1_include_toolbar),
            e(R.string.section_t6, "6.2", R.string.t6_2, R.layout.t6_2_merge_card),
            e(R.string.section_t6, "6.3", R.string.t6_3, R.layout.t6_3_view_stub, Behaviors::networkStub),
            e(R.string.section_t6, "6.4", R.string.t6_4, R.layout.t6_4_include_override),
            e(R.string.section_t6, "6.5", R.string.t6_5_cart, R.layout.t6_5_cart),
            e(R.string.section_t6, "6.5", R.string.t6_5_reorder, R.layout.t6_5_reorder),

            e(R.string.section_b1, "1", R.string.m01, R.layout.m01_marketplace),
            e(R.string.section_b1, "2", R.string.m02, R.layout.m02_restaurant),
            e(R.string.section_b1, "3", R.string.m03, R.layout.m03_promo_banner),
            e(R.string.section_b1, "4", R.string.m04, R.layout.m04_bank_card),
            e(R.string.section_b1, "5", R.string.m05, R.layout.m05_car_specs),
            e(R.string.section_b1, "6", R.string.m06, R.layout.m06_flight_ticket),
            e(R.string.section_b1, "7", R.string.m07, R.layout.m07_track),
            e(R.string.section_b1, "8", R.string.m08, R.layout.m08_tariff, Behaviors::tariff),
            e(R.string.section_b1, "9", R.string.m09, R.layout.m09_subscription),
            e(R.string.section_b1, "10", R.string.m10, R.layout.m10_hotel),

            e(R.string.section_b2, "11", R.string.m11, R.layout.m11_phone_transfer, Behaviors::phoneTransfer),
            e(R.string.section_b2, "12", R.string.m12, R.layout.m12_filter, Behaviors::priceFilter),
            e(R.string.section_b2, "13", R.string.m13, R.layout.m13_password, Behaviors::passwordRules),
            e(R.string.section_b2, "14", R.string.m14, R.layout.m14_address),
            e(R.string.section_b2, "15", R.string.m15, R.layout.m15_review),
            e(R.string.section_b2, "16", R.string.m16, R.layout.m16_privacy),
            e(R.string.section_b2, "17", R.string.m17, R.layout.m17_card_input),
            e(R.string.section_b2, "18", R.string.m18, R.layout.m18_feedback, Behaviors::feedbackTopics),
            e(R.string.section_b2, "19", R.string.m19, R.layout.m19_sms_code, Behaviors::smsCode),
            e(R.string.section_b2, "20", R.string.m20, R.layout.m20_profile_edit),

            e(R.string.section_b3, "21", R.string.m21, R.layout.m21_profile_header),
            e(R.string.section_b3, "22", R.string.m22, R.layout.m22_fitness),
            e(R.string.section_b3, "23", R.string.m23, R.layout.m23_wallet, Behaviors::walletBalance),
            e(R.string.section_b3, "24", R.string.m24, R.layout.m24_analytics),
            e(R.string.section_b3, "25", R.string.m25, R.layout.m25_battery),
            e(R.string.section_b3, "26", R.string.m26, R.layout.m26_achievements),
            e(R.string.section_b3, "27", R.string.m27, R.layout.m27_driver),
            e(R.string.section_b3, "28", R.string.m28, R.layout.m28_orders),
            e(R.string.section_b3, "29", R.string.m29, R.layout.m29_smart_home),
            e(R.string.section_b3, "30", R.string.m30, R.layout.m30_medical),

            e(R.string.section_b4, "31", R.string.m31, R.layout.m31_player, Behaviors::player),
            e(R.string.section_b4, "32", R.string.m32, R.layout.m32_chat),
            full(R.string.section_b4, "33", R.string.m33, R.layout.m33_reels, null),
            full(R.string.section_b4, "34", R.string.m34, R.layout.m34_call, Behaviors::callBlur),
            e(R.string.section_b4, "35", R.string.m35, R.layout.m35_comments),
            e(R.string.section_b4, "36", R.string.m36, R.layout.m36_video_controls, Behaviors::videoControls),
            e(R.string.section_b4, "37", R.string.m37, R.layout.m37_voice_record, Behaviors::voiceRecord),
            e(R.string.section_b4, "38", R.string.m38, R.layout.m38_gallery, Behaviors::gallery),
            full(R.string.section_b4, "39", R.string.m39, R.layout.m39_stories, null),
            e(R.string.section_b4, "40", R.string.m40, R.layout.m40_podcast, Behaviors::podcast),

            full(R.string.section_b5, "41", R.string.m41, R.layout.m41_taxi, Behaviors::taxi),
            e(R.string.section_b5, "42", R.string.m42, R.layout.m42_cart_bar),
            e(R.string.section_b5, "43", R.string.m43, R.layout.m43_onboarding),
            full(R.string.section_b5, "44", R.string.m44, R.layout.m44_qr, null),
            e(R.string.section_b5, "45", R.string.m45, R.layout.m45_empty_state),
            e(R.string.section_b5, "46", R.string.m46, R.layout.m46_quiz_result),
            e(R.string.section_b5, "47", R.string.m47, R.layout.m47_confirm_otp, Behaviors::confirmCountdown),
            e(R.string.section_b5, "48", R.string.m48, R.layout.m48_compare),
            e(R.string.section_b5, "49", R.string.m49, R.layout.m49_insurance, Behaviors::insuranceSteps),
            full(R.string.section_b5, "50", R.string.m50, R.layout.m50_product, Behaviors::productPage));
}

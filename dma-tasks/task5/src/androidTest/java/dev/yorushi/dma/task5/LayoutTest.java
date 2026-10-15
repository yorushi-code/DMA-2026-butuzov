package dev.yorushi.dma.task5;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.chip.ChipGroup;
import dev.yorushi.dma.task5.widget.IconCardView;
import dev.yorushi.dma.task5.widget.ScannerOverlayView;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * The geometry each layout promises, measured on the device: equal weights, ratios,
 * guidelines, barriers, overlays, and the flatness the optimisation topic asks for.
 */
@RunWith(AndroidJUnit4.class)
public class LayoutTest {

    /** One pixel of rounding either way. */
    private static final int PX = 1;

    private static void near(String what, int expected, int actual) {
        assertTrue(what + ": expected " + expected + ", was " + actual, Math.abs(expected - actual) <= PX);
    }

    // ---- Topic 1 ---------------------------------------------------------------------

    @Test
    public void t1_1_pinButtonsShareTheRowAndAreRound() {
        Screens.check(R.layout.t1_1_pin_row, a -> {
            View one = a.findViewById(R.id.pin_1);
            View two = a.findViewById(R.id.pin_2);
            View three = a.findViewById(R.id.pin_3);
            near("equal widths", one.getWidth(), two.getWidth());
            near("equal widths", one.getWidth(), three.getWidth());
            near("square", one.getWidth(), one.getHeight());
        });
    }

    @Test
    public void t1_2_halvesAreEqual() {
        Screens.check(R.layout.t1_2_split, a ->
                near("50/50", a.findViewById(R.id.split_top).getHeight(), a.findViewById(R.id.split_bottom).getHeight()));
    }

    @Test
    public void t1_3_fieldTakesWhatTheIconLeaves() {
        Screens.check(R.layout.t1_3_search, a -> {
            View row = a.findViewById(R.id.search_row);
            View field = a.findViewById(R.id.search_field);
            View icon = a.findViewById(R.id.search_button);
            near("icon 48dp", Screens.dp(icon, 48), icon.getWidth());
            near("field fills the rest", row.getWidth() - row.getPaddingStart() - icon.getWidth(), field.getWidth());
        });
    }

    @Test
    public void t1_4_titleCentredSubtitleAtTheEnd() {
        Screens.check(R.layout.t1_4_titles, a -> {
            View title = a.findViewById(R.id.title_center);
            View subtitle = a.findViewById(R.id.subtitle_end);
            View parent = (View) title.getParent();
            near("centred", parent.getWidth() / 2, title.getLeft() + title.getWidth() / 2);
            near("at the end", parent.getWidth() - parent.getPaddingEnd(), subtitle.getRight());
        });
    }

    @Test
    public void t1_5_dividerIsOneDp() {
        Screens.check(R.layout.t1_5_login, a ->
                near("1dp", Screens.dp(a.findViewById(R.id.login_divider), 1), a.findViewById(R.id.login_divider).getHeight()));
    }

    // ---- Topic 2 ---------------------------------------------------------------------

    @Test
    public void t2_1_badgeInTheAvatarsBottomEndCorner() {
        Screens.check(R.layout.t2_1_avatar_badge, a -> {
            View frame = a.findViewById(R.id.avatar_frame);
            View dot = a.findViewById(R.id.online_dot);
            near("bottom", frame.getHeight() - Screens.dp(dot, 2), dot.getBottom());
            near("end", frame.getWidth() - Screens.dp(dot, 2), dot.getRight());
        });
    }

    @Test
    public void t2_3_fabFloatsInTheCorner() {
        Screens.check(R.layout.t2_3_fab, a -> {
            View fab = a.findViewById(R.id.fab);
            View root = Screens.root(a);
            near("24dp from the end", root.getWidth() - Screens.dp(fab, 24), fab.getRight());
            near("24dp from the bottom", root.getHeight() - Screens.dp(fab, 24), fab.getBottom());
        });
    }

    @Test
    public void t2_5_playIconCentred() {
        Screens.check(R.layout.t2_5_video_preview, a -> {
            View frame = a.findViewById(R.id.video_frame);
            View play = a.findViewById(R.id.video_play);
            near("x", frame.getWidth() / 2, play.getLeft() + play.getWidth() / 2);
            near("y", frame.getHeight() / 2, play.getTop() + play.getHeight() / 2);
        });
    }

    // ---- Topic 3 ---------------------------------------------------------------------

    @Test
    public void t3_1_timeAtTheBottomRightOfTheText() {
        Screens.check(R.layout.t3_1_chat_bubble, a -> {
            View text = a.findViewById(R.id.bubble_text);
            View time = a.findViewById(R.id.bubble_time);
            near("bottoms aligned", text.getBottom(), time.getBottom());
            assertTrue("right of the text", time.getLeft() >= text.getRight());
        });
    }

    @Test
    public void t3_2_titleCentredBetweenEdgeButtons() {
        Screens.check(R.layout.t3_2_top_bar, a -> {
            View bar = a.findViewById(R.id.top_bar);
            View title = a.findViewById(R.id.bar_title);
            near("centred", bar.getWidth() / 2, title.getLeft() + title.getWidth() / 2);
            near("back at start", bar.getPaddingStart(), a.findViewById(R.id.bar_back).getLeft());
            near("settings at end", bar.getWidth() - bar.getPaddingEnd(), a.findViewById(R.id.bar_settings).getRight());
        });
    }

    @Test
    public void t3_3_priceSitsOnThePhotosBottom() {
        Screens.check(R.layout.t3_3_product, a -> {
            View photo = a.findViewById(R.id.product_photo);
            near("name at photo top", photo.getTop(), a.findViewById(R.id.product_name).getTop());
            near("price at photo bottom", photo.getBottom(), a.findViewById(R.id.product_price).getBottom());
        });
    }

    @Test
    public void t3_5_sameBaseline() {
        Screens.check(R.layout.t3_5_baseline, a -> {
            TextView big = a.findViewById(R.id.price_big);
            TextView unit = a.findViewById(R.id.price_unit);
            near("baseline", big.getTop() + big.getBaseline(), unit.getTop() + unit.getBaseline());
        });
    }

    // ---- Topic 4 ---------------------------------------------------------------------

    @Test
    public void t4_1_bannerIsSixteenByNine() {
        Screens.check(R.layout.t4_1_ratio_banner, a -> {
            View banner = a.findViewById(R.id.ratio_banner);
            near("full width", Screens.root(a).getWidth(), banner.getWidth());
            near("16:9", banner.getWidth() * 9 / 16, banner.getHeight());
        });
    }

    @Test
    public void t4_2_chainBiasedToTheTop() {
        Screens.check(R.layout.t4_2_packed_chain, a -> {
            View root = Screens.root(a);
            View title = a.findViewById(R.id.chain_title);
            View subtitle = a.findViewById(R.id.chain_subtitle);
            int free = root.getHeight() - root.getPaddingTop() - root.getPaddingBottom()
                    - (subtitle.getBottom() - title.getTop());
            near("packed", title.getBottom() + Screens.dp(title, 8), subtitle.getTop());
            near("bias 0.2", root.getPaddingTop() + Math.round(free * 0.2f), title.getTop());
        });
    }

    @Test
    public void t4_3_buttonFollowsTheLongerLabel() {
        Screens.check(R.layout.t4_3_barrier, a -> {
            int gap = Screens.dp(a.findViewById(R.id.barrier_button), 16);
            // First copy: the name is longer.
            near("after the name", a.findViewById(R.id.barrier_name).getRight() + gap,
                    a.findViewById(R.id.barrier_button).getLeft());
            // Second copy: the role is longer.
            near("after the role", a.findViewById(R.id.barrier2_role).getRight() + gap,
                    a.findViewById(R.id.barrier2_button).getLeft());
        });
    }

    @Test
    public void t4_4_buttonFrom15To85Percent() {
        Screens.check(R.layout.t4_4_guidelines, a -> {
            int width = Screens.root(a).getWidth();
            View button = a.findViewById(R.id.guide_button);
            near("15%", Math.round(width * 0.15f), button.getLeft());
            near("85%", Math.round(width * 0.85f), button.getRight());
        });
    }

    // ---- Topic 5 ---------------------------------------------------------------------

    @Test
    public void t5_1_storiesAreWiderThanTheScreen() {
        Screens.check(R.layout.t5_1_stories, a ->
                assertTrue(a.findViewById(R.id.stories_row).getWidth() > a.findViewById(R.id.stories_scroll).getWidth()));
    }

    @Test
    public void t5_2_buttonOutsideTheScroll() {
        Screens.check(R.layout.t5_2_license, a -> {
            View scroll = a.findViewById(R.id.license_scroll);
            View accept = a.findViewById(R.id.license_accept);
            assertFalse("not inside the ScrollView", accept.getParent() == ((ViewGroup) scroll).getChildAt(0));
            assertTrue("below it", accept.getTop() >= scroll.getBottom());
            near("at the bottom", Screens.root(a).getHeight() - Screens.dp(accept, 16), accept.getBottom());
            assertTrue("the text needs scrolling", scroll.canScrollVertically(1));
        });
    }

    @Test
    public void t5_4_noScrollbar() {
        Screens.check(R.layout.t5_4_no_scrollbar, a -> {
            View scroll = a.findViewById(R.id.no_scrollbar);
            assertFalse(scroll.isVerticalScrollBarEnabled());
            assertTrue("still scrolls", scroll.canScrollVertically(1));
        });
    }

    // ---- Topic 6 ---------------------------------------------------------------------

    @Test
    public void t6_1_sharedToolbarOnFourScreens() {
        int[] screens = {R.layout.t6_1_include_toolbar, R.layout.t6_3_view_stub, R.layout.t6_5_cart, R.layout.t6_5_reorder};
        for (int layout : screens) {
            Screens.check(layout, a -> {
                TextView title = a.findViewById(R.id.toolbar_title);
                assertEquals(a.getString(a.entry().title), title.getText().toString());
                assertTrue(a.findViewById(R.id.toolbar_back).hasOnClickListeners());
            });
        }
    }

    @Test
    public void t6_2_mergeAddsNoExtraLevel() {
        Screens.check(R.layout.t6_2_merge_card, a -> {
            IconCardView card = a.findViewById(R.id.icon_card_1);
            assertEquals("icon and text are the card's own children", 2, card.getChildCount());
            assertTrue(card.getChildAt(0) instanceof ImageView);
            assertEquals(a.getString(R.string.t6_2_card_1), ((TextView) card.getChildAt(1)).getText().toString());
        });
    }

    @Test
    public void t6_3_stubIsNotInflatedUntilNeeded() {
        Screens.check(R.layout.t6_3_view_stub, a -> {
            assertNull(a.findViewById(R.id.no_internet));
            assertTrue(a.findViewById(R.id.stub_no_internet) != null);
        });
    }

    @Test
    public void t6_4_includeParamsOverridden() {
        Screens.check(R.layout.t6_4_include_override, a -> {
            View full = a.findViewById(R.id.info_full);
            View compact = a.findViewById(R.id.info_compact);
            int width = Screens.root(a).getWidth();
            near("match_parent with 16dp margins", width - 2 * Screens.dp(full, 16), full.getWidth());
            assertTrue("wrap_content is narrower", compact.getWidth() < full.getWidth());
            near("40dp margin", Screens.dp(compact, 40), compact.getLeft());
        });
    }

    @Test
    public void t6_5_sameFooterOnBothScreens() {
        for (int layout : new int[] {R.layout.t6_5_cart, R.layout.t6_5_reorder}) {
            Screens.check(layout, a -> {
                View footer = a.findViewById(R.id.cart_footer);
                near("at the bottom", Screens.root(a).getHeight(), footer.getBottom());
                assertEquals(a.getString(R.string.t6_5_total), ((TextView) a.findViewById(R.id.cart_total)).getText().toString());
            });
        }
    }

    // ---- Mixed items -----------------------------------------------------------------

    @Test
    public void m01_photoSquareBadgeInTheCorner() {
        Screens.check(R.layout.m01_marketplace, a -> {
            View card = a.findViewById(R.id.market_card_1);
            View frame = card.findViewById(R.id.market_photo_frame);
            View badge = card.findViewById(R.id.market_badge);
            near("1:1", frame.getWidth(), frame.getHeight());
            near("top-start", Screens.dp(badge, 8), badge.getLeft());
            near("equal cards", card.getWidth(), a.findViewById(R.id.market_card_2).getWidth());
        });
    }

    @Test
    public void m03_artSticksOutOfTheCard() {
        Screens.check(R.layout.m03_promo_banner, a ->
                assertTrue(Screens.onScreen(a.findViewById(R.id.promo_art))[1] < Screens.onScreen(a.findViewById(R.id.promo_card))[1]));
    }

    @Test
    public void m04_cardIs85By54() {
        Screens.check(R.layout.m04_bank_card, a -> {
            View card = a.findViewById(R.id.bank_card);
            near("85:54", Math.round(card.getWidth() * 54f / 85f), card.getHeight());
            View logo = a.findViewById(R.id.bank_logo_right);
            near("logo bottom-end", card.getRight() - Screens.dp(logo, 20), logo.getRight());
        });
    }

    @Test
    public void m05_specGridIsFlat() {
        Screens.check(R.layout.m05_car_specs, a -> {
            ViewGroup grid = a.findViewById(R.id.spec_grid);
            assertEquals("one level: no nested layouts", 1, Screens.depth(grid));
            near("columns split at 50%", grid.getWidth() / 2, a.findViewById(R.id.spec_engine_icon).getLeft());
        });
    }

    @Test
    public void m09_hitLabelSitsOnTheBorder() {
        Screens.check(R.layout.m09_subscription, a -> {
            View card = a.findViewById(R.id.sub_card);
            View hit = a.findViewById(R.id.sub_hit);
            near("centred on the top border", card.getTop(), hit.getTop() + hit.getHeight() / 2);
        });
    }

    @Test
    public void m14_houseRowIsOneOneOneOne() {
        Screens.check(R.layout.m14_address, a -> {
            int w = a.findViewById(R.id.addr_house_layout).getWidth();
            near("entrance", w, a.findViewById(R.id.addr_entrance_layout).getWidth());
            near("floor", w, a.findViewById(R.id.addr_floor_layout).getWidth());
            near("flat", w, a.findViewById(R.id.addr_flat_layout).getWidth());
        });
    }

    @Test
    public void m19_cellsCentred() {
        Screens.check(R.layout.m19_sms_code, a -> {
            View cells = a.findViewById(R.id.sms_cells);
            View root = Screens.root(a);
            near("horizontally", root.getWidth() / 2, cells.getLeft() + cells.getWidth() / 2);
            View first = a.findViewById(R.id.sms_digit_1);
            near("square-ish cells of 44dp", Screens.dp(first, 44), first.getWidth());
        });
    }

    @Test
    public void m21_countersHaveEqualWeights() {
        Screens.check(R.layout.m21_profile_header, a -> {
            int w = a.findViewById(R.id.count_posts).getWidth();
            near("followers", w, a.findViewById(R.id.count_followers).getWidth());
            near("following", w, a.findViewById(R.id.count_following).getWidth());
        });
    }

    @Test
    public void m22_stepsCentredInTheRing() {
        Screens.check(R.layout.m22_fitness, a -> {
            View ring = a.findViewById(R.id.fit_ring);
            View steps = a.findViewById(R.id.fit_steps);
            near("x", ring.getLeft() + ring.getWidth() / 2, steps.getLeft() + steps.getWidth() / 2);
            assertTrue("calories left of the ring", a.findViewById(R.id.fit_kcal).getRight() <= ring.getLeft());
            assertTrue("km right of the ring", a.findViewById(R.id.fit_km).getLeft() >= ring.getRight());
        });
    }

    @Test
    public void m25_fillIsTheChargeLevel() {
        Screens.check(R.layout.m25_battery, a -> {
            View outline = a.findViewById(R.id.battery_outline);
            View fill = a.findViewById(R.id.battery_fill);
            int inner = outline.getWidth() - outline.getPaddingStart() - outline.getPaddingEnd();
            near("68% of the outline", Math.round(inner * 0.68f), fill.getWidth());
        });
    }

    @Test
    public void m26_lockedBadgesWearALock() {
        Screens.check(R.layout.m26_achievements, a -> {
            assertNull(a.findViewById(R.id.badge_1).findViewById(R.id.badge_lock_6));
            View tile = a.findViewById(R.id.badge_6);
            near("square tile", tile.getWidth(), tile.getHeight());
            assertTrue(a.findViewById(R.id.badge_lock_6).isShown());
        });
    }

    @Test
    public void m29_tilesAreEqualSquares() {
        Screens.check(R.layout.m29_smart_home, a -> {
            View one = a.findViewById(R.id.room_1);
            View two = a.findViewById(R.id.room_2);
            near("square", one.getWidth(), one.getHeight());
            near("equal", one.getWidth(), two.getWidth());
        });
    }

    @Test
    public void m30_tagsWrap() {
        Screens.check(R.layout.m30_medical, a -> {
            ChipGroup tags = a.findViewById(R.id.med_allergies);
            View first = tags.getChildAt(0);
            View last = tags.getChildAt(tags.getChildCount() - 1);
            assertTrue("more than one line", last.getTop() > first.getTop());
        });
    }

    @Test
    public void m32_bubblesOnTheirSides() {
        Screens.check(R.layout.m32_chat, a -> {
            View list = a.findViewById(R.id.chat_list);
            View out = a.findViewById(R.id.chat_out_1);
            near("outgoing at the end", list.getWidth() - list.getPaddingEnd(), out.getRight());
            assertTrue("incoming on the start side",
                    Screens.onScreen(a.findViewById(R.id.chat_in_1))[0] < list.getWidth() / 2);
        });
    }

    @Test
    public void m33_actionsDownTheRightEdge() {
        Screens.check(R.layout.m33_reels, a -> {
            View actions = a.findViewById(R.id.reels_actions);
            View root = Screens.root(a);
            near("end", root.getWidth() - Screens.dp(actions, 8), actions.getRight());
            assertTrue("stacked", a.findViewById(R.id.reels_sound).getTop() > a.findViewById(R.id.reels_like).getTop());
        });
    }

    @Test
    public void m35_repliesAreIndented() {
        Screens.check(R.layout.m35_comments, a -> {
            int root = a.findViewById(R.id.comment_avatar_1).getLeft();
            int reply = a.findViewById(R.id.comment_avatar_2).getLeft();
            int nested = a.findViewById(R.id.comment_avatar_3).getLeft();
            near("one level", root + Screens.dp(a.findViewById(R.id.comment_1), 44), reply);
            near("two levels", reply + Screens.dp(a.findViewById(R.id.comment_1), 44), nested);
        });
    }

    @Test
    public void m36_controlsOnTheVideoEdges() {
        Screens.check(R.layout.m36_video_controls, a -> {
            View video = a.findViewById(R.id.video_surface);
            near("16:9", video.getWidth() * 9 / 16, video.getHeight());
            near("fullscreen bottom-end", video.getBottom(), a.findViewById(R.id.video_fullscreen).getBottom());
            View pause = a.findViewById(R.id.video_pause);
            near("pause centred", video.getTop() + video.getHeight() / 2, pause.getTop() + pause.getHeight() / 2);
        });
    }

    @Test
    public void m39_segmentsSplitEqually() {
        Screens.check(R.layout.m39_stories, a -> {
            LinearLayout bars = a.findViewById(R.id.story_bars);
            int w = bars.getChildAt(0).getWidth();
            for (int i = 1; i < bars.getChildCount(); i++) {
                near("segment " + i, w, bars.getChildAt(i).getWidth());
            }
        });
    }

    @Test
    public void m42_panelFixedListScrollsUnderIt() {
        Screens.check(R.layout.m42_cart_bar, a -> {
            View panel = a.findViewById(R.id.cart_panel);
            View scroll = a.findViewById(R.id.cart_scroll);
            near("panel at the bottom", Screens.root(a).getHeight(), panel.getBottom());
            near("padding = panel height", panel.getHeight(), scroll.getPaddingBottom());
        });
    }

    @Test
    public void m43_artIsFortyPercent() {
        Screens.check(R.layout.m43_onboarding, a ->
                near("40%", Math.round(Screens.root(a).getHeight() * 0.4f), a.findViewById(R.id.onboarding_art).getHeight()));
    }

    @Test
    public void m44_windowIsACentredSquare() {
        Screens.check(R.layout.m44_qr, a -> {
            ScannerOverlayView overlay = a.findViewById(R.id.qr_overlay);
            near("square", Math.round(overlay.window().width()), Math.round(overlay.window().height()));
            near("centred", overlay.getWidth() / 2, Math.round(overlay.window().centerX()));
        });
    }

    @Test
    public void m45_blockCentred() {
        Screens.check(R.layout.m45_empty_state, a -> {
            View root = Screens.root(a);
            int top = a.findViewById(R.id.empty_art).getTop();
            int bottom = a.findViewById(R.id.empty_refresh).getBottom();
            near("vertically", root.getHeight() / 2, (top + bottom) / 2);
        });
    }

    @Test
    public void m48_dividerAtHalf() {
        Screens.check(R.layout.m48_compare, a -> {
            View divider = a.findViewById(R.id.cmp_divider);
            View parent = (View) divider.getParent();
            near("50%", parent.getWidth() / 2, divider.getLeft() + divider.getWidth() / 2);
        });
    }

    @Test
    public void m49_onlyTheFormScrolls() {
        Screens.check(R.layout.m49_insurance, a -> {
            near("nav at the bottom", Screens.root(a).getHeight(), a.findViewById(R.id.ins_nav).getBottom());
            near("header at the top", 0, a.findViewById(R.id.ins_header).getTop());
            near("equal buttons", a.findViewById(R.id.ins_back).getWidth(), a.findViewById(R.id.ins_next).getWidth());
        });
    }

    /** Item 50 and topic 7: flat body, bare slider pages, fixed bars, no window background. */
    @Test
    public void m50_productionArchitecture() {
        Screens.check(R.layout.m50_product, a -> {
            ViewGroup body = a.findViewById(R.id.product_body);
            for (int i = 0; i < body.getChildCount(); i++) {
                View child = body.getChildAt(i);
                if (child instanceof ViewGroup && !(child instanceof ChipGroup) && !(child instanceof ViewPager2)) {
                    throw new AssertionError("nested container in the body: " + child);
                }
            }
            ViewPager2 pager = a.findViewById(R.id.product_pager);
            near("16:9", pager.getWidth() * 9 / 16, pager.getHeight());
            ViewGroup list = (ViewGroup) pager.getChildAt(0);
            assertTrue("a page is a bare ImageView", list.getChildAt(0) instanceof ImageView);
            View root = Screens.root(a);
            near("toolbar over the photo", 0, a.findViewById(R.id.product_toolbar).getTop());
            assertNull("toolbar has no background", a.findViewById(R.id.product_toolbar).getBackground());
            assertNull("body has no background", body.getBackground());
            assertNull("scroll has no background", a.findViewById(R.id.product_scroll).getBackground());
            near("bottom bar fixed", root.getHeight(), a.findViewById(R.id.product_bottom_bar).getBottom());
            assertNull("window background dropped", a.getWindow().getDecorView().getBackground());
        });
    }
}

package dev.yorushi.dma.task5;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import dev.yorushi.dma.task5.logic.Countdown;
import dev.yorushi.dma.task5.logic.PasswordRules;
import dev.yorushi.dma.task5.logic.PriceRange;
import dev.yorushi.dma.task5.logic.SelectionOrder;
import dev.yorushi.dma.task5.logic.Tariff;
import dev.yorushi.dma.task5.widget.WaveformView;
import java.util.Arrays;
import java.util.List;

/**
 * The code behind the items that do more than lay views out. Each method is attached
 * to its catalog entry and runs right after the entry's layout is inflated.
 */
final class Behaviors {

    /** Simulated network delay for T2.2. */
    static final long SUBMIT_DELAY_MS = 2000;

    private Behaviors() {
    }

    // ---- Topic 2 ----------------------------------------------------------------

    /** T2.2: while "sending", the button is disabled and the spinner is shown over it. */
    static void submitOverlay(ScreenActivity screen) {
        Button submit = screen.findViewById(R.id.submit);
        View progress = screen.findViewById(R.id.submit_progress);
        TextView result = screen.findViewById(R.id.submit_result);
        Handler handler = new Handler(Looper.getMainLooper());
        submit.setOnClickListener(v -> {
            submit.setEnabled(false);
            progress.setVisibility(View.VISIBLE);
            result.setText(null);
            handler.postDelayed(() -> {
                progress.setVisibility(View.GONE);
                submit.setEnabled(true);
                result.setText(R.string.t2_2_done);
            }, SUBMIT_DELAY_MS);
        });
    }

    static void fab(ScreenActivity screen) {
        screen.findViewById(R.id.fab).setOnClickListener(
                v -> Toast.makeText(screen, R.string.t2_3_added, Toast.LENGTH_SHORT).show());
    }

    /** T2.4: the empty container is real: a fragment is committed into it. */
    static void fragmentContainer(ScreenActivity screen) {
        if (screen.getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView) == null) {
            screen.getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainerView, new PlaceholderFragment())
                    .commitNow();
        }
    }

    // ---- Topic 4 ----------------------------------------------------------------

    /** T4.5: one line hides or shows all three views of the group. */
    static void groupToggle(ScreenActivity screen) {
        Group group = screen.findViewById(R.id.group);
        Button toggle = screen.findViewById(R.id.group_toggle);
        toggle.setOnClickListener(v -> {
            boolean visible = group.getVisibility() == View.VISIBLE;
            group.setVisibility(visible ? View.GONE : View.VISIBLE);
            toggle.setText(visible ? R.string.t4_5_toggle_show : R.string.t4_5_toggle);
        });
    }

    // ---- Topic 6 ----------------------------------------------------------------

    /** T6.3: the stub is inflated on first use; later it is only shown and hidden. */
    static void networkStub(ScreenActivity screen) {
        screen.findViewById(R.id.simulate_offline).setOnClickListener(v -> {
            ViewStub stub = screen.findViewById(R.id.stub_no_internet);
            View error = stub != null ? stub.inflate() : screen.findViewById(R.id.no_internet);
            error.setVisibility(View.VISIBLE);
            error.findViewById(R.id.retry).setOnClickListener(r -> error.setVisibility(View.GONE));
        });
    }

    // ---- Block 1 -----------------------------------------------------------------

    /** Item 8: the sliders show their values and the monthly fee follows them. */
    static void tariff(ScreenActivity screen) {
        Slider minutes = screen.findViewById(R.id.tariff_minutes_slider);
        Slider gigabytes = screen.findViewById(R.id.tariff_gb_slider);
        TextView minutesText = screen.findViewById(R.id.tariff_minutes);
        TextView gbText = screen.findViewById(R.id.tariff_gb);
        TextView price = screen.findViewById(R.id.tariff_price);
        Runnable update = () -> {
            int m = (int) minutes.getValue();
            int g = (int) gigabytes.getValue();
            minutesText.setText(screen.getString(R.string.m08_minutes, m));
            gbText.setText(screen.getString(R.string.m08_gb, g));
            price.setText(screen.getString(R.string.m08_price, Tariff.monthly(m, g)));
        };
        minutes.addOnChangeListener((s, v, fromUser) -> update.run());
        gigabytes.addOnChangeListener((s, v, fromUser) -> update.run());
        update.run();
    }

    // ---- Block 2 -----------------------------------------------------------------

    /** Item 11: a quick-sum button fills the amount field. */
    static void phoneTransfer(ScreenActivity screen) {
        EditText amount = screen.findViewById(R.id.transfer_amount);
        int[][] quick = {{R.id.quick_500, 500}, {R.id.quick_1000, 1000}, {R.id.quick_5000, 5000}};
        for (int[] q : quick) {
            int sum = q[1];
            screen.findViewById(q[0]).setOnClickListener(v -> {
                amount.setText(String.valueOf(sum));
                amount.setSelection(amount.length());
            });
        }
    }

    /** Item 12: slider -> fields while dragging; fields -> slider when editing ends; reset. */
    static void priceFilter(ScreenActivity screen) {
        RangeSlider range = screen.findViewById(R.id.filter_range);
        EditText from = screen.findViewById(R.id.filter_from);
        EditText to = screen.findViewById(R.id.filter_to);
        // Copied out as numbers: the defaults must survive the slider moving.
        float initialFrom = range.getValues().get(0);
        float initialTo = range.getValues().get(1);
        Runnable toFields = () -> {
            List<Float> v = range.getValues();
            from.setText(String.valueOf(Math.round(v.get(0))));
            to.setText(String.valueOf(Math.round(v.get(1))));
        };
        Runnable toSlider = () -> {
            int[] r = PriceRange.normalize(from.getText().toString(), to.getText().toString(),
                    (int) range.getValueFrom(), (int) range.getValueTo(), (int) range.getStepSize());
            range.setValues((float) r[0], (float) r[1]);
            toFields.run();
        };
        range.addOnChangeListener((s, v, fromUser) -> {
            if (fromUser) {
                toFields.run();
            }
        });
        View.OnFocusChangeListener commit = (v, hasFocus) -> {
            if (!hasFocus) {
                toSlider.run();
            }
        };
        from.setOnFocusChangeListener(commit);
        to.setOnFocusChangeListener(commit);
        from.setOnEditorActionListener((v, action, event) -> {
            toSlider.run();
            return false;
        });
        to.setOnEditorActionListener((v, action, event) -> {
            toSlider.run();
            return false;
        });
        screen.findViewById(R.id.filter_reset).setOnClickListener(v -> {
            range.setValues(initialFrom, initialTo);
            toFields.run();
        });
        toFields.run();
    }

    /** Item 13: every keystroke re-checks the rules; met ones turn green. */
    static void passwordRules(ScreenActivity screen) {
        EditText fresh = screen.findViewById(R.id.pass_new);
        EditText repeat = screen.findViewById(R.id.pass_repeat);
        TextView length = screen.findViewById(R.id.rule_length);
        TextView digit = screen.findViewById(R.id.rule_digit);
        TextView mixed = screen.findViewById(R.id.rule_case);
        TextView match = screen.findViewById(R.id.rule_match);
        Button save = screen.findViewById(R.id.pass_save);
        ColorStateList met = ColorStateList.valueOf(ContextCompat.getColor(screen, R.color.ok));
        ColorStateList unmet = ColorStateList.valueOf(ContextCompat.getColor(screen, R.color.text_muted));
        Runnable check = () -> {
            CharSequence p = fresh.getText();
            CharSequence r = repeat.getText();
            TextViewCompat.setCompoundDrawableTintList(length, PasswordRules.longEnough(p) ? met : unmet);
            TextViewCompat.setCompoundDrawableTintList(digit, PasswordRules.hasDigit(p) ? met : unmet);
            TextViewCompat.setCompoundDrawableTintList(mixed, PasswordRules.mixedCase(p) ? met : unmet);
            TextViewCompat.setCompoundDrawableTintList(match, PasswordRules.matches(p, r) ? met : unmet);
            save.setEnabled(PasswordRules.allMet(p, r));
        };
        fresh.addTextChangedListener(afterChange(check));
        repeat.addTextChangedListener(afterChange(check));
        check.run();
    }

    /** Item 18: the attach button reveals the attachment row; its cross removes it. */
    static void feedbackTopics(ScreenActivity screen) {
        View file = screen.findViewById(R.id.feedback_file);
        View attach = screen.findViewById(R.id.feedback_attach);
        attach.setOnClickListener(v -> {
            file.setVisibility(View.VISIBLE);
            attach.setVisibility(View.GONE);
        });
        screen.findViewById(R.id.feedback_file_remove).setOnClickListener(v -> {
            file.setVisibility(View.GONE);
            attach.setVisibility(View.VISIBLE);
        });
    }

    static final int[] SMS_CELLS = {
        R.id.sms_digit_1, R.id.sms_digit_2, R.id.sms_digit_3,
        R.id.sms_digit_4, R.id.sms_digit_5, R.id.sms_digit_6,
    };

    /** Item 19: a digit moves the focus on; delete in an empty cell moves it back. */
    static void smsCode(ScreenActivity screen) {
        EditText[] cells = new EditText[SMS_CELLS.length];
        for (int i = 0; i < cells.length; i++) {
            cells[i] = screen.findViewById(SMS_CELLS[i]);
        }
        for (int i = 0; i < cells.length; i++) {
            int index = i;
            cells[i].addTextChangedListener(afterChange(() -> {
                if (cells[index].length() == 1 && index + 1 < cells.length) {
                    cells[index + 1].requestFocus();
                }
            }));
            cells[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN
                        && cells[index].length() == 0 && index > 0) {
                    cells[index - 1].requestFocus();
                    cells[index - 1].setText(null);
                    return true;
                }
                return false;
            });
        }
        cells[0].requestFocus();
    }

    // ---- Block 3 -----------------------------------------------------------------

    /** Item 23: the eye hides the balance behind dots and back. */
    static void walletBalance(ScreenActivity screen) {
        TextView balance = screen.findViewById(R.id.wallet_balance);
        ImageButton eye = screen.findViewById(R.id.wallet_eye);
        eye.setOnClickListener(v -> {
            boolean hide = !Boolean.TRUE.equals(eye.getTag());
            eye.setTag(hide);
            balance.setText(hide ? R.string.m23_hidden : R.string.m23_balance);
            eye.setImageResource(hide ? R.drawable.ic_eye_off : R.drawable.ic_eye);
        });
    }

    // ---- Block 4 -----------------------------------------------------------------

    /** Item 31: a marquee only runs on a selected view; the big button toggles play/pause. */
    static void player(ScreenActivity screen) {
        screen.findViewById(R.id.player_title).setSelected(true);
        ImageButton play = screen.findViewById(R.id.player_play);
        play.setOnClickListener(v -> togglePlay(play, null));
    }

    /** Item 34: blur the caller's photo behind the call UI (Android 12+). */
    static void callBlur(ScreenActivity screen) {
        ImageView backdrop = screen.findViewById(R.id.call_backdrop);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            backdrop.setRenderEffect(RenderEffect.createBlurEffect(60f, 60f, Shader.TileMode.CLAMP));
        } else {
            backdrop.setAlpha(0.35f);
        }
    }

    /** Item 36: a tap on the video shows or hides all the controls at once. */
    static void videoControls(ScreenActivity screen) {
        Group controls = screen.findViewById(R.id.video_controls);
        View.OnClickListener toggle = v -> controls.setVisibility(
                controls.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        // While the controls show, their scrim covers the video, so it takes the tap.
        screen.findViewById(R.id.video_surface).setOnClickListener(toggle);
        screen.findViewById(R.id.video_scrim).setOnClickListener(toggle);
        ImageButton pause = screen.findViewById(R.id.video_pause);
        pause.setOnClickListener(v -> togglePlay(pause, null));
    }

    /** How far left (in mic widths) the microphone must be dragged to cancel. */
    static final float CANCEL_DRAG = 2.5f;

    /** Item 37: timer and waveform run while recording; dragging the mic left cancels. */
    @SuppressLint("ClickableViewAccessibility")
    static void voiceRecord(ScreenActivity screen) {
        WaveformView wave = screen.findViewById(R.id.voice_wave);
        TextView timer = screen.findViewById(R.id.voice_timer);
        TextView status = screen.findViewById(R.id.voice_status);
        View mic = screen.findViewById(R.id.voice_mic);
        Handler handler = new Handler(Looper.getMainLooper());
        long[] startedAt = {System.currentTimeMillis()};
        Runnable tick = new Runnable() {
            @Override
            public void run() {
                long seconds = (System.currentTimeMillis() - startedAt[0]) / 1000;
                timer.setText(Countdown.format(seconds));
                handler.postDelayed(this, 250);
            }
        };
        Runnable start = () -> {
            startedAt[0] = System.currentTimeMillis();
            status.setText(R.string.m37_recording);
            wave.start();
            handler.removeCallbacks(tick);
            handler.post(tick);
        };
        Runnable cancel = () -> {
            handler.removeCallbacks(tick);
            wave.stop();
            timer.setText(Countdown.format(0));
            status.setText(R.string.m37_cancelled);
        };
        float[] downX = new float[1];
        mic.setOnTouchListener((v, e) -> {
            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    downX[0] = e.getRawX();
                    if (!wave.isRunning()) {
                        start.run();
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                    v.setTranslationX(Math.min(0, e.getRawX() - downX[0]));
                    return true;
                case MotionEvent.ACTION_UP:
                    // Judge by where the finger was lifted, not by the last move event.
                    if (downX[0] - e.getRawX() > v.getWidth() * CANCEL_DRAG) {
                        cancel.run();
                    }
                    v.animate().translationX(0).setDuration(150).start();
                    return true;
                case MotionEvent.ACTION_CANCEL:
                    v.animate().translationX(0).setDuration(150).start();
                    return true;
                default:
                    return false;
            }
        });
        // Recording starts with the screen, like holding the mic button in a messenger.
        start.run();
        screen.getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                handler.removeCallbacks(tick);
            }
        });
    }

    static final int[] GALLERY_TILES = {
        R.id.gallery_1, R.id.gallery_2, R.id.gallery_3, R.id.gallery_4,
        R.id.gallery_5, R.id.gallery_6, R.id.gallery_7, R.id.gallery_8,
        R.id.gallery_9, R.id.gallery_10, R.id.gallery_11, R.id.gallery_12,
    };

    static final int[] GALLERY_BADGES = {
        R.id.gallery_badge_1, R.id.gallery_badge_2, R.id.gallery_badge_3, R.id.gallery_badge_4,
        R.id.gallery_badge_5, R.id.gallery_badge_6, R.id.gallery_badge_7, R.id.gallery_badge_8,
        R.id.gallery_badge_9, R.id.gallery_badge_10, R.id.gallery_badge_11, R.id.gallery_badge_12,
    };

    /** Item 38: tapping photos numbers them in the order they were picked. */
    static void gallery(ScreenActivity screen) {
        SelectionOrder order = new SelectionOrder();
        TextView count = screen.findViewById(R.id.gallery_count);
        Button send = screen.findViewById(R.id.gallery_send);
        TextView[] badges = new TextView[GALLERY_BADGES.length];
        for (int i = 0; i < badges.length; i++) {
            badges[i] = screen.findViewById(GALLERY_BADGES[i]);
        }
        Runnable render = () -> {
            for (int i = 0; i < badges.length; i++) {
                int position = order.position(i);
                badges[i].setVisibility(position > 0 ? View.VISIBLE : View.GONE);
                badges[i].setText(position > 0 ? String.valueOf(position) : null);
            }
            count.setText(screen.getString(R.string.m38_selected, order.size()));
            send.setEnabled(order.size() > 0);
        };
        for (int i = 0; i < GALLERY_TILES.length; i++) {
            int item = i;
            View tile = screen.findViewById(GALLERY_TILES[i]);
            tile.setContentDescription(screen.getString(R.string.cd_photo, i + 1));
            tile.setOnClickListener(v -> {
                order.toggle(item);
                render.run();
            });
        }
        render.run();
    }

    /** Item 40: play right in the feed: the button turns into pause and a progress bar shows. */
    static void podcast(ScreenActivity screen) {
        int[][] banners = {
            {R.id.podcast_play_1, R.id.podcast_progress_1},
            {R.id.podcast_play_2, R.id.podcast_progress_2},
        };
        for (int[] b : banners) {
            ImageButton play = screen.findViewById(b[0]);
            LinearProgressIndicator progress = screen.findViewById(b[1]);
            play.setOnClickListener(v -> togglePlay(play, progress));
        }
    }

    /** Swaps a play button between play and pause; shows the progress bar while playing. */
    static void togglePlay(ImageButton button, LinearProgressIndicator progress) {
        boolean playing = !Boolean.TRUE.equals(button.getTag());
        // Buttons that start out showing "pause" (player, video) are already playing.
        if (button.getTag() == null && button.getContentDescription() != null
                && button.getContentDescription().toString()
                        .equals(button.getResources().getString(R.string.cd_pause))) {
            playing = false;
        }
        button.setTag(playing);
        button.setImageResource(playing ? R.drawable.ic_pause : R.drawable.ic_play);
        button.setContentDescription(button.getResources().getString(playing ? R.string.cd_pause : R.string.cd_play));
        if (progress != null) {
            progress.show();
            if (playing && progress.getProgress() == 0) {
                progress.setProgressCompat(8, true);
            }
        }
    }

    // ---- Block 5 -----------------------------------------------------------------

    static final int[] TAXI_TARIFFS = {R.id.tariff_1, R.id.tariff_2, R.id.tariff_3};

    /** Item 41: the three tariff cards behave as one single-choice group. */
    static void taxi(ScreenActivity screen) {
        MaterialCardView[] cards = new MaterialCardView[TAXI_TARIFFS.length];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = screen.findViewById(TAXI_TARIFFS[i]);
        }
        for (MaterialCardView card : cards) {
            card.setOnClickListener(v -> {
                for (MaterialCardView other : cards) {
                    other.setChecked(other == card);
                }
            });
        }
    }

    static final long OTP_LIFETIME_MS = 60_000;

    /** Item 47: the code expires after a minute; only then can a new one be requested. */
    static void confirmCountdown(ScreenActivity screen) {
        TextView timer = screen.findViewById(R.id.confirm_timer);
        Button resend = screen.findViewById(R.id.confirm_resend);
        CountDownTimer[] current = new CountDownTimer[1];
        Runnable start = new Runnable() {
            @Override
            public void run() {
                resend.setEnabled(false);
                current[0] = new CountDownTimer(OTP_LIFETIME_MS, 250) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        timer.setText(screen.getString(R.string.m47_timer,
                                Countdown.format(Countdown.secondsLeft(millisUntilFinished))));
                    }

                    @Override
                    public void onFinish() {
                        timer.setText(R.string.m47_expired);
                        resend.setEnabled(true);
                    }
                }.start();
            }
        };
        resend.setOnClickListener(v -> start.run());
        start.run();
        screen.getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                current[0].cancel();
            }
        });
    }

    static final int[] INSURANCE_STEPS = {R.string.m49_step_1, R.string.m49_step_2, R.string.m49_step_3, R.string.m49_step_4};

    /** Item 49: Back and Next walk through the four steps; the indicator follows. */
    static void insuranceSteps(ScreenActivity screen) {
        TextView step = screen.findViewById(R.id.ins_step);
        TextView title = screen.findViewById(R.id.ins_step_title);
        LinearProgressIndicator progress = screen.findViewById(R.id.ins_progress);
        Button back = screen.findViewById(R.id.ins_back);
        Button next = screen.findViewById(R.id.ins_next);
        int total = INSURANCE_STEPS.length;
        int[] current = {1};
        Runnable render = () -> {
            int n = current[0];
            step.setText(screen.getString(R.string.m49_step, n, total));
            title.setText(INSURANCE_STEPS[n - 1]);
            progress.setProgressCompat(n, true);
            back.setEnabled(n > 1);
            next.setText(n == total ? R.string.m49_finish : R.string.next);
        };
        back.setOnClickListener(v -> {
            current[0] = Math.max(1, current[0] - 1);
            render.run();
        });
        next.setOnClickListener(v -> {
            current[0] = Math.min(total, current[0] + 1);
            render.run();
        });
        render.run();
    }

    static final int[] PRODUCT_PHOTOS = {R.color.photo_5, R.color.photo_3, R.color.photo_2};

    /**
     * Item 50: slider with a page counter, toolbar back, the accordions. (Its window
     * background is dropped by ScreenActivity, as the root paints the whole screen.)
     */
    static void productPage(ScreenActivity screen) {
        ViewPager2 pager = screen.findViewById(R.id.product_pager);
        TextView counter = screen.findViewById(R.id.product_counter);
        pager.setAdapter(new PhotoAdapter(PRODUCT_PHOTOS));
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                counter.setText(screen.getString(R.string.m50_counter, position + 1, PRODUCT_PHOTOS.length));
            }
        });
        counter.setText(screen.getString(R.string.m50_counter, 1, PRODUCT_PHOTOS.length));

        screen.findViewById(R.id.product_back).setOnClickListener(v -> screen.finish());
        accordion(screen.findViewById(R.id.product_description_header), screen.findViewById(R.id.product_description));
        accordion(screen.findViewById(R.id.product_specs_header), screen.findViewById(R.id.product_specs));
    }

    /** Opens and closes an accordion section; the chevron points up while it is open. */
    static void accordion(TextView header, View body) {
        header.setOnClickListener(v -> {
            boolean open = body.getVisibility() != View.VISIBLE;
            body.setVisibility(open ? View.VISIBLE : View.GONE);
            header.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0, 0, open ? R.drawable.ic_collapse : R.drawable.ic_expand, 0);
        });
    }

    /** Item 50: the slider's pages are bare ImageViews, so the list has no nesting. */
    static final class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.Holder> {

        private final int[] colors;

        PhotoAdapter(int[] colors) {
            this.colors = Arrays.copyOf(colors, colors.length);
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImageView view = (ImageView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product_photo, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.image.setBackgroundColor(ContextCompat.getColor(holder.image.getContext(), colors[position]));
        }

        @Override
        public int getItemCount() {
            return colors.length;
        }

        static final class Holder extends RecyclerView.ViewHolder {
            final ImageView image;

            Holder(ImageView image) {
                super(image);
                this.image = image;
            }
        }
    }

    // ---- Helpers -------------------------------------------------------------------

    static TextWatcher afterChange(Runnable action) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                action.run();
            }
        };
    }
}

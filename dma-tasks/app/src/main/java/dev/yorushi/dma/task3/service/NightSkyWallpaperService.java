package dev.yorushi.dma.task3.service;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Looper;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import java.util.Random;

/** P40: live wallpaper with slowly drifting stars. */
public final class NightSkyWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new SkyEngine();
    }

    private final class SkyEngine extends Engine {
        private static final int STARS = 60;
        private static final long FRAME_MS = 50;

        private final Handler handler = new Handler(Looper.getMainLooper());
        private final Paint starPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint skyPaint = new Paint();
        private final float[] x = new float[STARS];
        private final float[] y = new float[STARS];
        private final Runnable frame = this::draw;
        private boolean visible;

        SkyEngine() {
            starPaint.setColor(Color.WHITE);
            Random random = new Random(7);
            for (int i = 0; i < STARS; i++) {
                x[i] = random.nextFloat();
                y[i] = random.nextFloat();
            }
        }

        @Override
        public void onVisibilityChanged(boolean isVisible) {
            visible = isVisible;
            handler.removeCallbacks(frame);
            if (visible) {
                draw();
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            visible = false;
            handler.removeCallbacks(frame);
        }

        private void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                try {
                    int w = canvas.getWidth();
                    int h = canvas.getHeight();
                    skyPaint.setShader(new LinearGradient(0, 0, 0, h, 0xFF0B1026, 0xFF1A2A5A, Shader.TileMode.CLAMP));
                    canvas.drawRect(0, 0, w, h, skyPaint);
                    for (int i = 0; i < STARS; i++) {
                        x[i] = (x[i] + 0.0005f) % 1f;
                        canvas.drawCircle(x[i] * w, y[i] * h, 2f + (i % 3), starPaint);
                    }
                } finally {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
            if (visible) {
                handler.postDelayed(frame, FRAME_MS);
            }
        }
    }
}

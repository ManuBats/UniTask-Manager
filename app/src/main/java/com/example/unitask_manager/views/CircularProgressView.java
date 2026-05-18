package com.example.unitask_manager.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.unitask_manager.R;

public class CircularProgressView extends View {

    private static final int DEFAULT_SIZE = 140;
    private static final float STROKE_WIDTH = 14f;
    private static final float PROGRESS = 0.68f;

    private final Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF arcRect = new RectF();
    private Bitmap graduationBitmap;
    private float cx, cy, radius;

    public CircularProgressView(Context context) {
        super(context);
        init();
    }

    public CircularProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        bgPaint.setColor(ContextCompat.getColor(getContext(), R.color.primary_light));
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(STROKE_WIDTH);
        bgPaint.setStrokeCap(Paint.Cap.ROUND);

        progressPaint.setColor(ContextCompat.getColor(getContext(), R.color.primary_purple));
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(STROKE_WIDTH);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_primary));
        textPaint.setTextSize(28f);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        loadGraduationIcon();
    }

    private void loadGraduationIcon() {
        Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.ic_graduation);
        if (d != null) {
            int size = 36;
            d.setBounds(0, 0, size, size);
            graduationBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(graduationBitmap);
            d.draw(c);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = resolveSize(dpToPx(DEFAULT_SIZE), widthMeasureSpec);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        cx = w / 2f;
        cy = h / 2f;
        radius = Math.min(cx, cy) - STROKE_WIDTH / 2f - dpToPx(4);
        float halfStroke = STROKE_WIDTH / 2f;
        arcRect.set(cx - radius, cy - radius, cx + radius, cy + radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(cx, cy, radius, bgPaint);

        float sweep = 360f * PROGRESS;
        canvas.drawArc(arcRect, -90, sweep, false, progressPaint);

        String pct = Math.round(PROGRESS * 100) + "%";
        float textY = cy - dpToPx(6);
        canvas.drawText(pct, cx, textY, textPaint);

        if (graduationBitmap != null) {
            float iconLeft = cx - graduationBitmap.getWidth() / 2f;
            float iconTop = textY + dpToPx(8);
            canvas.drawBitmap(graduationBitmap, iconLeft, iconTop, null);
        }
    }

    private int dpToPx(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}

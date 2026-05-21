package com.example.unitask_manager.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class PieChartView extends View {

    private String[] courseNames = {};
    private float[] percentages = {};
    private int[] colors = {};

    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF arcRect = new RectF();
    private float cx, cy, radius;

    public PieChartView(Context context) {
        super(context);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        init();
    }

    public void setData(String[] courseNames, float[] percentages, int[] courseColors) {
        this.courseNames = courseNames != null ? courseNames : new String[]{};
        this.percentages = percentages != null ? percentages : new float[]{};
        this.colors = courseColors != null ? courseColors : new int[]{};
        invalidate();
    }

    private void init() {
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(0xFFE5E7EB);

        fillPaint.setStyle(Paint.Style.FILL);

        borderPaint.setColor(0xFFFFFFFF);
        borderPaint.setStrokeWidth(dpToPx(3));
        borderPaint.setStyle(Paint.Style.STROKE);

        labelPaint.setColor(0xFFFFFFFF);
        labelPaint.setTextSize(dpToPx(14));
        labelPaint.setTypeface(Typeface.DEFAULT_BOLD);
        labelPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = resolveSize(dpToPx(160), widthMeasureSpec);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        cx = w / 2f;
        cy = h / 2f;
        radius = Math.min(cx, cy) - dpToPx(8);
        float inset = dpToPx(2);
        arcRect.set(cx - radius + inset, cy - radius + inset, cx + radius - inset, cy + radius - inset);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (courseNames.length == 0) {
            canvas.drawCircle(cx, cy, radius, bgPaint);
            canvas.drawCircle(cx, cy, radius, borderPaint);
            return;
        }

        float sweepPerCourse = 360f / courseNames.length;

        canvas.drawCircle(cx, cy, radius, bgPaint);

        for (int i = 0; i < courseNames.length; i++) {
            if (percentages[i] <= 0f) continue;
            float startAngle = -90 + i * sweepPerCourse;
            float fillAngle = sweepPerCourse * Math.min(percentages[i], 1f);
            fillPaint.setColor(colors[i]);
            canvas.drawArc(arcRect, startAngle, fillAngle, true, fillPaint);
        }

        for (int i = 0; i < courseNames.length; i++) {
            float midAngle = -90 + i * sweepPerCourse + sweepPerCourse / 2;
            float rad = (float) Math.toRadians(midAngle);
            float labelDist = radius * 0.55f;
            float lx = cx + labelDist * (float) Math.cos(rad);
            float ly = cy + labelDist * (float) Math.sin(rad);

            String label = courseNames[i];
            if (label.length() > 10) {
                label = label.substring(0, 10) + "…";
            }
            canvas.drawText(label, lx, ly + dpToPx(4), labelPaint);
        }

        for (int i = 0; i < courseNames.length; i++) {
            float angle = -90 + i * sweepPerCourse;
            float rad = (float) Math.toRadians(angle);
            float dx = cx + radius * (float) Math.cos(rad);
            float dy = cy + radius * (float) Math.sin(rad);
            canvas.drawLine(cx, cy, dx, dy, borderPaint);
        }

        canvas.drawCircle(cx, cy, radius, borderPaint);
    }

    private int dpToPx(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}

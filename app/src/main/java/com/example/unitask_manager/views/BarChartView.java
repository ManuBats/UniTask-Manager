package com.example.unitask_manager.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.unitask_manager.R;

public class BarChartView extends View {

    private int[] values = {};
    private String[] dayLabels = {};
    private int maxValue = 1;

    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int barColor, gridColor, labelColor, valueColor;
    private float barWidth, barSpacing, chartBottom, chartTop, chartLeft, chartRight;

    public BarChartView(Context context) {
        super(context);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setData(int[] values, String[] dayLabels) {
        this.values = values != null ? values : new int[]{};
        this.dayLabels = dayLabels != null ? dayLabels : new String[]{};
        this.maxValue = 1;
        for (int v : this.values) {
            if (v > maxValue) maxValue = v;
        }
        calcularEspaciado();
        invalidate();
    }

    private void init() {
        barColor = ContextCompat.getColor(getContext(), R.color.primary_purple);
        gridColor = ContextCompat.getColor(getContext(), R.color.divider);
        labelColor = ContextCompat.getColor(getContext(), R.color.text_tertiary);
        valueColor = ContextCompat.getColor(getContext(), R.color.text_secondary);

        barPaint.setColor(barColor);
        barPaint.setStyle(Paint.Style.FILL);

        gridPaint.setColor(gridColor);
        gridPaint.setStrokeWidth(1f);

        labelPaint.setColor(labelColor);
        labelPaint.setTextSize(dpToPx(11));
        labelPaint.setTextAlign(Paint.Align.CENTER);

        valuePaint.setColor(valueColor);
        valuePaint.setTextSize(dpToPx(10));
        valuePaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minHeight = dpToPx(160);
        int height = resolveSize(minHeight, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        float padLeft = dpToPx(8);
        float padRight = dpToPx(8);
        float padTop = dpToPx(24);
        float padBottom = dpToPx(32);

        chartLeft = padLeft;
        chartRight = w - padRight;
        chartTop = padTop;
        chartBottom = h - padBottom;

        calcularEspaciado();
    }

    private void calcularEspaciado() {
        float totalWidth = chartRight - chartLeft;
        int count = Math.max(dayLabels.length, 1);
        barSpacing = totalWidth / count;
        barWidth = barSpacing * 0.45f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawGridLines(canvas);
        drawBars(canvas);
        drawLabels(canvas);
    }

    private void drawGridLines(Canvas canvas) {
        int gridLines = 3;
        for (int i = 0; i <= gridLines; i++) {
            float y = chartTop + (chartBottom - chartTop) * i / gridLines;
            canvas.drawLine(chartLeft, y, chartRight, y, gridPaint);
        }
    }

    private void drawBars(Canvas canvas) {
        for (int i = 0; i < values.length; i++) {
            float barHeight = (chartBottom - chartTop) * values[i] / maxValue;
            float left = chartLeft + i * barSpacing + (barSpacing - barWidth) / 2f;
            float top = chartBottom - barHeight;
            float right = left + barWidth;
            float bottom = chartBottom;

            RectF rect = new RectF(left, top, right, bottom);
            float radius = dpToPx(4);
            canvas.drawRoundRect(rect, radius, radius, barPaint);

            canvas.drawText(String.valueOf(values[i]), left + barWidth / 2f, top - dpToPx(4), valuePaint);
        }
    }

    private void drawLabels(Canvas canvas) {
        for (int i = 0; i < dayLabels.length; i++) {
            float x = chartLeft + i * barSpacing + barSpacing / 2f;
            canvas.drawText(dayLabels[i], x, chartBottom + dpToPx(18), labelPaint);
        }
    }

    private int dpToPx(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}

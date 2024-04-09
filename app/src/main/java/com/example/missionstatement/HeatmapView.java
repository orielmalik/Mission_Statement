package com.example.missionstatement;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class HeatmapView extends View {

    private List<Path> paths;
    private Paint paint;
    private Path currentPath;
    private Handler handler;
    private boolean isDrawing = false;

    public HeatmapView(Context context) {
        super(context);
        init();
    }

    public HeatmapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeatmapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paths = new ArrayList<>();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED); // Default color
        paint.setStrokeWidth(10); // Default stroke width

        handler = new Handler();
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    public void setStrokeWidth(float width) {
        paint.setStrokeWidth(width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Path path : paths) {
            canvas.drawPath(path, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startDrawing();
                currentPath = new Path();
                currentPath.moveTo(x, y);
                paths.add(currentPath);
                break;
            case MotionEvent.ACTION_MOVE:
                currentPath.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                stopDrawing();
                break;
        }

        invalidate(); // Request redraw
        return true;
    }

    private void startDrawing() {
        isDrawing = true;
        handler.postDelayed(() -> stopDrawing(), 2000); // Stop after 2 seconds
    }

    private void stopDrawing() {
        isDrawing = false;
        handler.removeCallbacksAndMessages(null); // Remove all callbacks
    }
}

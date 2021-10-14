package com.example.marcelovaranda.newview;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcelovaranda on 2018-04-02.
 */

public class NewView extends View implements View.OnTouchListener{

    class Tracer {
        Path    path;
        Paint   paint;

        public Tracer(Path path, Paint paint) {
            this.path = path;
            this.paint = paint;
        }
        public Path getPath() {return path;}
        public Paint getPaint() {return paint;}
    }
    static final String TAG = "DEMO";
    Paint current_paint;
    int current_color;
    int current_stroke_width;
    Tracer active_tracer = null;
    List<Tracer> tracer_list;
    Paint   ref_rect;
    float   ref_rect_ori_x, ref_rect_ori_y;
    Bitmap bitmap = null;

    public NewView(Context context) {
        super(context);
        init(context);
    }

    public NewView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NewView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public NewView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        tracer_list = new ArrayList<Tracer>();
        current_color =  Color.RED;
        current_stroke_width = 10;

        current_paint = new Paint();
        current_paint.setAntiAlias(true);
        current_paint.setColor(current_color);
        current_paint.setStrokeWidth(current_stroke_width);
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ref_rect_ori_x = x;
                ref_rect_ori_y = y;
                Path path = new Path();
                Paint paint = new Paint();
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(current_stroke_width);
                paint.setColor(current_color);
                path.moveTo(x,y);
                active_tracer = new Tracer(path, paint);
                tracer_list.add(active_tracer);

                Log.d(TAG, "Down " + x + ", " + y);
                break;
            case MotionEvent.ACTION_MOVE:
                active_tracer.getPath().lineTo(x,y);
                Log.d(TAG, "MOVE " + x + ", " + y);
                break;

            case MotionEvent.ACTION_UP:
                Log.d(TAG, "UP " + x + ", " + y);
                break;
            default:
                break;
        }
        invalidate();

        return true;
    }

    public void setLineColor(int c) {
        current_color = c;
        current_paint.setColor(c);
        //invalidate();
    }

    public void setStrokeWidth(int c) {
        current_stroke_width = c;
        current_paint.setTextSize(c);
        invalidate();
    }

    public void undo( ) {
        if (tracer_list == null) return;
        int i = tracer_list.size();
        if (tracer_list.size() > 0) {
            tracer_list.remove(i-1);
            invalidate();
        }
    }

    public void setBitmap(Bitmap b) {
        bitmap = b;
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);


        float w = getWidth();
        float h = getHeight();
        c.drawColor(Color.BLACK);
        //c.drawRect( new RectF(0, 0, 200, 400), current_paint);
        //c.drawLine(0,0, w, h, current_paint);
//        if (bitmap != null) {
//            c.drawBitmap(bitmap, 0,0,null);
//        }
//        c.drawText("MY TEXT", w/2, h/2, current_paint);

        // ------ draw paths ------
        int i;
        if (tracer_list == null) return;
        for (i=0; i<tracer_list.size(); i++ ) {
            c.drawPath(tracer_list.get(i).getPath(), tracer_list.get(i).getPaint());
        }
    }
}

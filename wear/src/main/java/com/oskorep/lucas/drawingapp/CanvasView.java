package com.oskorep.lucas.drawingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import static android.graphics.Bitmap.createBitmap;

/**
 * This class serves as the custom view class facilitating as the "drawing canvas" for this app
 */
class CanvasView extends View {

    private Context mContext;
    private final String TAG = "Wearable: ";


    private int paintColor = Color.BLACK;
    private int stroke_width = 10;

    private Paint mPaint, mCanvasPaint;
    private Path mPath;

    private ArrayList<Path> paths = new ArrayList<Path>();
    private ArrayList<Paint> paints = new ArrayList<Paint>();

    private ArrayList<Path> redoPaths = new ArrayList<Path>();
    private ArrayList<Paint> redoPaints = new ArrayList<Paint>();

    private Bitmap mBitmap;

    private Canvas mCanvas;


    /**
     * Creates a CanvasView as a default constructor as none is provided from the View class.
     * Initializes the paintbrush for the app.
     *
     * @param context      - The app context.
     * @param attributeSet - A set of attributes for the superclass.
     */
    public CanvasView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        setupPaint();
        mCanvasPaint = new Paint(Paint.DITHER_FLAG);
        mPath = new Path();
    }

    /**
     * Draws the user-made paths from this app to the screen whent eh View.onDraw call is made
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, mCanvasPaint);
        for (int x = 0; x < paths.size(); x++) {
            canvas.drawPath(paths.get(x), paints.get(x));
        }
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * Registeres touch events on teh given view and draws a line corresponding to the path traces
     * by the user's input.
     *
     * @param event - The MotionEvent being triggered
     * @return - true if path changed, false if not.
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float touchX = event.getX();
        float touchY = event.getY();
        // Checks for the event that occurs
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                redoPaths.clear();
                redoPaints.clear();
                mPath.reset();
                mPath.moveTo(touchX, touchY);
                return true;

            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "Moving");
                mPath.lineTo(touchX, touchY);
                break;

            case MotionEvent.ACTION_UP:

                mPath.lineTo(touchX, touchY);
                mCanvas.drawPath(mPath, mPaint);
                paths.add(mPath);
                paints.add(mPaint);
                setupPaint();
                mPath = new Path();
                mPath.reset();
                break;
            default:

                return false;
        }
        // Force a view to draw again
        invalidate();
        return false;
    }



    /**
     * Creates a paintbrush for drawing paths with default values.
     */
    private void setupPaint() {
        // Setup paint with color and stroke styles
        mPaint = new Paint();
        mPaint.setColor(paintColor);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(stroke_width);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {

        super.onSizeChanged(width, height, oldWidth, oldHeight);


        mBitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas();

    }



}

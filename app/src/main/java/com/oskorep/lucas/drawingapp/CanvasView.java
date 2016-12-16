package com.oskorep.lucas.drawingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.mukesh.image_processing.ImageProcessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import cn.Ragnarok.BitmapFilter;

/**
 * This class serves as the custom view class facilitating as the "drawing canvas" for this app
 */
class CanvasView extends View {

    private Context mContext;

    private int mWidth, mHeight;

    private int paintColor = Color.BLACK;
    private int stroke_width = 10;
    private int filterColor = Color.WHITE;

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

//    final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
//        public void onLongPress(MotionEvent e) {
//            Log.e("", "Longpress detected");
//        }
//    });
////    @Override
////    public boolean onLongClick(View v) {
////        //do things
////        Toast.makeText(mContext, "TEST", Toast.LENGTH_SHORT).show();
////        return true;
////
////    }

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

    public void undo() {
        if (paths.size() > 0) {
            redoPaths.add(paths.remove(paths.size() - 1));
            redoPaints.add(paints.remove(paints.size() - 1));
            invalidate();
        }
    }

    public void redo() {
        if (redoPaths.size() > 0) {
            paths.add(redoPaths.remove(redoPaths.size() - 1));
            paints.add(redoPaints.remove(redoPaints.size() - 1));
            invalidate();
        }
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

        mWidth = width;
        mHeight = height;

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas();
//        mCanvas = new Canvas(mBitmap);

    }


    /**
     * Erases the current canvas and sets the new background to be the incoming BITMAP
     *
     * @param bitmap - The new image to serve as a background.
     */
    public void loadImage(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, mWidth, mHeight, true);
        paths.clear();
        paints.clear();
        mBitmap = newBitmap;
        invalidate();
    }

    /**
     * Saves teh current canvas to the Android.GALLERY on Android.SDCARD
     */
    public void save() {

        this.setDrawingCacheEnabled(true);
        MediaStore.Images.Media.insertImage(mContext.getContentResolver(), this.getDrawingCache(), "NEW TITLE", "description HERE");
        this.setDrawingCacheEnabled(false);
        Toast.makeText(mContext, "Picture was saved to: /sdcard/Pictures", Toast.LENGTH_SHORT).show();

    }

    /**
     * Applies a specified filter to the current image.  Also the current image should be flattened
     * to preserve any current edits.
     *
     * @param filter - the id of the filter to be applied.
     */
    public void applyFilter(int filter) {
        this.setDrawingCacheEnabled(true);
        Bitmap prefilteredBitmap = Bitmap.createBitmap(this.getDrawingCache());
        Bitmap filteredBitmap;

        //Add in the imagefilter here

        ImageProcessor imageProcessor = new ImageProcessor();

        switch (filter) {

            case MainActivity.FILTER_GREYSCALE:

                filteredBitmap = BitmapFilter.changeStyle(prefilteredBitmap, BitmapFilter.GRAY_STYLE);
                break;

            case MainActivity.FILTER_INVERT:

                filteredBitmap = BitmapFilter.changeStyle(prefilteredBitmap, BitmapFilter.INVERT_STYLE);
                break;

            case MainActivity.FILTER_TINT:

                filteredBitmap = imageProcessor.applyShadingFilter(prefilteredBitmap, filterColor);
                break;

            case MainActivity.FILTER_SKETCH:

                filteredBitmap = BitmapFilter.changeStyle(prefilteredBitmap, BitmapFilter.SKETCH_STYLE);
                break;

            case MainActivity.FILTER_OIL:

                filteredBitmap = BitmapFilter.changeStyle(prefilteredBitmap, BitmapFilter.OIL_STYLE);
                break;

            default:
                filteredBitmap = prefilteredBitmap;
                ;
        }
        loadImage(filteredBitmap);
        this.setDrawingCacheEnabled(false);
    }


    /**
     * Sets the brush size to the input size
     *
     * @param size - Size in DP to set the brush to.
     */
    public void setBrushSize(int size) {
        mPaint.setStrokeWidth(size);
        stroke_width = size;
    }

    /**
     * Sets the color of the brush to the input color
     *
     * @param color - Hex valued integer containing the color of the new brush.
     */
    public void setBrushColor(int color) {
        mPaint.setColor(color);
        paintColor = color;
    }

    public void setFilterColor(int color) {
        filterColor = color;
    }

    /**
     * Grabs the current cached view o the canvas, saves it to a temporary file and then sends it
     * to the image-intent filter for sharing the current image.
     */
    public void shareImage() {

        this.setDrawingCacheEnabled(true);
        Bitmap sharable = this.getDrawingCache();
        File root = Environment.getExternalStorageDirectory();
        File tmpFile = new File(root, "image0.png");

        try {
            //Writes the bitmap to a temporary file created above.
            OutputStream os = new FileOutputStream(tmpFile);
            sharable.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.close();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tmpFile));


            //Makes hte mediascanner recognize the new content in the tmp file.
            MediaScannerConnection.scanFile(mContext,
                    new String[]{tmpFile.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });

            mContext.startActivity(Intent.createChooser(shareIntent, "Share Your Drawing"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        this.setDrawingCacheEnabled(false);

    }
}

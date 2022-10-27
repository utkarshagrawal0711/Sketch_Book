//package com.example.sketchbook.Model;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.graphics.PorterDuff;
//import android.graphics.PorterDuffXfermode;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.view.View;
//
//import androidx.annotation.Nullable;
//
//import com.example.sketchbook.PaintActivity;
//
//import java.util.ArrayList;
//
///*
//Every view has a canvas onDraw(Canvas canvas) method
//To redraw -> invalidate()
//*/
//
//public class PaintView extends View {
//    private Bitmap bitmapBackground,bitmapView;
//    private Paint paint = new Paint();
//    private Path path = new Path();
//    private int backgroundColor;
//    private int brushSize;
//    private int eraserSize;
//    private float mX,mY;
//    private Canvas canvas=null;
//    private final int TOUCH_TOLERANCE=4;
//
//    private ImageObject image;
//
//    private ArrayList<Bitmap> undoList = new ArrayList<>();
//    private ArrayList<Bitmap> redoList = new ArrayList<>();
//
//    public PaintView(Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//        initialise();
//    }
//
//    private void initialise() {
//        eraserSize=12;
//        brushSize=12;
//        backgroundColor= Color.WHITE;
//
//        paint.setColor(Color.BLACK);
//
//        /*Helper for setFlags(), setting or clearing the ANTI_ALIAS_FLAG bit AntiAliasing smooths
//        out the edges of what is being drawn, but is has no impact on the interior of the shape.*/
//        paint.setAntiAlias(true);
//
//        /*Helper for setFlags(), setting or clearing the DITHER_FLAG bit Dithering affects how
//        colors that are higher precision than the device are down-sampled.*/
//        paint.setDither(true);
//
//        paint.setStyle(Paint.Style.STROKE);
//
//        paint.setStrokeCap(Paint.Cap.ROUND);
//
//        paint.setStrokeJoin(Paint.Join.ROUND);
//
//        paint.setStrokeWidth(toPx(brushSize));
//    }
//
//    private float toPx(int brushSize) {
//        return brushSize*(getResources().getDisplayMetrics().density);
//    }
//
//    public void init(int width,int height) {
//        bitmapBackground=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
//        bitmapView=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
//        canvas=new Canvas(bitmapView);
//    }
//
//    /*
//    The Canvas class holds the "draw" calls. To draw something, you need 4 basic components:
//    A Bitmap to hold the pixels,
//    a Canvas to host the draw calls (writing into the bitmap),
//    a drawing primitive (e.g. Rect, Path, text, Bitmap),
//    and a paint (to describe the colors and styles for the drawing).
//    */
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        canvas.drawColor(backgroundColor);
//        canvas.drawBitmap(bitmapBackground,0,0,null);
//
//
//        if (image!=null) {
//            canvas.drawBitmap(image.bitmap,image.imageLeft,image.imageTop,null);
//        }
//
//        canvas.drawBitmap(bitmapView,0,0,null);
//    }
//
//    public void setBitmapView(Bitmap bitmap) {
//        this.bitmapView = bitmap;
//        canvas.drawBitmap(bitmapView,0,0,null);
//    }
//
//    public void setBackgroundColor(int backgroundColor) {
//        this.backgroundColor=backgroundColor;
//        invalidate(); //Redraw
//    }
//
//    public void setBrushSize(int brushSize) {
//        this.brushSize=brushSize;
//        paint.setStrokeWidth(toPx(brushSize));
//    }
//
//    public void setBrushColor(int color) {
//        paint.setColor(color);
//    }
//
//    public void setEraserSize(int eraserSize) {
//        this.eraserSize=eraserSize;
//        paint.setStrokeWidth(toPx(eraserSize));
//    }
//
//    public int getBrushSize() {
//        return this.brushSize;
//    }
//
//    public int getEraserSize() {
//        return this.eraserSize;
//    }
//
//    public void enableEraser() {
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//        paint.setStrokeWidth(toPx(eraserSize));
//    }
//
//    public void disableEraser() {
//        paint.setXfermode(null);
//        paint.setShader(null);
//        paint.setMaskFilter(null);
//        paint.setStrokeWidth(toPx(brushSize));
//    }
//
//    public void addUndoAction(Bitmap bitmap) {
//        undoList.add(bitmap);
//    }
//
//    private boolean undoPerform=false;
//    public void getUndoAction() {
//        if (undoList.size()>0) {
//            Bitmap redoAction = undoList.remove(undoList.size()-1);
//            redoList.add(redoAction);
//            if (undoList.size()>0) {
//                bitmapView = undoList.get(undoList.size()-1);
//            }
//            else {
//                bitmapView = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
//            }
//            canvas = new Canvas(bitmapView);
//            invalidate();
//            undoPerform=true;
//        }
//    }
//
//    public void getRedoAction() {
//        if (redoList.size()==0) {
//            return;
//        }
//        else {
//            undoPerform=false;
//            bitmapView = redoList.remove(redoList.size()-1);
//            addUndoAction(getBitmap());
//            canvas = new Canvas(bitmapView);
//            invalidate();
//        }
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        float x = event.getX();
//        float y = event.getY();
//
//        switch (event.getAction()) {
//            // Case when finger touches the screen
//            case MotionEvent.ACTION_DOWN:
//                touchStart(x,y);
//                break;
//
//            // Case when finger moves on screen
//            case MotionEvent.ACTION_MOVE:
//                PaintActivity.savedStatus=false;
//                if (undoPerform) {
//                    addUndoAction(getBitmap());
//                }
//                touchMove(x,y);
//                break;
//
//            // Case when finger is taken away from screen
//            case MotionEvent.ACTION_UP:
//                touchUp();
//                addUndoAction(getBitmap());
//                redoList.clear();
//                undoPerform=false;
//                break;
//
//            default :
//                return false;
//        }
//        return true;
//    }
//
//    private void touchStart(float x, float y) {
//        path.moveTo(x,y);
//        mX=x;
//        mY=y;
//    }
//
//    private void touchMove(float x, float y) {
//        float dx = Math.abs(x-mX);
//        float dy = Math.abs(y-mY);
//
//        if (dx>=TOUCH_TOLERANCE || dy>=TOUCH_TOLERANCE) {
//            path.quadTo(x,y,(x+mX)/2,(y+mY)/2);
//            mX=x;
//            mY=y;
//
//            canvas.drawPath(path,paint);
//            invalidate();
//        }
//    }
//
//    private void touchUp() {
//        path.reset();
//    }
//
//    public Bitmap getBitmap() {
//        this.setDrawingCacheEnabled(true);
//        this.buildDrawingCache();
//        Bitmap bitmap = Bitmap.createBitmap(this.getDrawingCache());
//        this.setDrawingCacheEnabled(false);
//        return bitmap;
//    }
//
//    public void setImage(Bitmap bitmap) {
//        image = new ImageObject();
//
//        image.bitmap = Bitmap.createScaledBitmap(bitmap,getWidth(),getHeight(),true);
//        image.imageLeft=0;
//        image.imageTop=0;
//        invalidate();
//    }
//}


package com.example.sketchbook.Model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.sketchbook.PaintActivity;

import java.util.ArrayList;

/*
Every view has a canvas onDraw(Canvas canvas) method
To redraw -> invalidate()
*/

public class PaintView extends View {
    private Bitmap bitmapBackground,bitmapView;
    private int backgroundColor;
    private int brushSize;
    private int eraserSize;
    private float mX,mY;
    private Canvas canvas=null;
    private final int TOUCH_TOLERANCE=4;
    private int paintColor;

    private int modeStatus;
    /*
    1 for brush
    2 for eraser
    */

    private ImageObject image;

    private ArrayList<Bitmap> undoList = new ArrayList<>();
    private ArrayList<Bitmap> redoList = new ArrayList<>();

    private ArrayList<Paint> paints = new ArrayList<>();
    private ArrayList<Path> paths = new ArrayList<>();
    private int historyPointer=0;

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialise();
    }

    private void initialise() {
        eraserSize=10;
        brushSize=10;
        backgroundColor= Color.WHITE;
        paintColor = Color.BLACK;
        modeStatus = 1;

        paints.add(createPaint());
        paths.add(new Path());
        historyPointer++;
    }

    private float toPx(int brushSize) {
        return brushSize*(getResources().getDisplayMetrics().density);
    }

    public void init(int width,int height) {
        bitmapBackground=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        bitmapView=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        canvas=new Canvas(bitmapView);
    }

    /*
    The Canvas class holds the "draw" calls. To draw something, you need 4 basic components:
    A Bitmap to hold the pixels,
    a Canvas to host the draw calls (writing into the bitmap),
    a drawing primitive (e.g. Rect, Path, text, Bitmap),
    and a paint (to describe the colors and styles for the drawing).
    */

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(backgroundColor);
        canvas.drawBitmap(bitmapBackground,0,0,null);

        if (image!=null) {
            canvas.drawBitmap(image.bitmap,image.imageLeft,image.imageTop,null);
        }

        @SuppressLint("CanvasSize")
        int layerId = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);

        for (int i=0;i<historyPointer;i++) {
            Path path = paths.get(i);
            Paint paint = paints.get(i);

            canvas.drawPath(path,paint);
        }

        canvas.restoreToCount(layerId);
        this.canvas=canvas;
    }

    private Paint createPaint() {
        Paint paint = new Paint();

        paint.setColor(paintColor);

        /*Helper for setFlags(), setting or clearing the ANTI_ALIAS_FLAG bit AntiAliasing smooths
        out the edges of what is being drawn, but is has no impact on the interior of the shape.*/
        paint.setAntiAlias(true);

        /*Helper for setFlags(), setting or clearing the DITHER_FLAG bit Dithering affects how
        colors that are higher precision than the device are down-sampled.*/
        paint.setDither(true);

        paint.setStyle(Paint.Style.STROKE);

        paint.setStrokeCap(Paint.Cap.ROUND);

        paint.setStrokeJoin(Paint.Join.ROUND);

        if (modeStatus==1) {
            paint.setXfermode(null);
            paint.setShader(null);
            paint.setMaskFilter(null);
            paint.setStrokeWidth(toPx(brushSize));
        }
        else {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            paint.setStrokeWidth(toPx(eraserSize));
        }

        return paint;
    }

    private Path createPath(float x,float y) {
        Path path = new Path();
        path.moveTo(x,y);
        return path;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor=backgroundColor;
        invalidate(); //Redraw
    }

    public void setBrushSize(int brushSize) {
        this.brushSize=brushSize;
        modeStatus=1;
    }

    public void setBrushColor(int color) {
        paintColor=color;
    }

    public void setEraserSize(int eraserSize) {
        this.eraserSize=eraserSize;
        modeStatus=2;
    }

    public int getBrushSize() {
        return this.brushSize;
    }

    public int getEraserSize() {
        return this.eraserSize;
    }

    public void enableEraser() {
        Paint paint = getCurrentPaint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint.setStrokeWidth(toPx(eraserSize));
    }

    public void disableEraser() {
        Paint paint = getCurrentPaint();
        paint.setXfermode(null);
        paint.setShader(null);
        paint.setMaskFilter(null);
        paint.setStrokeWidth(toPx(brushSize));
    }

    private void updateHistory(Path path) {
        if (historyPointer==paths.size()) {
            paths.add(path);
            paints.add(createPaint());
            historyPointer++;
        }
        else {
            paths.set(historyPointer,path);
            paints.set(historyPointer,createPaint());
            historyPointer++;

            for (int i=historyPointer,size=paths.size();i<size;i++) {
                paths.remove(historyPointer);
                paints.remove(historyPointer);
            }
        }
    }

    private Path getCurrentPath() {
        return paths.get(historyPointer-1);
    }

    private Paint getCurrentPaint() {
        return paints.get(historyPointer-1);
    }

    private boolean canUndo() {
        if (historyPointer>1)
            return true;
        return false;
    }

    private boolean canRedo() {
        if (historyPointer<paths.size())
            return true;
        return false;
    }

    public void undo() {
        if (canUndo()) {
            historyPointer--;
            invalidate();
        }
    }

    public void redo() {
        if (canRedo()) {
            historyPointer++;
            invalidate();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            // Case when finger touches the screen
            case MotionEvent.ACTION_DOWN:
                touchStart(x,y);
                break;

            // Case when finger moves on screen
            case MotionEvent.ACTION_MOVE:
                PaintActivity.savedStatus=false;
                touchMove(x,y);
                break;

            // Case when finger is taken away from screen
            case MotionEvent.ACTION_UP:
                touchUp();
                break;

            default :
                return false;
        }
        return true;
    }

    private void touchStart(float x, float y) {
        mX=x;
        mY=y;
        updateHistory(createPath(x,y));
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x-mX);
        float dy = Math.abs(y-mY);

        Path path = getCurrentPath();

        if (dx>=TOUCH_TOLERANCE || dy>=TOUCH_TOLERANCE) {
            path.quadTo(x,y,(x+mX)/2,(y+mY)/2);
            mX=x;
            mY=y;
        }
        invalidate();;
    }

    private void touchUp() {
    }

    public Bitmap getBitmap() {
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public void setImage(Bitmap bitmap) {
        image = new ImageObject();
        image.bitmap = Bitmap.createScaledBitmap(bitmap,getWidth(),getHeight(),true);
        image.imageLeft=0;
        image.imageTop=0;
        invalidate();
    }
}



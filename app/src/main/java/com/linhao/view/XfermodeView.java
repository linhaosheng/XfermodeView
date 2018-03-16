package com.linhao.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.linhao.model.TouchPoint;
import com.linhao.util.ImageSizeUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class XfermodeView extends View {
    public static final String TEMP_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() +
            "/reeman/ringTranslate/picture/picture.jpg";

    private Bitmap mBgBitmap;
    Bitmap mFgBitmap;
    private Paint mPaint;
    private Canvas mCanvas;
    private Path mPath;
    int FINGER_WIDTH = 40;
    int BLACK_HALF_COLOR = 0x89000000;
    private Activity activity;
    private int screenWidth;
    private int screenHeigh;

    public XfermodeView(Context context) {
        this(context, null);
    }

    public void setActivity(Activity activity1) {
        this.activity = activity1;
        //计算屏幕的尺寸
        caculateScreenSize();
    }

    private void caculateScreenSize() {
        //计算屏幕的尺寸
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeigh = dm.heightPixels;
    }

    public XfermodeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XfermodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBgBitmap, 0, 0, null);
        canvas.drawBitmap(mFgBitmap, 0, 0, null);
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAlpha(0);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(100);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPath = new Path();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                listener.onEvent(MotionEvent.ACTION_DOWN);
                listPoint.clear();
                listPoint.add(new TouchPoint(event.getX(), event.getY()));
                clearCanvas();
                mPath.reset();
                invalidate();
                mPath.moveTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                listener.onEvent(MotionEvent.ACTION_MOVE);
                listPoint.add(new TouchPoint(event.getX(), event.getY()));
                mPath.lineTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                listener.onEvent(MotionEvent.ACTION_UP);
                listPoint.add(new TouchPoint(event.getX(), event.getY()));
                changePoints();
                break;
        }
        mCanvas.drawPath(mPath, mPaint);
        invalidate();
        return true;
    }


    public void clearCanvas() {
        if (mPaint == null || mCanvas == null) {
            return;
        }
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCanvas.drawPaint(mPaint);
        mCanvas = new Canvas(mFgBitmap);
        mCanvas.drawColor(BLACK_HALF_COLOR);
        invalidate();
    }

    List<TouchPoint> listPoint = new ArrayList<>();
    List<Float> pointX = new ArrayList<>();
    List<Float> pointY = new ArrayList<>();

    /***
     * 计算手指滑动的区域
     */
    private void changePoints() {
        pointX.clear();
        pointY.clear();
        for (int i = 0; i < listPoint.size(); i++) {
            pointX.add(listPoint.get(i).getPointX());
            pointY.add(listPoint.get(i).getPointY());
        }
        /***
         * 这里因为用户会反复的涂抹，所以吧所有的移动点添加到集合中，然后对所有的点取值
         * 取出手指的最大范围和最小范围，
         */
        float minX = Collections.min(pointX);
        float minY = Collections.min(pointY);
        float maxX = Collections.max(pointX);
        float maxY = Collections.max(pointY);
        /***
         * 这里加上笔触的宽度
         */
        minX = minX - FINGER_WIDTH;
        minY = minY - FINGER_WIDTH;
        maxX = maxX + FINGER_WIDTH;
        maxY = maxY + FINGER_WIDTH;
        if (minX < 0) {
            minX = 0;
        }
        if (minY < 0) {
            minY = 0;
        }

        if (maxX > screenWidth) {
            maxX = screenWidth;
        }
        if (maxY > screenHeigh) {
            maxY = screenHeigh;
        }
        //这是手指绘制的区域
        RectF rect = new RectF(minX, minY, maxX, maxY);
        saveBitmap(rect);
    }

    /***
     * 保存图片到本地
     * @param rect
     */
    private void saveBitmap(RectF rect) {
        try {
            Log.i("point", "====保存的尺寸==" + rect.left + "/" + rect.top + "  ///" + rect.right + "/" + rect.bottom);
            float rectWidth = Math.abs(rect.left - rect.right);
            float rectHeight = Math.abs(rect.top - rect.bottom);
            Log.i("point", "====保存的面积==" + rectWidth * rectHeight);
            if (rectWidth * rectHeight < 6500) {
                listener.touchCutError("涂抹区域太小");
                return;
            }

            Bitmap bmp = Bitmap.createBitmap((int) rect.width(), (int) rect.height(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            canvas.translate(-(rect.left), -(rect.top));
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(mBgBitmap, 0, 0, null);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
            File file = new File(TEMP_PATH);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file.getPath());
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            listener.touchBackUrl(file.getPath());
        } catch (Exception e) {
            listener.touchCutError(e.toString());
            e.printStackTrace();
        }
    }

    public void setBitmap(String path) {
        mBgBitmap = ImageSizeUtil.decodeSampleBitmap(path, screenWidth, screenWidth);
        mFgBitmap = Bitmap.createBitmap(mBgBitmap.getWidth(), mBgBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mFgBitmap);
        mCanvas.drawColor(BLACK_HALF_COLOR);
        invalidate();
    }


    TouchCutListener listener;

    public void setOnTouchCutListener(TouchCutListener listener) {
        this.listener = listener;
    }

    public interface TouchCutListener {
        void onEvent(int event);

        void touchBackUrl(String filePath);

        void touchCutError(String error);
    }


}
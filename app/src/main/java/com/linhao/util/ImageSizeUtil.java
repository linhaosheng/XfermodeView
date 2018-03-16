package com.linhao.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.File;

/**
 * Created by haoshenglin on 2018/3/16.
 */

public class ImageSizeUtil {

    /***
     * 从资源文件中读取图片资源
     * @param reqWidth
     * @return
     */
    public static Bitmap decodeSampleBitmap(String picPath, int reqWidth, int imageWidth) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;   //去加载图片
        BitmapFactory.decodeFile(picPath, options);
        options.inSampleSize = calculateImSampleSize(options, reqWidth);
        options.inJustDecodeBounds = false;
        Bitmap mapBack = BitmapFactory.decodeFile(picPath, options);
        mapBack = zoomImage(mapBack, imageWidth, imageWidth * 4 / 3);
        Log.i("main", "=====返回的Bitmap的尺寸==" + mapBack.getWidth() + "/" + mapBack.getHeight());
        return mapBack;
    }


    /***
     * 计算图片的压缩比例
     * @param options
     * @param reqWidth
     * @return
     */
    private static int calculateImSampleSize(BitmapFactory.Options options, int reqWidth) {
        int reqHeight = reqWidth * 4 / 3;
        if (reqWidth == 0) {
            return 1;
        }
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || reqWidth > reqWidth) {
            int halfWidth = width / 2;
            int halfHeight = height / 2;
            while ((halfHeight / inSampleSize) >= reqHeight || (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 3;  //压缩过的图片比例比控件大，再次缩减压缩比例
            }
        }
        return inSampleSize;
    }


    /***
     * 图片的缩放方法
     *
     *            ：源图片资源
     * @param newWidth
     *            ：缩放后宽度
     * @param newHeight
     *            ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
//        Bitmap bgimage = BitmapFactory.decodeFile(mPath);
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

}

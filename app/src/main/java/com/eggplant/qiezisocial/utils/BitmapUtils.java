package com.eggplant.qiezisocial.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BitmapUtils {





    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

    /**
     * @param view 需要截取图片的view
     * @return 截图
     */
    public static Bitmap createBitmap(View view) {
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }







    /**
     * Utility method for downsampling images.
     *
     * @param path
     *            the file path
     * @param data
     *            if file path is null, provide the image data directly
     * @param target
     *            the target dimension
     * @param isWidth
     *            use width as target, otherwise use the higher value of height
     *            or width
     * @param round
     *            corner radius
     * @return the resized image
     */
    public static Bitmap getResizedImage(String path, byte[] data, int target,
            boolean isWidth, int round) {
 
        Options options = null;
 
        if (target > 0) {
 
            Options info = new Options();
            info.inJustDecodeBounds = true;
            //�������������Կ��Լ����ڴ����
            info.inInputShareable = true;
            info.inPurgeable = true;
 
            decode(path, data, info);
 
            int dim = info.outWidth;
            if (!isWidth)
                dim = Math.max(dim, info.outHeight);
            int ssize = sampleSize(dim, target);
 
            options = new Options();
            options.inSampleSize = ssize;
 
        }
 
        Bitmap bm = null;
        try {
            bm = decode(path, data, options);
        } catch (OutOfMemoryError e) {
        	Log.e("TAG",e.toString());
            e.printStackTrace();
        }
 
        if (round > 0) {
            bm = getRoundedCornerBitmap(bm, round);
        }
 
        return bm;
 
    }
 
    private static Bitmap decode(String path, byte[] data,
            Options options) {
 
        Bitmap result = null;
 
        if (path != null) {
 
            result = decodeFile(path, options);
 
        } else if (data != null) {
 
            // AQUtility.debug("decoding byte[]");
 
            result = BitmapFactory.decodeByteArray(data, 0, data.length,
                    options);
 
        }
 
        if (result == null && options != null && !options.inJustDecodeBounds) {
//            AQUtility.debug("decode image failed", path);
        	Log.e("TAG","decode image failed: path = " + path);
        }
 
        return result;
    }
 
    private static Bitmap decodeFile(String path, Options options) {
 
        Bitmap result = null;
 
        if (options == null) {
            options = new Options();
        }
 
        options.inInputShareable = true;
        options.inPurgeable = true;
 
        FileInputStream fis = null;
 
        try {
 
            fis = new FileInputStream(path);
 
            FileDescriptor fd = fis.getFD();
 
            result = BitmapFactory.decodeFileDescriptor(fd, null, options);
        } catch (IOException e) {
            Log.e("TAG",e.toString());
        } finally {
            try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
 
        return result;
 
    }
 
    private static int sampleSize(int width, int target) {
 
        int result = 1;
 
        for (int i = 0; i < 10; i++) {
 
            if (width < target * 2) {
                break;
            }
 
            width = width / 2;
            result = result * 2;
 
        }
 
        return result;
    }
 
    /**
     * ��ȡԲ�ǵ�bitmap
     * @param bitmap
     * @param pixels
     * @return
     */
    private static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
 
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
 
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
 
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
 
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
 
        return output;
    }
 
    /**
     * auto fix the imageOrientation
     * @param bm source
     * @param iv imageView  if set invloke it's setImageBitmap() otherwise do nothing
     * @param uri image Uri if null user path
     * @param path image path if null use uri
     */
    public static Bitmap autoFixOrientation(Bitmap bm, ImageView iv, Uri uri,String path) {
        int deg = 0;
        try {
            ExifInterface exif = null;
            if (uri == null) {
                exif = new ExifInterface(path);
            }
            else if (path == null) {
                exif = new ExifInterface(uri.getPath());
            }
 
            if (exif == null) {
                Log.e("TAG","exif is null check your uri or path");
                return bm;
            }
 
            String rotate = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int rotateValue = Integer.parseInt(rotate);
            System.out.println("orientetion : " + rotateValue);
            switch (rotateValue) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                deg = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                deg = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                deg = 270;
                break;
            default:
                deg = 0;
                break;
            }
        } catch (Exception ee) {
            Log.d("catch img error", "return");
            if(iv != null)
            iv.setImageBitmap(bm);
            return bm;
        }
        Matrix m = new Matrix();
        m.preRotate(deg);
        bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
 
        //bm = Compress(bm, 75);
        if(iv != null)
            iv.setImageBitmap(bm);
        return bm;
    }

    public Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        float targetX, targetY;
        if (orientationDegree == 90) {
            targetX = bm.getHeight();
            targetY = 0;
        } else {
            targetX = bm.getHeight();
            targetY = bm.getWidth();
        }

        final float[] values = new float[9];
        m.getValues(values);

        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];

        m.postTranslate(targetX - x1, targetY - y1);

        Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bm1);
        canvas.drawBitmap(bm, m, paint);

        return bm1;
    }

    /**
     * 图片反转
     *
     * @param bmp
     * @param flag
     *            0为水平反转，1为垂直反转
     * @return
     */
    public static Bitmap reverseBitmap(Bitmap bmp, int flag) {
        float[] floats = null;
        switch (flag) {
            case 0: // 水平反转
                floats = new float[] { -1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f };
                break;
            case 1: // 垂直反转
                floats = new float[] { 1f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 1f };
                break;
        }

        if (floats != null) {
            Matrix matrix = new Matrix();
            matrix.setValues(floats);
            return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        }

        return null;
    }

    /**
     * 图片旋转
     *
     * @param bmp
     *            要旋转的图片
     * @param degree
     *            图片旋转的角度，负值为逆时针旋转，正值为顺时针旋转
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bmp, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    }

    /**
     * 图片缩放
     *
     * @param bm
     * @param scale
     *            值小于则为缩小，否则为放大
     * @return
     */
    public static Bitmap resizeBitmap(Bitmap bm, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
    }

    /**
     * 图片缩放
     *
     * @param bm
     * @param w
     *            缩小或放大成的宽
     * @param h
     *            缩小或放大成的高
     * @return
     */
    public static Bitmap resizeBitmap(Bitmap bm, int w, int h) {
        Bitmap BitmapOrg = bm;

        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();

        float scaleWidth = ((float) w) / width;
        float scaleHeight = ((float) h) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);
    }

    public static boolean saveBitmap2SDCard(Bitmap bitmap, String savePath) {
        if (bitmap == null || savePath.equals("")) {
            return false;
        }

        File f = new File(savePath);
        try {
            f.createNewFile();
        } catch (IOException e1) {
            return false;
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            return false;
        }

        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            return false;
        }
        try {
            fOut.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    public static boolean saveSmallBitmap2SDCard(Bitmap bitmap, String savePath) {

        if (bitmap == null || savePath.equals("")) {
            return false;
        }



        File f = new File(savePath);
        try {
            f.createNewFile();
        } catch (IOException e1) {
            return false;
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            return false;
        }

        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            return false;
        }
        try {
            fOut.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }



    public static boolean saveBitmap2SDCard(Bitmap bitmap, String savePath, String format) {
        if (bitmap == null || savePath.equals("")) {
            return false;
        }

        File f = new File(savePath);
        try {
            f.createNewFile();
        } catch (IOException e1) {
            return false;
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            return false;
        }
        if(format.equals("png")){
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        }else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            return false;
        }
        try {
            fOut.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    //生成圆角图片
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight()));
            final float roundPx = 10;
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

            final Rect src = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());

            canvas.drawBitmap(bitmap, src, rect, paint);
            return output;
        } catch (Exception e) {
            return bitmap;
        }
    }

    /**
     * 从assets中读取图片
     */
    public static Bitmap getImageFromAssetsFile(Resources res, String fileName)
    {
        Bitmap image = null;
        AssetManager am = res.getAssets();
        try
        {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return image;
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    public static final byte[] input2byte(InputStream inStream)
            throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        swapStream.close();
        return in2b;
    }

    public static void inputstreamtofile(InputStream ins,File file){
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap decodeUriAsBitmap(Context context, Uri uri){
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    public static Bitmap getLocalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param path
     * @return
     */
    public static Bitmap decodeImage(String path) {
        Bitmap res;
        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Options op = new Options();
            op.inSampleSize = 1;
            op.inJustDecodeBounds = false;
            //op.inMutable = true;
            res = BitmapFactory.decodeFile(path, op);
            //rotate and scale.
            Matrix matrix = new Matrix();

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                matrix.postRotate(90);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                matrix.postRotate(180);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                matrix.postRotate(270);
            }

            Bitmap temp = Bitmap.createBitmap(res, 0, 0, res.getWidth(), res.getHeight(), matrix, true);
            Log.d("com.arcsoft", "check target Image:" + temp.getWidth() + "X" + temp.getHeight());

            if (!temp.equals(res)) {
                res.recycle();
            }
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取一个 View 的缓存视图
     *
     * @param view
     * @return
     */
    public static Bitmap getCacheBitmapFromView(View view) {
        final boolean drawingCacheEnabled = true;
        view.setDrawingCacheEnabled(drawingCacheEnabled);
        view.buildDrawingCache(drawingCacheEnabled);
        final Bitmap drawingCache = view.getDrawingCache();
        Bitmap bitmap;
        if (drawingCache != null) {
            bitmap = Bitmap.createBitmap(drawingCache);
            view.setDrawingCacheEnabled(false);
        } else {
            bitmap = null;
        }
        return bitmap;
    }


}

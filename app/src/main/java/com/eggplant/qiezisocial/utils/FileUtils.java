package com.eggplant.qiezisocial.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.base.Request;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class FileUtils {


    public interface DownloadFileCallback {
        void onError(String message);

        void onSuccess(String filePath);

        void onProgress(Float progress);
    }

    /**
     * @param context          上下文
     * @param fileUrl          下载完整url
     * @param
     * @param destFileName     文件名
     * @param mFileRelativeUrl 下载相对地址
     *                         （我们从服务器端获取到的数据都是相对的地址）例如： "filepath": "/movie/20180511/1526028508.txt"
     */
    public static void downloadFile(final Context context, String fileUrl, String destFileName, String mFileRelativeUrl, final DownloadFileCallback callback) {
        downloadFile(context, fileUrl, destFileName, mFileRelativeUrl, getChatFilePath(context), callback);
    }

    public static void downloadFile(final Context context, String fileUrl, String destFileName, String mFileRelativeUrl, final String savePath, final DownloadFileCallback callback) {
        int i = mFileRelativeUrl.lastIndexOf(".");
        if (i == -1) {
            return;
        }
        final String mDestFileName = destFileName + mFileRelativeUrl.substring(i, mFileRelativeUrl.length());
        OkGo.<File>get(fileUrl).tag(context).execute(new FileCallback(savePath, mDestFileName) { //文件下载时指定下载的路径以及下载的文件的名称

            @Override
            public void onStart(Request<File, ? extends Request> request) {
                super.onStart(request);
                Log.e("", "开始下载文件" + "DDDDD");
            }


            @Override
            public void onSuccess(com.lzy.okgo.model.Response<File> response) {
                Log.e("", "下载文件成功" + "DDDDD" + response.body().length());
                callback.onSuccess(savePath + mDestFileName);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Log.e("", "下载文件完成" + "DDDDD");
            }

            @Override
            public void onError(com.lzy.okgo.model.Response<File> response) {
                super.onError(response);
                Log.e("", "下载文件出错" + "DDDDD" + response.message());
                callback.onError(response.message());
            }

            @Override
            public void downloadProgress(Progress progress) {
                super.downloadProgress(progress);
                float dLProgress = progress.fraction;
                callback.onProgress(dLProgress);
                Log.e("", "文件下载的进度" + "DDDDD" + dLProgress);
            }
        });
    }

    /**
     * 重命名文件
     *
     * @param oldPath 原来的文件地址
     * @param newPath 新的文件地址
     */
    public static void renameFile(String oldPath, String newPath) {
        File oleFile = new File(oldPath);
        File newFile = new File(newPath);
        //执行重命名
        oleFile.renameTo(newFile);
    }

    public static <E> List<E> deepCopy(List<E> src) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(src);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            @SuppressWarnings("unchecked")
            List<E> dest = (List<E>) in.readObject();
            return dest;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<E>();
        }
    }

    //下载具体操作
    private boolean download(Context context, String urlStr, String fName) {
        try {
            URL url = new URL(urlStr);
            //打开连接
            URLConnection conn = url.openConnection();
            //打开输入流
            InputStream is = conn.getInputStream();
            //获得长度
            int contentLength = conn.getContentLength();
            Log.e("", "文件长度 = " + contentLength);

            //下载后的文件名
            String fileName = getChatFilePath(context) + fName;
            File file1 = new File(fileName);

            if (file1.exists()) {
                Log.e("文件-----", "文件已经存在！");
                return fileIsExists(fileName);
            } else {
                //创建字节流
                byte[] bs = new byte[1024];
                int len;
                OutputStream os = new FileOutputStream(fileName);
                //写数据
                while ((len = is.read(bs)) != -1) {
                    os.write(bs, 0, len);
                }
                //完成后关闭流
                Log.e("文件-----", "下载成功！");
                os.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    //判断文件是否存在
    public boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }


    /**
     * 保存到SD卡的文件里
     *
     * @param content
     * @param filename
     */
    public static void saveFileToSDCard(String content, String filename) {
        File file = new File(Environment.getExternalStorageDirectory(), filename);
        try {
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(content.getBytes());
            outStream.close();
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拷贝SD卡上的单个文件
     *
     * @throws IOException
     */
    public static boolean copySDFileTo(String srcPath, String destPath) {
        File srcFile = new File(srcPath);
        File destFile = new File(destPath);
        try {
            return copyFileTo(srcFile, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 拷贝一个文件,srcFile源文件，destFile目标文件
     *
     * @throws IOException
     */

    public static boolean copyFileTo(File srcFile, File destFile) throws IOException {
        if (srcFile.isDirectory() || destFile.isDirectory())
            return false;// 判断是否是文件
        FileInputStream fis = new FileInputStream(srcFile);
        FileOutputStream fos = new FileOutputStream(destFile);

        int readLen = 0;
        byte[] buf = new byte[1024];
        while ((readLen = fis.read(buf)) != -1) {
            fos.write(buf, 0, readLen);
        }
        fos.flush();
        fos.close();
        fis.close();
        return true;
    }

    /**
     * 从assets里读取文件
     *
     * @param fileName
     * @param context
     * @return
     */
    public static String readFromAssets(String fileName, Context context) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets()
                    .open(fileName, Context.MODE_PRIVATE);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
        return returnString.toString();
    }

    /**
     * 读取数据（filename是读取文件的名称，reads是返回读取的字符串）
     * 读取应用的包下 /data/data/<package name>/files/
     *
     * @param filename
     * @return
     */
    public static String readFile(Context context, String filename) {
        String reads = null;
        try {
            FileInputStream fis = context.openFileInput(filename);
            byte[] b = new byte[1024];
            int len = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((len = fis.read(b)) != -1) {
                baos.write(b, 0, len);
            }
            baos.close();
            fis.close();
            reads = new String(baos.toByteArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return reads;
    }

    /**
     * 写入保存（str是要存入的字符串，filename是保存文件的名称）
     * 存储到应用的包下 /data/data/<package name>/files/
     *
     * @param str
     * @param filename
     */
    public static void saveFile(Context context, String str, String filename) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(filename, Activity.MODE_PRIVATE);
            fos.write(str.getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();

    }


    //flie：要删除的文件夹的所在位置
    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                Log.e("deletefile", "run:  delete3");
                File f = files[i];
                deleteFile(f);
            }
            file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            Log.e("deletefile", "run:  delete4");
            file.delete();
        }
    }


    public static boolean deleteFile(String url) {
        if (TextUtils.isEmpty(url))
            return false;
        boolean result = false;
        File file = new File(url);
        if (file.exists()) {
            result = file.delete();
        }
        return result;
    }

    public static void createDir(String path) {
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    public static String getFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;

        final String scheme = uri.getScheme();
        String data = null;

        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


    public static String getRootPath() {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bpx/";
        createDir(root);
        return root;
    }

    public static String getTempPath() {
        String temp = getRootPath() + "temp/";
        createDir(temp);
        return temp;
    }

    /**
     * app file儲存根目录
     *
     * @param context
     * @return
     */
    private static String getFilePath(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalFilesDir(null).getPath();
        } else {
            cachePath = context.getFilesDir().getPath();
        }
        return cachePath;
    }

    /**
     * 聊天缓存路径
     *
     * @param context
     * @return
     */
    public static String getChatFilePath(Context context) {
        String chatPath = getFilePath(context) + "/chat/";
        createDir(chatPath);
        return chatPath;
    }
    public static String getTempDownloadFilePath(Context context){
        String chatPath = getFilePath(context) + "/download/";
        createDir(chatPath);
        return chatPath;
    }

    /**
     * 删除聊天缓存
     *
     * @param context
     */
    public static void deleteChatCache(Context context) {
        deleteFile(new File(getChatFilePath(context)));
    }


    /**
     * app cache缓存根目录
     *
     * @param context
     * @return
     */
    private static String getCachePath(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }


    public static String getTempFilePath(Context context) {
        String cachePath = getCachePath(context) + "/temporal/";
        createDir(cachePath);
        return cachePath;
    }

//    public static void deleteTempCache(Context context){
//        deleteFile(new File(getTempFilePath(context)));
//    }


    public static String getStoreFilePath(Context context) {
        String store;
        store = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getPath();
        if (store.isEmpty()) {
            store = getFilePath(context);
        }
        store += "/store/";
        createDir(store);
        return store;
    }

    public static void deleteStoreFilePath(Context context) {
        deleteFile(new File(getStoreFilePath(context)));
    }


}

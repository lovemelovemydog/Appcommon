package com.common.app.imageasync;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.common.app.common.Log;
import com.common.app.common.util.StringUtil;
import com.common.app.http.HttpClientHelper;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * image load by fangzhu
 */
public class ImageAsyncLoader {
    public static final String TAG = "ImageAsyncLoader";
    private HashMap<String, SoftReference<Drawable>> cacheMap = null;
    private BlockingQueue<Runnable> queue = null;
    private ThreadPoolExecutor executor = null;
    static private ImageAsyncLoader _instance = null;

    static public ImageAsyncLoader instance() {
        if (null == _instance) {
            _instance = new ImageAsyncLoader();
        }
        return _instance;
    }

    public ImageAsyncLoader() {
        cacheMap = new HashMap<String, SoftReference<Drawable>>();
        queue = new LinkedBlockingQueue<Runnable>();
        /**
         * 具体参数详细测试后调整
         * 线程池维护线程的最少数量10
         * 线程池维护线程的最大数量30
         * 线程池维护线程所允许的空闲时间60秒
         */
        executor = new ThreadPoolExecutor(10, 30, 0, TimeUnit.SECONDS, queue);
    }

    public Drawable loadDrawableFileCache(final Context context, final String imageUrl,
                                          final Drawable defaultDrawable, final ImageCallback imageCallback) {
        if (imageUrl == null)
            return null;
        try {
            if (cacheMap.containsKey(imageUrl)) {
                SoftReference<Drawable> softReference = cacheMap.get(imageUrl);
                Drawable drawable = softReference.get();
                if (drawable != null) {
                    return drawable;
                }
            }

            final Handler handler = new Handler() {
                public void handleMessage(Message message) {
                    imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
                }
            };

            // 将任务添加到线程池
            executor.execute(new Runnable() {
                public void run() {
                    Drawable drawable = loadImageFromUrl(context, imageUrl, true);
                    if (null != drawable) {
                        cacheMap.put(imageUrl, new SoftReference<Drawable>(drawable));
                    } else {
                        drawable = defaultDrawable;
                    }

                    Message message = handler.obtainMessage(0, drawable);
                    handler.sendMessage(message);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
            return null;
        }
        return null;
    }

    public Drawable loadDrawable(final Context context, final String imageUrl,
                                 final Drawable defaultDrawable, final ImageCallback imageCallback) {
        if (imageUrl == null)
            return null;
        try {
            if (cacheMap.containsKey(imageUrl)) {
                SoftReference<Drawable> softReference = cacheMap.get(imageUrl);
                Drawable drawable = softReference.get();
                if (drawable != null) {
                    return drawable;
                }
            }

            final Handler handler = new Handler() {
                public void handleMessage(Message message) {
                    imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
                }
            };

            // 将任务添加到线程池
            executor.execute(new Runnable() {
                public void run() {
                    Drawable drawable = loadImageFromUrl(context, imageUrl, false);
                    if (null != drawable) {
                        cacheMap.put(imageUrl, new SoftReference<Drawable>(drawable));
                    } else {
                        drawable = defaultDrawable;
                    }

                    Message message = handler.obtainMessage(0, drawable);
                    handler.sendMessage(message);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static Drawable loadImageFromUrl(Context context, String imageUrl, boolean cacheflag) {
        Drawable drawable = null;

        if (imageUrl == null)
            return null;
        try {
            if (cacheflag) {
                String fileName = "";
                if (imageUrl.length() != 0) {
                    fileName = StringUtil.md5(imageUrl);
                }
                File file = new File(context.getCacheDir(), fileName);
                if (!file.exists() && !file.isDirectory()) {
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        InputStream is = HttpClientHelper.getImageStream(imageUrl);
                        BufferedOutputStream bos = new BufferedOutputStream(fos);
                        int len = 1024;
                        byte[] buff = new byte[len];
                        int data;
                        while ((data = is.read(buff, 0, len)) != -1) {
                            bos.write(buff, 0, data);
                        }
                        bos.close();
                        is.close();
                        String filepath = context.getCacheDir() + fileName;
                        drawable = Drawable.createFromPath(file.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                        file.delete();
                        return null;
                    }
                } else {
                    if (!file.isDirectory()) {
                        drawable = Drawable.createFromPath(file.toString());
                    } else {
                        return null;
                    }
                }
            } else {
                InputStream is = HttpClientHelper.getImageStream(imageUrl);
                if (is == null) {
                    return null;
                } else {
                    drawable = Drawable.createFromStream(is, "src");

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
            return null;
        }
        return drawable;
    }

    public interface ImageCallback {
        public void imageLoaded(Drawable imageDrawable, String imageUrl);
    }

    public static byte[] drawableToByteArray(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static byte[] drawableToByteArrayCompressed(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
        return stream.toByteArray();
    }

    public static void clearFileCache(Context context) {
        try {
            Log.v(TAG, "clearFileCache-");
            File cacheDir = context.getCacheDir();
            File files[] = cacheDir.listFiles();
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    File f = files[i];
                    if (f.isFile()) f.delete();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "clearFileCache error--");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static boolean clearImageCacheByUrl(Context context, String imageUrl) {
        try {
            if (context == null || imageUrl == null || imageUrl.trim().length() == 0) return false;
            String fileName = StringUtil.md5(imageUrl);
            File file = new File(context.getCacheDir(), fileName);
            if (file.exists() && !file.isDirectory()) {
                return file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return false;
        }
        return false;
    }
}

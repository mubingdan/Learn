package com.example.router.utils;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import dalvik.system.DexFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

public class ClassUtil {

    /**
     * 获取所有的apk path
     * @param context
     * @return
     */
    private static List<String> getSourcePaths(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            List<String> paths = new ArrayList<>();
            paths.add(applicationInfo.sourceDir);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (null != applicationInfo.splitSourceDirs) {
                    paths.addAll(Arrays.asList(applicationInfo.splitSourceDirs));
                }
            }
            return paths;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获有所以生成的路由表类
     * @param context
     * @param packageName
     * @return
     * @throws InterruptedException
     */
    public static Set<String> getFileNameByPackageName(Application context, final String packageName) throws InterruptedException {
        final Set<String> classNames = new HashSet<>();
        List<String> paths = getSourcePaths(context);
        if (null == paths) {
            return classNames;
        }

        final CountDownLatch downLatch = new CountDownLatch(paths.size());
        ThreadPoolExecutor poolExecutor = RouterPoolExecutor.newPoolExecutor(paths.size());
        if (null == poolExecutor) {
            return classNames;
        }
        for (final String path : paths) {
            poolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        DexFile dexFile = new DexFile(path);
                        Enumeration<String> entries = dexFile.entries();
                        while (entries.hasMoreElements()) {
                            String className = entries.nextElement();
                            if (!TextUtils.isEmpty(className) && className.startsWith(packageName)) {
                                classNames.add(className);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        downLatch.countDown();
                    }
                }
            });
        }
        downLatch.await();
        return classNames;
    }

}

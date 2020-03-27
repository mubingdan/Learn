package com.example.router;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import com.example.router.annotation.model.RouteMeta;
import com.example.router.model.PostCard;
import com.example.router.template.IRouteGroup;
import com.example.router.template.IRouteRoot;
import com.example.router.utils.ClassUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

public class Router {

    private static volatile Router instance = null;

    private static final String TAG = "Router";

    // 生成的路由对照表的包名
    private static final String ROUTER_PACKAGE_NAME = "com.example.router.routers";

    private static Application mContext;

    private Handler mHandler;

    private Router() {
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    public static Router getInstance() {
        if (null == instance) {
            synchronized (Router.class) {
                if (null == instance) {
                    instance = new Router();
                }
            }
        }

        return instance;
    }

    public static void init(Application application) {
        mContext = application;
        try {
            loadInfo(application);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public PostCard build(String path) {
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("路由地址不能为空");
        }

        if (!path.startsWith("/")) {
            throw new RuntimeException("路由地址格式不正确");
        }

        return build(path, getGroup(path));
    }

    public PostCard build(String path, String group) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(group)) {
            throw new RuntimeException("路由地址无效");
        }

        return new PostCard(path, group);
    }

    private String getGroup(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new RuntimeException("提取路由group错误");
        }

        return path.substring(1, path.indexOf("/", 1));
    }

    private static void loadInfo(Application application) throws InterruptedException, ClassNotFoundException,
            NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Set<String> classNames = ClassUtil.getFileNameByPackageName(application, ROUTER_PACKAGE_NAME);
        if (null == classNames || classNames.isEmpty()) {
            return;
        }

        for (String className : classNames) {
            if (className.startsWith(ROUTER_PACKAGE_NAME + ".Router_Root")) {
                ((IRouteRoot) (Class.forName(className).getConstructor().newInstance())).loadInfo(Warehouse.groups);
            }
        }

        for (Map.Entry<String, Class<? extends IRouteGroup>> entry : Warehouse.groups.entrySet()) {
            Log.d(TAG, "获取Group表 group:" + entry.getKey() + ", class:" + entry.getValue().getSimpleName());
        }
    }

    public Object navigation(final Context context, PostCard postCard, final int requestCode) {
        try {
            prepareCard(postCard);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (postCard.getType() == RouteMeta.Type.ACTIVITY) {
            final Context activity = null == context ? mContext : context;
            final Intent intent = new Intent(activity, postCard.getDilution());
            intent.putExtras(postCard.getBundle());
            if (postCard.getFlag() != -1) {
                intent.setFlags(postCard.getFlag());
            } else if (!(context instanceof Activity)){
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            this.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (requestCode > 0 && activity instanceof Activity) {
                        ActivityCompat.startActivityForResult((Activity) activity, intent, requestCode, null);
                    } else {
                        ActivityCompat.startActivity(activity, intent, null);
                    }
                }
            });
        }

        return null;
    }

    private void prepareCard(PostCard postCard) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (null == postCard) {
            return;
        }

        RouteMeta routeMeta = Warehouse.routes.get(postCard.getPath());
        if (null == routeMeta) {
            Class<? extends IRouteGroup> group = Warehouse.groups.get(postCard.getGroup());
            if (null == group) {
                throw new RuntimeException("未找到路由表");
            }

            IRouteGroup routeGroup = group.getConstructor().newInstance();
            routeGroup.loadInfo(Warehouse.routes);
            Warehouse.groups.remove(postCard.getGroup());
            prepareCard(postCard);
        } else {
            postCard.setDilution(routeMeta.getDilution());
            postCard.setType(routeMeta.getType());
        }
    }
}

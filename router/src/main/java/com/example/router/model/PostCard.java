package com.example.router.model;

import android.content.Context;
import android.os.Bundle;
import com.example.router.Router;
import com.example.router.annotation.model.RouteMeta;

public class PostCard extends RouteMeta {

    private Bundle bundle;
    private int flag = -1;

    public PostCard(String path, String group) {
        this(path, group, null);
    }

    public PostCard(String path, String group, Bundle bundle) {
        setPath(path);
        setGroup(group);
        this.bundle = (null == bundle ? new Bundle() : bundle);
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public Object navigation() {
        return Router.getInstance().navigation(null, this, -1);
    }

    public Object navigation(Context context) {
        return Router.getInstance().navigation(context, this, -1);
    }
}

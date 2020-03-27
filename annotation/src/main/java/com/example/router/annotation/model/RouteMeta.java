package com.example.router.annotation.model;

import com.example.router.annotation.Route;

import javax.lang.model.element.Element;

public class RouteMeta {

    public enum Type {
        ACTIVITY
    }

    private String path;

    private String group;

    private Type type;

    private Class<?> dilution;

    private Element element;

    public static RouteMeta build(Type type, Class<?> dilution, String path, String
            group) {
        return new RouteMeta(type, null, dilution, path, group);
    }

    public RouteMeta() {

    }

    public RouteMeta(Type type, Element element, Route route) {
        this.type = type;
        this.element = element;
        this.path = route.path();
        this.group = route.group();

        // 初始化是检查group值
        checkGroup();
    }

    public RouteMeta(Type type, Element element, Class<?> dilution, String path, String group) {
        this.type = type;
        this.path = path;
        this.group = group;
        this.dilution = dilution;
        this.element = element;

        // 初始化是检查group值
        checkGroup();
    }

    private void checkGroup() {
        if (null == this.path || this.path.isEmpty()) {
            return;
        }

        if (null == this.group || this.group.isEmpty()) {
            this.group = path.substring(1, path.indexOf("/", 1));
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Class<?> getDilution() {
        return dilution;
    }

    public void setDilution(Class<?> dilution) {
        this.dilution = dilution;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }
}

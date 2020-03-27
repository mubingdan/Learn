package com.example.router.template;

import com.example.router.annotation.model.RouteMeta;

import java.util.Map;

public interface IRouteGroup {

    void loadInfo(Map<String, RouteMeta> map);
}

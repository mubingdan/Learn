package com.example.router;

import com.example.router.annotation.model.RouteMeta;
import com.example.router.template.IRouteGroup;

import java.util.HashMap;
import java.util.Map;

public class Warehouse {

    static final Map<String, Class<? extends IRouteGroup>> groups = new HashMap<>();

    static final Map<String, RouteMeta> routes = new HashMap<>();

}

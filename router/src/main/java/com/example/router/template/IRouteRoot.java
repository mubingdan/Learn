package com.example.router.template;

import java.util.Map;

public interface IRouteRoot {

    void loadInfo(Map<String, Class<? extends IRouteGroup>> routes);
}

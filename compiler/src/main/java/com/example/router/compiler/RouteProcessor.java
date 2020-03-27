package com.example.router.compiler;

import com.example.router.Constants;
import com.example.router.annotation.Route;
import com.example.router.annotation.model.RouteMeta;
import com.example.router.utils.LogUtil;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.util.*;

@AutoService(Processor.class)
@SupportedOptions(Constants.ARGUMENTS_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("com.example.router.annotation.Route")
public class RouteProcessor extends AbstractProcessor {

    // 文件生成器
    private Filer filer;
    // 类信息工具类
    private Types types;
    /**
     * 节点工具类 (类、函数、属性都是节点)
     */
    private Elements elementsUtils;

    private String modelName;

    private LogUtil logUtil;

    private Map<String, String> routeMap = new HashMap<>();

    private Map<String, List<RouteMeta>> groupMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        this.filer = processingEnvironment.getFiler();
        this.types = processingEnvironment.getTypeUtils();
        this.elementsUtils = processingEnvironment.getElementUtils();

        Map<String, String> options = processingEnvironment.getOptions();
        if (null != options && options.containsKey(Constants.ARGUMENTS_NAME)) {
            this.modelName = options.get(Constants.ARGUMENTS_NAME);
        }

        if (null == modelName || modelName.isEmpty()) {
            throw new RuntimeException("Not find processor modelName option !");
        }

        this.logUtil = LogUtil.newLog(processingEnvironment.getMessager());
        logUtil.i("Route processor init " + modelName + " success");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Route.class);
        if (null != elements && !elements.isEmpty()) {
            processRouteElements(elements);
            return true;
        }
        return false;
    }

    /**
     * 解析Route注解的类集合
     * @param elements
     */
    private void processRouteElements(Set<? extends Element> elements) {
        TypeElement activityElement = this.elementsUtils.getTypeElement(Constants.ACTIVITY);
        for (Element element : elements) {
            RouteMeta routeMeta;
            TypeMirror typeMirror =  element.asType();
            if (this.types.isSubtype(typeMirror, activityElement.asType())) {
                Route route = element.getAnnotation(Route.class);
                logUtil.i("找到协议：" + route.path() + ", group:" + route.group() + ", class: " + element.getSimpleName());
                routeMeta = new RouteMeta(RouteMeta.Type.ACTIVITY, element, route);
            } else {
                throw new RuntimeException("Just support Activity Route: " + element);
            }

            buildGroupMap(routeMeta);
        }

        this.generatedGroup();
        this.generatedRoot();
    }

    /**
     * 生成group对应的group类
     */
    private void generatedRoot() {
        TypeElement groupElement = elementsUtils.getTypeElement(Constants.IROUTE_GROUP);
        ParameterizedTypeName parameterTypeName = ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(String.class), ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(groupElement))));

        // Class<? extends IRouteGroup> routes
        ParameterSpec parameterSpec = ParameterSpec.builder(parameterTypeName, "routes").build();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("loadInfo")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(parameterSpec);

        for (Map.Entry<String, String> entry : this.routeMap.entrySet()) {
            methodBuilder.addStatement("routes.put($S, $T.class)", entry.getKey(),
                    ClassName.get(Constants.ROUTE_PACKAGE_NAME, entry.getValue()));
        }

        String rootClassName = Constants.NAME_OF_ROOT + this.modelName;
        TypeElement rootElement = elementsUtils.getTypeElement(Constants.IROUTE_ROOT);
        TypeSpec typeSpec = TypeSpec.classBuilder(rootClassName).addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(rootElement))
                .addMethod(methodBuilder.build())
                .build();
        try {
            JavaFile javaFile = JavaFile.builder(Constants.ROUTE_PACKAGE_NAME, typeSpec).build();
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成model group对应的类，保存path及对应的class
     */
    private void generatedGroup() {
        TypeElement typeElement = elementsUtils.getTypeElement(Constants.IROUTE_GROUP);
        ParameterizedTypeName routeMap = ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(String.class), ClassName.get(RouteMeta.class));
        ParameterSpec mapSpec = ParameterSpec.builder(routeMap, "map").build();
        for (Map.Entry<String, List<RouteMeta>> entry : this.groupMap.entrySet()) {
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("loadInfo")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(mapSpec);

            String group = entry.getKey();
            List<RouteMeta> list = entry.getValue();
            for (RouteMeta routeMeta : list) {
                // 向函数体内增加map put的内容
                methodBuilder.addStatement("map.put($S, $T.build($T.$L, $T.class, $S, $S))",
                        routeMeta.getPath(), ClassName.get(RouteMeta.class),
                        ClassName.get(RouteMeta.Type.class), routeMeta.getType(),
                        ClassName.get((TypeElement) routeMeta.getElement()),
                        routeMeta.getPath(), routeMeta.getGroup());
            }

            String groupClassName = Constants.NAME_OF_GROUP + group;
            TypeSpec typeSpec = TypeSpec.classBuilder(groupClassName).addSuperinterface(ClassName.get(typeElement))
                    .addModifiers(Modifier.PUBLIC).addMethod(methodBuilder.build()).build();
            JavaFile javaFile = JavaFile.builder(Constants.ROUTE_PACKAGE_NAME, typeSpec).build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.routeMap.put(group, groupClassName);
        }
    }

    /**
     * 创建并加入group对应的路由信息
     * @param routeMeta
     */
    private void buildGroupMap(RouteMeta routeMeta) {
        if (checkPathVerify(routeMeta)) {
            List<RouteMeta> metas = this.groupMap.get(routeMeta.getGroup());
            if (null == metas) {
                metas = new ArrayList<>();
                metas.add(routeMeta);
                this.groupMap.put(routeMeta.getGroup(), metas);
            } else {
                metas.add(routeMeta);
            }
        } else {
            logUtil.i("checkPathVerify() path is not verify...");
        }
    }

    /**
     * 检测path是否合法
     * @param routeMeta
     * @return
     */
    private boolean checkPathVerify(RouteMeta routeMeta) {
        String path = routeMeta.getPath();
        String group = routeMeta.getGroup();
        return null != path && !path.isEmpty() && !group.isEmpty() && path.startsWith("/");
    }
}

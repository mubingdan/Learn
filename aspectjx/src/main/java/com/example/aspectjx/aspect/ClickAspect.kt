package com.example.aspectjx.aspect

import android.util.Log
import com.example.aspectjx.annotations.OnSingleClick
import com.example.aspectjx.utils.SingleClickUtil
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature

@Aspect
class ClickAspect {

    @Pointcut("call(* com.example.learndemo.MainActivity.clickHello())")
    fun onClickHello(){}

    @Pointcut("execution(@com.example.aspectjx.annotations.OnSingleClick * *(..))")
    fun onDoubleClickPoint() {}

    @Pointcut("withincode(* com.example.learndemo.MainActivity.testAop())")
    fun inClickHello() {}

    @Pointcut("call(* com.example.learndemo.MainActivity.testAopWithincode())")
    fun inClickHelloLoadData() {}

    @Pointcut("inClickHello() && inClickHelloLoadData()")
    fun clickHelloAndLoadData(){}

    @Around("onDoubleClickPoint()")
    fun aroundDoubleClickJoinPoint(joinPoint: ProceedingJoinPoint) {
        val methodSignature = joinPoint.signature as MethodSignature
        val method = methodSignature.method
        if (method.isAnnotationPresent(OnSingleClick::class.java)) {
            val onClick = method.getAnnotation(OnSingleClick::class.java)
            val value = onClick?.value ?: 1000
            Log.d("ClickAspect", "onDoubleClick value:$value")
            if (SingleClickUtil.isDoubleClick(value)) {
                Log.d("ClickAspect", "onDoubleClick is double click")
                return
            }

            joinPoint.proceed()
        }
    }

    @Before("onClickHello()")
    fun clickHelloJoinPoint(joinPoint: JoinPoint) {
        Log.d("ClickAspect", "before onClickHello...")
    }

    @Before("inClickHello()")
    fun withincodeClickHelloJoinPoint(joinPoint: JoinPoint) {
        Log.d("ClickAspect", "withincode click hello before....")
    }

    @Before("inClickHelloLoadData()")
    fun aopWithInCodeJoinPoint(joinPoint: JoinPoint) {
        Log.d("ClickAspect", "aopWithCode aopwithincode///")
    }

    @Before("clickHelloAndLoadData()")
    fun clickHelloLoadDataJoinPoint(joinPoint: JoinPoint) {
        Log.d("ClickAspect", "before onClickHello loadData...")
    }
}
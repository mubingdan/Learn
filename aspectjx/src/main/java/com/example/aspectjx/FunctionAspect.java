package com.example.aspectjx;

import android.util.Log;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class FunctionAspect {

    static final String TAG = "FunctionAspect";

    @Before("execution(* android.app.Activity.onCreate(..))")
    public void onActivityCreate(JoinPoint point) {
        Log.d(TAG, "before execution Activity onCreate..." + point.getSignature().toString());
    }

    @After("execution(* com.example.learndemo.MainActivity.onStart())")
    public void onActivityStart(JoinPoint point) {
        Log.d(TAG, "after execution Activity onStart..." + point.getSignature().toString());
    }

    @Around("execution(* com.example.learndemo.MainActivity.loadData(..))")
    public Object onLoadData(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (null != args && args.length > 0) {
            Object arg = args[0];
            if (arg instanceof Integer) {
                int id = (int) arg;
                if (id > 5) {
                    args[0] = 2;
                    return joinPoint.proceed(args);
                }
            }
        }

        return null;
    }

}

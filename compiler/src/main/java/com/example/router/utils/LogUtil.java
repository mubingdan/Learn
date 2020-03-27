package com.example.router.utils;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class LogUtil {

    private Messager messager;

    private LogUtil(Messager messager) {
        this.messager = messager;
    }

    public static LogUtil newLog(Messager messager) {
        return new LogUtil(messager);
    }

    public void i(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

}

package com.example.aspectjx.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OnSingleClick (

    val value: Long = 1000

)
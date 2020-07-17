package com.zeongit.share.annotations


/**
 * 标识需要包装的接口
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class RestfulPack
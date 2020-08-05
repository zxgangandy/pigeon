package io.andy.pigeon.rpc.core.client.annotation;

import java.lang.annotation.*;

/**
 * 消费服务注解
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcReference {

    String version() default "";

    String address() default "";
}

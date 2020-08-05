package io.andy.pigeon.rpc.core.server.annotation;

import java.lang.annotation.*;

/**
 * 服务提供者注解
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcService {

    String version() default "";
}

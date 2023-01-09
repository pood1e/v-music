package me.pood1e.vmusic.server.core.annotation;

import me.pood1e.vmusic.server.core.model.data.OutSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author pood1e
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ManagerType {
    OutSource value();
}

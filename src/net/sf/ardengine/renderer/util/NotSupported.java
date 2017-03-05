package net.sf.ardengine.renderer.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation warns programmer about methods,
 * which are not supported by specific renderer.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface NotSupported {

    /**
     * @return true, if method will be supported at future
     */
    public boolean isSupportPlanned() default false;
}

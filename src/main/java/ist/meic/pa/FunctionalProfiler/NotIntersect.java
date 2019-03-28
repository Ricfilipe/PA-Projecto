/*
 * Annotation inserted in classes we don't want to change
 */
package ist.meic.pa.FunctionalProfiler;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface NotIntersect {
}

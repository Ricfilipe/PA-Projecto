package ist.meic.pa.FunctionalProfilerExtended;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface NotIntersect {}

package pl.rychellos.hotel.lib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SearchFilter {
    // The field name in the Entity. If empty, use the DTO field name.
    String path() default "";

    Operator operator() default Operator.EQUAL;

    boolean ignoreIfNull() default true;

    enum Operator {
        EQUAL,
        NOT_EQUAL,
        LIKE_IGNORE_CASE, // String contains
        STARTING_WITH,    // String starts with
        IN,               // Collection IN
        GREATER_THAN,
        GREATER_THAN_EQ,
        LESS_THAN,
        LESS_THAN_EQ,
        IS_NULL,          // Triggered by boolean true in DTO
        IS_NOT_NULL       // Triggered by boolean true in DTO
    }
}

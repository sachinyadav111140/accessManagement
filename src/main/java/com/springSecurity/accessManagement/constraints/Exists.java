package com.springSecurity.accessManagement.constraints;



import com.springSecurity.accessManagement.constraints.validators.ExistsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = ExistsValidator.class)
@Target({
    TYPE, FIELD,
    ANNOTATION_TYPE
})
@Retention(RUNTIME)
@Documented
public @interface Exists {
    String message() default "{constraints.exists}";
    Class <?> [] groups() default {};
    Class <? extends Payload> [] payload() default {};
    String property();
    String repository();

    @Target({
        TYPE, FIELD,
        ANNOTATION_TYPE
    })
    @Retention(RUNTIME)
    @Documented
    @interface List {
        Exists[] value();
    }
}

package com.test.weathermusic.validator.annotation;

import com.test.weathermusic.validator.PlaylistParamsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by Eduardo on 29/01/2018.
 */
@Documented
@Constraint(validatedBy = PlaylistParamsValidator.class)
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface PlaylistParamsConstraint {
    String message() default "{validation.playlist.params}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

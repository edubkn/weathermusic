package com.test.weathermusic.validator;

import com.test.weathermusic.dto.PlaylistParamsDto;
import com.test.weathermusic.validator.annotation.PlaylistParamsConstraint;
import org.apache.commons.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by Eduardo on 29/01/2018.
 */
public class PlaylistParamsValidator implements ConstraintValidator<PlaylistParamsConstraint, PlaylistParamsDto> {

    @Override
    public void initialize(PlaylistParamsConstraint constraint) {
    }

    @Override
    public boolean isValid(PlaylistParamsDto params, ConstraintValidatorContext context) {
        return StringUtils.isNotBlank(params.getCity()) ||
                params.getLat() != null && params.getLon() != null;
    }
}

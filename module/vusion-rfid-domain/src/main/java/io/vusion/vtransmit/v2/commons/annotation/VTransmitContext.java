package io.vusion.vtransmit.v2.commons.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.vusion.vtransmit.v2.commons.model.EnumEventType;
import io.vusion.vtransmit.v2.commons.model.EnumPriority;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface VTransmitContext {

    EnumPriority priority() default EnumPriority.MEDIUM;

    EnumEventType eventType() default EnumEventType.UNDEFINED;
}

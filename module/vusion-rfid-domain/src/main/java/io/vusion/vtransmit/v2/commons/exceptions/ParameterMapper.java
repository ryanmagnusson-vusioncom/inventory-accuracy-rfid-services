package io.vusion.vtransmit.v2.commons.exceptions;

import static org.apache.commons.lang3.ClassUtils.isAssignable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import io.vusion.gson.utils.GsonHelper;

public interface ParameterMapper {
    NumberFormat intFormat     = new DecimalFormat("0");

    static String format(int value) { return intFormat.format(value); }

    static String format(short value) { return intFormat.format(value); }

    static String  format(long value) { return intFormat.format(value); }

    static String format(byte value) { return Integer.toString(value, 16); }

    static String format(double value) {
        final BigDecimal bd = BigDecimal.valueOf(value);
        return bd.scale() < 10
                          ? bd.toString()
                          : bd.setScale(10, RoundingMode.HALF_UP).toPlainString();
    }

    static String format(float value) {
        final BigDecimal bd = BigDecimal.valueOf(value);
        return bd.scale() < 10
                          ? bd.toString()
                          : bd.setScale(10, RoundingMode.HALF_UP).toPlainString();
    }

    static String format(String value) { return value; }

    static String format(Date dt) {
        return Optional.ofNullable(dt)
                       .map(Date::toInstant)
                       .map(Object::toString)
                       .orElse(null);
    }

    static <T extends Enum<T>> String format(T t) {
        return t == null ? null : t.name();
    }

    static boolean canFormat(Object value) {
        if (value == null) {
            return true;
        }

        return isAssignable(value.getClass(), Byte.class, true) ||
               isAssignable(value.getClass(), Short.class, true) ||
               isAssignable(value.getClass(), Integer.class, true) ||
               isAssignable(value.getClass(), Long.class, true) ||
               isAssignable(value.getClass(), Double.class, true) ||
               isAssignable(value.getClass(), Float.class, true) ||
               value instanceof String ||
               value instanceof Date ||
               value instanceof Enum;
    }

    static String format(Object value) {
        if (value == null) { return null; }

        if (value instanceof String response) {
            return response;
        }

        if (value instanceof CharSequence response) {
            try{
                return response.toString();
            } catch(Exception e){
                return "%s thrown while calling #toString on CharSequence. %s%n%s".formatted(
                        ClassUtils.getSimpleName(e), ExceptionUtils.getMessage(e),
                        ExceptionUtils.getStackTrace(e)
                );
            }
        }

        final Class<?> clazz = value.getClass();
        if (isAssignable(clazz, Byte.class, true)) {
            return format((byte)value);
        }

        if (isAssignable(clazz, Short.class, true)) {
            return format((short)value);
        }

        if (isAssignable(clazz, Integer.class, true)) {
            return format((int)value);
        }

        if (isAssignable(clazz, Long.class, true)) {
            return format((long)value);
        }

        if (isAssignable(clazz, Double.class, true)) {
            return format((double)value);
        }

        if (isAssignable(clazz, Float.class, true)) {
            return format((float)value);
        }

        if (value instanceof Date date) {
            return format(date);
        }

        if (value instanceof Enum) {
            return format(value);
        }

        return GsonHelper.toJson(value);
    }
}

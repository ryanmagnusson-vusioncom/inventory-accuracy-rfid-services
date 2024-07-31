package io.vusion.vtransmit.v2.commons.utils;

import java.util.Collection;

/**
 * Convenient interface for translatable item<br>
 * <u>Useful associated classes:</u><br>
 * <ul>
 * <li>{@link TranslatableDropDownList}
 * <li>{@link TranslatableEnumDropDownList}
 * </ul>
 *
 * @author Eric Lanoiselee
 * @date Mar 7, 2014
 * @version 1.0
 */
public interface In<T> {

	@SuppressWarnings("unchecked")
	default boolean in(final T... possibleValues) {

		for (final Object possibleValue : possibleValues) {
			if (this.equals(possibleValue)) {
				return true;
			}
		}

		return false;
	}

	default boolean in(final Collection<? extends T> possibleValues) {

		for (final Object possibleValue : possibleValues) {
			if (this.equals(possibleValue)) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	default boolean notIn(final T... impossibleValues) {
		return !in(impossibleValues);
	}

}

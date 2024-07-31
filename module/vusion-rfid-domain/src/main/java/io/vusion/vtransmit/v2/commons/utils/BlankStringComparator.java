package io.vusion.vtransmit.v2.commons.utils;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

import javax.annotation.Nullable;

import lombok.Getter;

/**
 * Compares two CharSequences, like Strings, to determine sort order between them. any blanks,
 * If either CharSequence is a {@link org.apache.commons.lang3.StringUtils#isBlank(CharSequence) blank} &#8212;
 * meaning it is only empty, whitespace, or a null reference &#8212; then they are treated as equal to a null.
 * <p>
 *     This imitates the behavior of {@link Comparator#nullsFirst(Comparator) Comparator#nullsFirst} and
 *     {@link Comparator#nullsLast(Comparator) Comparator#nullsLast}.
 * </p>
 */
public class BlankStringComparator implements Comparator<String>, Serializable {

    @Getter
    private final boolean blanksFirst;
    private final Comparator<String> real;

    /**
     * @param blanksFirst Flag if a blank is sorted as going first when compared to another String that is not blank
     * @param real The actual comparator to wrap
     */
    public BlankStringComparator(boolean blanksFirst, @Nullable Comparator<String> real) {
        this.real = real;
        this.blanksFirst = blanksFirst;
    }

    @Override
    public int compare(String left, String right) {
        if (isBlank(left)) {
            return isBlank(right) ? 0 : blanksFirst ? -1 : 1;
        }

        if (isBlank(right)) {
            return blanksFirst ? 1: -1;
        }

        return (real == null) ? 0 : real.compare(left, right);
    }

    @Override @SuppressWarnings("unchecked")
    public Comparator<String> thenComparing(Comparator<? super String> other) {
        Objects.requireNonNull(other, "The next Comparator to call is required in order to chain together");

        final Comparator<String> nextInChain = real == null
                                             ? (Comparator<String>) other
                                             : real.thenComparing(other);

        return new BlankStringComparator(blanksFirst, nextInChain);
    }

    @Override
    public Comparator<String> reversed() {
        return new BlankStringComparator(!blanksFirst, real == null ? null : real.reversed());
    }

    public static Comparator<String> blanksFirst() {
        return new BlankStringComparator(true, null);
    }

    public static Comparator<String> blanksFirst(Comparator<String> comparator) {
        return new BlankStringComparator(true, comparator);
    }

    public static Comparator<String> blanksLast() {
        return new BlankStringComparator(false, null);
    }

    public static Comparator<String> blanksLast(Comparator<String> comparator) {
        return new BlankStringComparator(false, comparator);
    }

    public static <T> Comparator<T> blanksFirst(Function<? super T, ? extends String> keyExtractor) {

        Objects.requireNonNull(keyExtractor, """
                                             Function to convert the provided object into a CharSequence key \
                                             is required in order to chain together""");

        return (Comparator<T> & Serializable)
                (c1, c2) -> new BlankStringComparator(true, null).compare(keyExtractor.apply(c1),
                                                                          keyExtractor.apply(c2));
    }

    @SuppressWarnings("unchecked")
    public static <T> Comparator<T> blanksFirst(Function<? super T, ? extends String> keyExtractor,
                                                Comparator<? super String> keyComparator) {
        Objects.requireNonNull(keyExtractor, """
                                             Function to convert the provided object into a CharSequence key \
                                             is required in order to chain together""");
        Objects.requireNonNull(keyComparator, """
                                             The next Comparator to call to compare the retrieved key \
                                             is required in order to chain them together""");

        final Comparator<String> comparator = (keyComparator instanceof BlankStringComparator)
                                            ? (Comparator<String>) keyComparator
                                            : new BlankStringComparator(true,
                                                                        (Comparator<String>) keyComparator);

        return (Comparator<T> & Serializable)
                (c1, c2) -> comparator.compare(keyExtractor.apply(c1), keyExtractor.apply(c2));



    }

    public static <T> Comparator<T> blanksLast(Function<? super T, ? extends String> keyExtractor) {

        Objects.requireNonNull(keyExtractor, """
                                             Function to convert the provided object into a CharSequence key \
                                             is required in order to chain together""");

        return (Comparator<T> & Serializable)
                (c1, c2) -> new BlankStringComparator(false, null).compare(keyExtractor.apply(c1),
                                                                           keyExtractor.apply(c2));
    }

    @SuppressWarnings("unchecked")
    public static <T> Comparator<T> blanksLast(Function<? super T, ? extends String> keyExtractor,
                                               Comparator<? super String> keyComparator) {
        Objects.requireNonNull(keyExtractor, """
                                             Function to convert the provided object into a CharSequence key \
                                             is required in order to chain together""");
        Objects.requireNonNull(keyComparator, """
                                             The next Comparator to call to compare the retrieved key \
                                             is required in order to chain them together""");

        final Comparator<String> comparator = (keyComparator instanceof BlankStringComparator)
                                            ? (Comparator<String>) keyComparator
                                            : new BlankStringComparator(false,
                                                                        (Comparator<String>) keyComparator);

        return (Comparator<T> & Serializable)
                (c1, c2) -> comparator.compare(keyExtractor.apply(c1), keyExtractor.apply(c2));
    }
}

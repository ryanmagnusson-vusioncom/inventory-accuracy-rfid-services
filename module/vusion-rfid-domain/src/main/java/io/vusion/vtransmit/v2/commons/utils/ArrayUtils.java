package io.vusion.vtransmit.v2.commons.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayUtils {
	public static <T> List<T[]> chunks(final T[] array,
			final int chunkSize) {
		final int numberOfArrays = array.length / chunkSize;
		final int remainder = array.length % chunkSize;

		int start = 0;
		int end = 0;

		final List<T[]> list = new ArrayList<>();
		for (int i = 0; i < numberOfArrays; i++) {
			end += chunkSize;
			list.add(Arrays.copyOfRange(array, start, end));
			start = end;
		}

		if (remainder > 0) {
			list.add(Arrays.copyOfRange(array, start, (start + remainder)));
		}
		return list;
	}
}

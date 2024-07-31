package io.vusion.vtransmit.v2.commons.utils;

import java.math.BigInteger;

/**
 * Helper class to encode and decode to and from base58 data.
 */
public class Base58 {

    private static final String ALPHABET = "123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final BigInteger BASE = BigInteger.valueOf(58);

    public static String encode(final byte[] input) {
        BigInteger bi = new BigInteger(1, input);
        final StringBuffer s = new StringBuffer();

        while (bi.compareTo(BASE) >= 0) {
            final BigInteger mod = bi.mod(BASE);
            s.insert(0, ALPHABET.charAt(mod.intValue()));
            bi = bi.subtract(mod).divide(BASE);
        }

        s.insert(0, ALPHABET.charAt(bi.intValue()));

        // Convert leading zeros too.
        for (final byte anInput : input) {
            if (anInput == 0) {
                s.insert(0, ALPHABET.charAt(0));
            }
            else {
                break;
            }
        }

        return s.toString();
    }

    public static byte[] decode(final String input) {
        if (input.length() == 0) {
            return null;
        }

        final BigInteger decoded = decodeToBigInteger(input);

        if (decoded == null) {
            return null;
        }

        final byte[] bytes = decoded.toByteArray();

        // We may have got one more byte than we wanted, if the high bit of the next-to-last byte was not zero. This
        // is because BigIntegers are represented with twos-compliment notation, thus if the high bit of the last
        // byte happens to be 1 another 8 zero bits will be added to ensure the number parses as positive. Detect
        // that case here and chop it off.
        final boolean stripSignByte = bytes.length > 1 && bytes[0] == 0 && bytes[1] < 0;

        // Count the leading zeros, if any.
        int leadingZeros = 0;

        for (int i = 0; i < input.length() && input.charAt(i) == ALPHABET.charAt(0); i++) {
            leadingZeros++;
        }

        // Now cut/pad correctly. Java 6 has a convenience for this, but Android
        // can't use it.
        final byte[] tmp = new byte[bytes.length - (stripSignByte ? 1 : 0) + leadingZeros];
        System.arraycopy(bytes, stripSignByte ? 1 : 0, tmp, leadingZeros, tmp.length - leadingZeros);
        return tmp;
    }

    private static BigInteger decodeToBigInteger(final String input) {
        BigInteger bi = BigInteger.valueOf(0);

        // Work backwards through the string.
        for (int i = input.length() - 1; i >= 0; i--) {
            final int alphaIndex = ALPHABET.indexOf(input.charAt(i));

            if (alphaIndex == -1) {
                return null;
            }

            bi = bi.add(BigInteger.valueOf(alphaIndex).multiply(BASE.pow(input.length() - 1 - i)));
        }

        return bi;
    }

    public static String encodeWithChecksum(final byte[] input) {
        final String encoded = encode(input);
        return encoded + calculateCheckSum(encoded);
    }

    public static char calculateCheckSum(final String encoded) {
        int sumOdd = 0;
        int sumEven = 0;

        for (int index = 0; index < encoded.length(); index++) {
            final int alphaIndex = ALPHABET.indexOf(encoded.charAt(index));

            if (index % 2 == 0) {
                sumOdd += alphaIndex;
            }
            else {
                sumEven += alphaIndex;
            }
        }

        return ALPHABET.charAt((sumEven + 3 * sumOdd) % 58);
    }
}

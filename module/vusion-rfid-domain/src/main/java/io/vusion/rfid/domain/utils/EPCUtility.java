package io.vusion.rfid.domain.utils;


import io.vusion.rfid.domain.model.EPCHeader;
import io.vusion.rfid.domain.model.SGTIN96;
import io.vusion.rfid.domain.model.SGTINFilter;
import io.vusion.rfid.domain.model.SGTINPartition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.right;
import static org.apache.commons.lang3.StringUtils.stripToEmpty;


public class EPCUtility {

    private static final int header = 48; // 8-bits
    private static final int filterValueMin = 0;
    private static final int filterValueMax = 7;
    private static final int partitionValueMin = 0;
    private static final int partitionValueMax = 6;
    private static final int CONVERTHEX = 16;
    private static final int CONVERTBINARY = 2;

    private static final Logger LOGGER = LoggerFactory.getLogger(EPCUtility.class);
    public static final String SGTIN96_ERROR = "Given EPC is not in SGTIN-96 format";


    // Returns 14-digit gtin assuming the passed epc is in strictly SGTIN-96 format
    // TODO: When expanding this to other vendors change this to handle other formats if any.


    /**
     * Returns 14 digit gtin UPC assuming the passed epc is in strictly SGTIN-96 format
     * @param epcString SGTIN-96 format EPC
     * @return Either the 14 digit gtin UPC or the EPC passed if in failure.
     */
    public static String calculateGtin(String epcString) {

        if (isBlank(epcString)) {
            return null;
        }

        final SGTIN96 epc = parseSgtin96(epcString);

        if (epc != null) {

            return String.format("%14s", epc.toUpc()).replace(' ', '0');

        }
        else {
            LOGGER.error(SGTIN96_ERROR + ". Given de-serialized EPC: " + epcString);

            // Right now we want to flag out such errors and we don't want to interrupt the job, hence we return the 'epc' itself.
            return epcString;
        }

    }

    static final Pattern IS_BINARY_REGEX = Pattern.compile("^[01]+$");
    static final Pattern IS_BASE10_DIGITS_REGEX = Pattern.compile("^[0-9]+$");

    static boolean isBinary(String text) {
        return IS_BINARY_REGEX.matcher(text).matches();
    }

    static boolean isBase10Digits(String text) {
        return IS_BASE10_DIGITS_REGEX.matcher(text).matches();
    }

    public static boolean isValidEPC(String text) {
        final String cleaned = stripToEmpty(text).replaceAll("[\\s+:\\-\\.]+", "");
        if (isBlank(cleaned) || cleaned.length() < 2) {
            return false;
        }


        final EPCHeader header;
        String numericalEncodedFormat;
        String hexEncodedFormat;
        String binaryEncodedFormat;
        if (isBinary(cleaned)) {
            if (cleaned.length() < 8) {
                 return false;
            }
            hexEncodedFormat = new BigInteger(cleaned, CONVERTBINARY).toString(CONVERTHEX);
            header = EPCHeader.fromString(left(hexEncodedFormat, 2));
            binaryEncodedFormat = cleaned;
//        } else if (isBase10Digits(cleaned)) {
//            header ==
        } else {
            binaryEncodedFormat = new BigInteger(cleaned, CONVERTHEX).toString(CONVERTBINARY);
            hexEncodedFormat = cleaned;
            header = EPCHeader.fromString(left(cleaned, 2));
        }

        if (header == null) {
            return false;
        }

        if (header == EPCHeader.SGTIN_96) {
            binaryEncodedFormat = right(leftPad(binaryEncodedFormat, 96, '0'), 96);

            final int filterValue = Integer.parseInt(binaryEncodedFormat.substring(8, 8 + 3), CONVERTBINARY);
            final SGTINFilter filter = SGTINFilter.fromInt(filterValue);

            final int partitionValue = Integer.parseInt(binaryEncodedFormat.substring(11, 11 + 3), CONVERTBINARY);
            final SGTINPartition partition = SGTINPartition.fromInt(partitionValue);

            return filter != null && partition != null;
        }
        return false;
    }

    // Populates this object with processed data from given epc
    public static SGTIN96 parseSgtin96(String epcString) {

        if (isBlank(epcString)) {
            throw new RuntimeException("SGTIN-96 EPC is blank");
        }

        SGTIN96 epc = new SGTIN96();
        final String cleanedUp = stripToEmpty(epcString.replaceAll("[\\s\\-:\\.]+", ""));

        if (isValidEPC(cleanedUp)) {

            final String binaryEncodedFormat = leftPad(new BigInteger(cleanedUp, CONVERTHEX).toString(CONVERTBINARY), 96, '0');

            // Extract the Filter Value (starts at the 8th bit, and is 3 bits long)
            final int filterId = Integer.parseInt(binaryEncodedFormat.substring(8, 8 + 3), CONVERTBINARY);
            final SGTINFilter filter = SGTINFilter.fromInt(filterId);

            // Extract the Partition Value (starts at the 11th bit and is 3 bits long)
            final int partitionId = Integer.parseInt(binaryEncodedFormat.substring(11, 11 + 3), CONVERTBINARY);
            final SGTINPartition partition = SGTINPartition.fromInt(partitionId);

            // Company prefix and item reference lengths are determined by the partition

            // Extract the Company Prefix (Starts at bit 14, and goes the specified amount, based on the partition)
            epc.setCompanyPrefix(new BigInteger(binaryEncodedFormat.substring(14, 14 + partition.getCompany().getBits()), CONVERTBINARY));

            // Extract the Item Reference (Starts at bit 14 + company prefix bit length, and goes the specified amount, based on the partition)
            epc.setItemReference(new BigInteger(binaryEncodedFormat.substring(14 + partition.getCompany().getBits(),
                                                                              14 + partition.getCompany().getBits() + partition.getItemReference().getBits()), CONVERTBINARY));


            // Extract the Serial number (Starts at bit 58, and goes 38 bits)
            epc.setSerialNumber(new BigInteger(binaryEncodedFormat.substring(58, 58 + 38), 2));

            // Pad the company prefix to the specified amount
            final String companyPrefixFormat = "%0" + partition.getCompany().getDigits() + "d";
            final String formattedCompanyPrefix = String.format(companyPrefixFormat, epc.getCompanyPrefix());

            // Pad the item reference to the specified amount
            final String itemRefFormat = "%0" + partition.getItemReference().getDigits() + "d";
            final String formattedItemRef = String.format(itemRefFormat, epc.getItemReference());

            // Take the first digit from the item reference, plus the entire company prefix,
            // plus the remaining item reference (minus the first digit)
            epc.setUpc(formattedItemRef.substring(0, 1) + formattedCompanyPrefix + formattedItemRef.substring(1));

            // Now calculate the UPC check digit
            epc.setUpcCheckDigit(calculateUpcCheckDigit(epc.getUpc()));

        } else {
            return null;
        }

        return epc;
    }

    public static int calculateUpcCheckDigit(String UPC) {
        int check = 0;
        if (isBlank(UPC)) {
            return check;
        }

        final String stripped = StringUtils.strip(UPC);
        final String onlyNumbers = stripped.replaceAll("[^0-9]", "");
        if (stripped.equals(onlyNumbers)) {

            // pad with zeros to lengthen to 13 digits
            final String padded = leftPad(stripped, 13);

            // evaluate check digit
            int[] a = new int[13];
            a[0] = Character.getNumericValue(padded.charAt(0)) * 3;
            a[1] = Character.getNumericValue(padded.charAt(1));
            a[2] = Character.getNumericValue(padded.charAt(2)) * 3;
            a[3] = Character.getNumericValue(padded.charAt(3));
            a[4] = Character.getNumericValue(padded.charAt(4)) * 3;
            a[5] = Character.getNumericValue(padded.charAt(5));
            a[6] = Character.getNumericValue(padded.charAt(6)) * 3;
            a[7] = Character.getNumericValue(padded.charAt(7));
            a[8] = Character.getNumericValue(padded.charAt(8)) * 3;
            a[9] = Character.getNumericValue(padded.charAt(9));
            a[10] = Character.getNumericValue(padded.charAt(10)) * 3;
            a[11] = Character.getNumericValue(padded.charAt(11));
            a[12] = Character.getNumericValue(padded.charAt(12)) * 3;
            int sum = a[0] + a[1] + a[2] + a[3] + a[4] + a[5] + a[6] + a[7] + a[8] + a[9] + a[10] + a[11] + a[12];
            check = (10 - (sum % 10)) % 10;
        }

        return check;
    }
}
package com.github.reducktion.socrates.validator;

import java.time.Year;

import org.apache.commons.lang3.StringUtils;

import com.github.reducktion.socrates.internal.DateValidator;
import com.github.reducktion.socrates.internal.TwoYearDateParser;

/**
 * National Identification Number validator for Denmark.
 *
 * This validation algorithm is based on the following wikipedia page:
 * https://en.wikipedia.org/wiki/Personal_identification_number_(Denmark)
 */
class DenmarkIdValidator implements IdValidator {

    private static final int ID_NUMBER_OF_CHARACTERS = 10;
    private static final int BASE_10_RADIX = 10;
    private static final int[] MULTIPLIERS = { 4, 3, 2, 7, 6, 5, 4, 3, 2, 1 };

    private final TwoYearDateParser twoYearDateParser = new TwoYearDateParser(Year.now().getValue());

    @Override
    public boolean validate(final String id) {
        if (id == null) {
            return false;
        }

        final String sanitizedId = sanitize(id);

        if (sanitizedId.length() != ID_NUMBER_OF_CHARACTERS || !StringUtils.isNumeric(sanitizedId)) {
            return false;
        }

        return validateDateOfBirth(sanitizedId) && validateChecksum(sanitizedId);
    }

    private String sanitize(final String id) {
        return id.replaceAll("[ -]", "");
    }

    private boolean validateChecksum(final String id) {
        int sum = 0;
        for (int i = 0; i < id.length() && i < MULTIPLIERS.length; i++) {
            final int digit = Character.digit(id.charAt(i), BASE_10_RADIX);
            sum += digit * MULTIPLIERS[i];
        }
        return sum % 11 == 0;
    }

    private boolean validateDateOfBirth(final String id) {
        final int day = Integer.parseInt(id.substring(0, 2));
        final int month = Integer.parseInt(id.substring(2, 4));
        final int year = twoYearDateParser.parse(id.substring(4, 6)).orElse(0);

        return DateValidator.validate(year, month, day);
    }
}

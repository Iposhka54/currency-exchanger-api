package util;

import lombok.experimental.UtilityClass;
import model.dto.CreateCurrencyDto;

@UtilityClass
public class Validator {
    public static final Integer CORRECT_CODE_LENGTH = 3;
    public static final Integer CORRECT_NAME_LENGTH = 40;
    public static final Integer CORRECT_PAIR_LENGTH = 6;
    public static final Integer CORRECT_SIGN_LENGTH = 3;
    private static final String REGEX_SIGN = "[a-zA-Z]{0,2}[\u20A0-\u20BF$¥£₹€₽₺]$";
    private static final String REGEX_SPLIT = " ";
    private static final String REGEX_NAME = "[a-zA-Zа-яА-Я]+";

    public static boolean isValidCurrencyParams(CreateCurrencyDto currency){
        if(!isValidCurrencyCode(currency.getCode())){
            return false;
        }

        if(!isValidCurrencyName(currency.getName())){
            return false;
        }

        if(currency.getSign().length() > CORRECT_SIGN_LENGTH){
            return false;
        }

        if (!isCorrectSign(currency.getSign())) {
            return false;
        }

        return true;
    }

    public static boolean isValidPair(String first, String second){
        if (!isValidCurrencyCode(first) || !isValidCurrencyCode(second)){
            return false;
        }
        return true;
    }

    public static boolean isValidCurrencyCode(String code){
        if(code.length() != CORRECT_CODE_LENGTH){
            return false;
        }
        for (char c : code.toCharArray()) {
            if(Character.UnicodeBlock.BASIC_LATIN != Character.UnicodeBlock.of(c)
                    || !Character.isLetter(c)){
                return false;
            }
        }
        return true;
    }

    private static boolean isValidCurrencyName(String name) {
        if (name == null || name.length() > CORRECT_NAME_LENGTH) {
            return false;
        }

        String[] parts = name.split(REGEX_SPLIT);
        for (String part : parts) {
            if (!part.matches(REGEX_NAME)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isCorrectSign(String sign) {
        return sign.matches(REGEX_SIGN);
    }
}

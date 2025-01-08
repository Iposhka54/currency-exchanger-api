package util;

import lombok.experimental.UtilityClass;
import model.dto.CreateCurrencyDto;

@UtilityClass
public class Validator {
    public static final Integer CORRECT_CODE_LENGTH = 3;
    public static final Integer CORRECT_NAME_LENGTH = 40;
    public static final Integer CORRECT_SIGN_LENGTH = 3;
    public static boolean isValidCurrencyParams(CreateCurrencyDto currrency){
        if(currrency.getCode().length() != CORRECT_CODE_LENGTH){
            return false;
        }
        for (char c : currrency.getCode().toCharArray()) {
            if(Character.UnicodeBlock.BASIC_LATIN != Character.UnicodeBlock.of(c)
            || !Character.isLetter(c)){
                return false;
            }
        }
        if(currrency.getName().length() != CORRECT_NAME_LENGTH){
            return false;
        }
        for (char c : currrency.getName().toCharArray()) {
            if(Character.UnicodeBlock.BASIC_LATIN != Character.UnicodeBlock.of(c) &&
            Character.UnicodeBlock.CYRILLIC != Character.UnicodeBlock.of(c)
            || !Character.isLetter(c)){
                return false;
            }
        }

        if(currrency.getSign().length() > CORRECT_SIGN_LENGTH){
            return false;
        }
        for (char c : currrency.getSign().toCharArray()) {
            if(Character.isDigit(c)){
                return false;
            }
        }
        return true;
    }
}

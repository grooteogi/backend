package grooteogi.utils;

import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class Validator {
  public void confirmPasswordVerification(String password) {
    Pattern pwPattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$");
    Matcher pwMatcher = pwPattern.matcher(password);

    if (!pwMatcher.find()) {
      throw new ApiException(ApiExceptionEnum.PASSWORD_VALUE_EXCEPTION);
    }

  }

}

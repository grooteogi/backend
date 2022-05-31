package grooteogi.utils;

import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsClient {

  @Value("${custom.sms.api-key}")
  private String apiKey;

  @Value("${custom.sms.api-secret}")
  private String apiSecret;

  @Value("${custom.sms.source}")
  private String from;

  public void certifiedPhoneNumber(String phoneNumber, String cerNum) {
    Message coolsms = new Message(apiKey, apiSecret);

    HashMap<String, String> params = new HashMap<>();
    params.put("to", phoneNumber);
    params.put("from", from);
    params.put("type", "SMS");
    params.put("text", "휴대폰인증 테스트 메시지 : 인증번호는" + "[" + cerNum + "]" + "입니다.");

    try {
      JSONObject obj = coolsms.send(params);
      System.out.println(obj.toString());
    } catch (CoolsmsException e) {
      System.out.println(e.getMessage());
      System.out.println(e.getCode());
    }
  }
}

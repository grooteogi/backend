package grooteogi.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Data
public class Token {
    private String accessToken;
    private String refreshToken;
}

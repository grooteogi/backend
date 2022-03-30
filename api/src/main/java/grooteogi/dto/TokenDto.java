package grooteogi.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Data
public class TokenDto {
    private String accessToken;
    private String refreshToken;
}

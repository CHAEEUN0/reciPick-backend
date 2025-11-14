package team3.recipick.jwt;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TokenRequest {
    @NotNull
    String refreshToken;
}

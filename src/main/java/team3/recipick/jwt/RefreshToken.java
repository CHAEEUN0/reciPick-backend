package team3.recipick.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value="refreshToken")
public class RefreshToken {

    @Id
    private String loginId;

    private String token;

    @TimeToLive
    private Long expiration;

    public void updateValue(String newToken) {
        this.token = newToken;
    }

    public static RefreshToken of(String loginId, String token, Long expiration) {
        return RefreshToken.builder()
                .loginId(loginId)
                .token(token)
                .expiration(expiration)
                .build();
    }

}

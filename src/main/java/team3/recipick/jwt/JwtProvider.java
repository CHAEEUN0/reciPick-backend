package team3.recipick.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JwtProvider {

    private final MemberDetailsService memberDetailsService;
    private final SecretKey secretKey;
    private final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 60; // 1시간
    private final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 14; // 14일
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtProvider(@Value("${jwt.secret}") String secret,
                       MemberDetailsService memberDetailsService,
                       RefreshTokenRepository refreshTokenRepository) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.memberDetailsService = memberDetailsService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public TokenResponse generate(String loginId){
        Date now = new Date();
        Date accessTokenExpiredAt = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);
        Date refreshTokenExpiredAt = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        String accessToken = createToken(loginId, accessTokenExpiredAt);
        String refreshToken = createToken(loginId, refreshTokenExpiredAt);

        refreshTokenRepository.save(RefreshToken.of(loginId, refreshToken, REFRESH_TOKEN_EXPIRE_TIME/ 1000L));

        return TokenResponse.of(accessToken, refreshToken);

    }
    public TokenResponse reissueAccessToken(String refreshToken) {

        if (!validateToken(refreshToken)) {
            throw new JwtException("만료된 리프레시 토큰입니다.");
        }

        String loginId = getLoginId(refreshToken);
        RefreshToken stored = refreshTokenRepository.findById(loginId)
                .orElseThrow(() -> new JwtException("저장된 리프레시 토큰이 없습니다."));

        if (!stored.getToken().equals(refreshToken)) {
            throw new JwtException("토큰이 일치하지 않습니다.");
        }

        Date now = new Date();
        Date accessTokenExpiredAt = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);
        Date refreshTokenExpiredAt = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        String newAccessToken = createToken(loginId, accessTokenExpiredAt);
        String newRefreshToken = createToken(loginId, refreshTokenExpiredAt);

        stored.updateValue(newRefreshToken);
        refreshTokenRepository.save(stored);

        return TokenResponse.of(newAccessToken, newRefreshToken);
    }

    public String createToken(String loginId, Date expiredAt) {
        return Jwts.builder()
                .subject(loginId)
                .expiration(expiredAt)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
            return true;

        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new JwtException("유효하지 않은 토큰입니다.");
        } catch (ExpiredJwtException e){
            throw new JwtException("만료된 토큰입니다.");
        } catch (UnsupportedJwtException e){
            throw new JwtException("지원되지 않는 유형의 토큰입니다.");
        } catch (IllegalArgumentException e){
            throw new JwtException("클레임이 비어있습니다.");
        }
    }

    public String getLoginId(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (JwtException e) {
            throw new RuntimeException("유효하지 않은 JWT 토큰입니다.");
        }
    }

    public Authentication getAuthentication(String token) {
        String loginId = getLoginId(token);
        UserDetails memberDetails = memberDetailsService.loadUserByUsername(loginId);
        return new UsernamePasswordAuthenticationToken(memberDetails, null, memberDetails.getAuthorities());
    }



}

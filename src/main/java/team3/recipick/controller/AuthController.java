package team3.recipick.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team3.recipick.dto.LoginRequestDto;
import team3.recipick.dto.MemberResponseDto;
import team3.recipick.domain.Member;
import team3.recipick.dto.SignUpRequestDto;
import team3.recipick.jwt.*;
import team3.recipick.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid SignUpRequestDto dto){
        authService.register(dto);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequestDto dto){
        //로그인 성공시 AccessToken, RefreshToken 발급
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(@RequestBody TokenRequest request){
        TokenResponse newToken = authService.reissue(request);
        return ResponseEntity.ok(newToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal MemberDetails member) {
        refreshTokenRepository.deleteById(member.getUsername());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@AuthenticationPrincipal MemberDetails member){
        String loginId = member.getUsername();
        authService.withdraw(loginId);
        refreshTokenRepository.deleteById(loginId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<MemberResponseDto> me(@AuthenticationPrincipal MemberDetails member) {
        Member found = authService.findByLoginId(member.getUsername());
        return ResponseEntity.ok(MemberResponseDto.from(found));
    }

}

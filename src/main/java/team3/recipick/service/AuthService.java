package team3.recipick.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team3.recipick.domain.Basket;
import team3.recipick.domain.History;
import team3.recipick.dto.LoginRequestDto;
import team3.recipick.domain.Fridge;
import team3.recipick.domain.Member;
import team3.recipick.dto.SignUpRequestDto;
import team3.recipick.jwt.*;
import team3.recipick.repository.BasketRepository;
import team3.recipick.repository.FridgeRepository;
import team3.recipick.repository.HistoryRepository;
import team3.recipick.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final FridgeRepository fridgeRepository;
    private final BasketRepository basketRepository;
    private final HistoryRepository historyRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(SignUpRequestDto dto){
        memberRepository.findByLoginId(dto.getLoginId())
                .ifPresent(m -> {throw new RuntimeException("이미 존재하는 아이디 입니다.");});

        if (!dto.getPassword().equals(dto.getCheckPassword())){
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        Member member = new Member(dto.getLoginId(), encodedPassword);
        Member saved = memberRepository.save(member);

        fridgeRepository.save(new Fridge(saved));
        basketRepository.save(new Basket(saved));
        historyRepository.save(new History(saved));
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequestDto dto){
        Member member = memberRepository.findByLoginId(dto.getLoginId())
                .orElseThrow(() -> new RuntimeException("아이디가 존재하지 않습니다."));

        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new RuntimeException("비밀번호가 올바르지 않습니다.");
        }

        return jwtProvider.generate(member.getLoginId());
    }

    public TokenResponse reissue(TokenRequest request) {
        String refreshToken = request.getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new RuntimeException("RefreshToken이 필요합니다.");
        }
        return jwtProvider.reissueAccessToken(refreshToken);
    }

    @Transactional
    public void withdraw(String loginId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        memberRepository.delete(member);
        refreshTokenRepository.deleteById(loginId);
    }

    @Transactional(readOnly = true)
    public Member findByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
    }
}

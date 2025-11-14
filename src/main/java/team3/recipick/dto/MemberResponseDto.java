package team3.recipick.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import team3.recipick.domain.Member;

@Data @AllArgsConstructor(staticName = "of")
public class MemberResponseDto {
    private Long id;
    private String loginId;

    public static MemberResponseDto from(Member member){
        return MemberResponseDto.of(member.getId(), member.getLoginId());
    }
}

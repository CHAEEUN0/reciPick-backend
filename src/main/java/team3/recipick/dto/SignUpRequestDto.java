package team3.recipick.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequestDto {

    @NotBlank(message = "아이디를 입력하세요.")
    private String loginId;
    @NotBlank(message = "비밀번호를 입력하세요.")
    @Size(min = 4)
    private String password;
    @NotBlank
    private String checkPassword;
}

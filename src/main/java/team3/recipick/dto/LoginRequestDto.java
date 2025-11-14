package team3.recipick.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {

    @NotBlank
    private String loginId;
    @NotBlank
    private String password;
}

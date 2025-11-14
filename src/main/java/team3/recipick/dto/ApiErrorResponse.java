package team3.recipick.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @AllArgsConstructor(staticName = "of")
public class ApiErrorResponse {

    private String status;
    private String message;
}

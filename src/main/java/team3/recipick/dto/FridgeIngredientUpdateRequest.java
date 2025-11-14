package team3.recipick.dto;

import lombok.Data;

@Data
public class FridgeIngredientUpdateRequest {

    private Long fridgeIngredientId; //fridgeIngredient.id (필수)
    private Long count;
    private String memo;
}

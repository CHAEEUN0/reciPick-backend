package team3.recipick.dto;

import lombok.Data;

@Data
public class FridgeIngredientRequest {

    private Long ingredientId;
    private Long count;
    private String memo;
}

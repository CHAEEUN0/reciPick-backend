package team3.recipick.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import team3.recipick.domain.BasketIngredient;
import team3.recipick.domain.Ingredient;

@Data
@AllArgsConstructor(staticName = "of")
public class BasketIngredientResponse {
    private Long basketIngredientId; //BasketIngredient id
    private Long ingredientId; // Ingredient.id (외래키)
    private String name; //Ingredient name
    private boolean inFridge;

    public static BasketIngredientResponse from(BasketIngredient ingredient, boolean inFridge) {
        return BasketIngredientResponse.of(
                ingredient.getId(),
                ingredient.getIngredient().getId(),
                ingredient.getIngredient().getName(),
                inFridge);
    }
}
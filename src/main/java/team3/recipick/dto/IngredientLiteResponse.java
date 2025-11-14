package team3.recipick.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team3.recipick.domain.Ingredient;

@Getter
@AllArgsConstructor(staticName = "of")
public class IngredientLiteResponse {
    private Long id;
    private String name;

    public static IngredientLiteResponse from(Ingredient i) {
        return of(i.getId(), i.getName());
    }
}
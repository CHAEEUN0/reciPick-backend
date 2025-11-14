package team3.recipick.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team3.recipick.domain.History;
import team3.recipick.domain.Ingredient;
import team3.recipick.domain.Recipe;

import java.util.List;

@Getter
@AllArgsConstructor(staticName = "of")
public class RecipeResponseDto {
    private Long recipeId;
    private String recipeName;
    //편의로 String
    private List<String> ingredientsNames;

    public static RecipeResponseDto from(Recipe recipe){
        List<String> ingredientsName = recipe.getIngredients().stream().map(Ingredient::getName).toList();
        return RecipeResponseDto.of(recipe.getId(), recipe.getName(), ingredientsName);
    }


}

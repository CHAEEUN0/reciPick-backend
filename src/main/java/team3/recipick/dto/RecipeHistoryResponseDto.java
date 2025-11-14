package team3.recipick.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team3.recipick.domain.HistoryRecipe;
import team3.recipick.domain.Ingredient;
import team3.recipick.domain.Recipe;

import java.util.List;

@Getter
@AllArgsConstructor(staticName = "of")
public class RecipeHistoryResponseDto {
    private Long historyRecipeId;
    private Long recipeId;
    private String recipeName;
    //편의로 String
    private List<String> ingredientsNames;

    public static RecipeHistoryResponseDto from(HistoryRecipe historyRecipe){
        List<String> ingredientsName = historyRecipe.getRecipe().getIngredients().stream().map(Ingredient::getName).toList();
        return RecipeHistoryResponseDto.of(historyRecipe.getId(), historyRecipe.getRecipe().getId(), historyRecipe.getRecipe().getName(), ingredientsName);
    }


}

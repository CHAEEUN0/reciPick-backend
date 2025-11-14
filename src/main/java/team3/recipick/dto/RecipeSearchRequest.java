package team3.recipick.dto;

import lombok.Data;

import java.util.List;

@Data
public class RecipeSearchRequest {
    private List<Long> ingredientIds;
}

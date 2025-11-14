package team3.recipick.dto;


import lombok.Data;

import java.util.List;

@Data
public class RecipeInfo {
    private final String title;
    private final String ingredients;
    private final List<String> steps;
    private final String imageUrl;

    public RecipeInfo(String title, String ingredients, List<String> steps, String imageUrl) {
        this.title = title;
        this.ingredients = ingredients;
        this.steps = steps;
        this.imageUrl = imageUrl;
    }

    public String getTitle() { return title; }
    public String getIngredients() { return ingredients; }
    public List<String> getSteps() { return steps; }
    public String getImageUrl() { return imageUrl; }
}

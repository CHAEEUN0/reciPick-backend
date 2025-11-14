// src/main/java/team3/recipick/controller/IngredientController.java
package team3.recipick.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import team3.recipick.dto.IngredientLiteResponse;
import team3.recipick.service.IngredientService;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController {
    private final IngredientService ingredientService;

    // /api/ingredients/autocomplete?q=계란&size=10
    @GetMapping("/autocomplete")
    public List<IngredientLiteResponse> autocomplete(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ingredientService.autocomplete(q, size);
    }
}

package team3.recipick.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team3.recipick.domain.FridgeIngredient;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter @AllArgsConstructor(staticName = "of")
public class FridgeIngredientResponse {

    private Long fridgeIngredientId; // FridgeIngredient.id
    private Long ingredientId; // Ingredient.id (외래키)
    private String ingredientName;
    private Long count;

    //보관기간
    private String memo;
    private Long storagePeriod;

    public static FridgeIngredientResponse from(FridgeIngredient fridgeIngredient) {
        long storagePeriod = ChronoUnit.DAYS.between(fridgeIngredient.getCreatedAt().toLocalDate(), LocalDate.now()) + 1;

        return FridgeIngredientResponse.of(fridgeIngredient.getId(), fridgeIngredient.getIngredient().getId(), fridgeIngredient.getIngredient().getName(), fridgeIngredient.getCount(), fridgeIngredient.getMemo(), storagePeriod );
    }
}
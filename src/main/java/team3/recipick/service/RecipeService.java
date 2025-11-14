package team3.recipick.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team3.recipick.domain.*;
import team3.recipick.dto.RecipeHistoryResponseDto;
import team3.recipick.dto.RecipeResponseDto;
import team3.recipick.repository.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final HistoryRepository historyRepository;
    private final BasketRepository basketRepository;
    private final HistoryRecipeRepository historyRecipeRepository;
    private final BasketIngredientRepository basketIngredientRepository;

    @Transactional
    public Page<RecipeResponseDto> searchRecipes(Long memberId, Pageable pageable) {
        Basket basket = basketRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("장바구니를 찾을 수 없습니다."));

        List<Long> ingredientIds = basket.getIngredients()
                .stream()
                .map(BasketIngredient::getIngredient)
                .map(Ingredient::getId)
                .toList();

        // 이땐 장바구니 비우기 x
        if (ingredientIds.isEmpty()) {
            return Page.empty(pageable);
        }

        Page<Recipe> recipes = recipeRepository.findAll(ingredientIds, pageable);
        Page<RecipeResponseDto> result = recipes.map(RecipeResponseDto::from);

        // 장바구니 비우기
        basketIngredientRepository.deleteByBasket(basket);

        return result;
    }



    @Transactional
    public void addBookmark(Long recipeId, Long memberId) {
        History history = historyRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("히스토리가 존재하지 않습니다."));
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("레시피가 존재하지 않습니다."));
        history.getHistoryRecipes().add(new HistoryRecipe(history, recipe));
    }

    @Transactional(readOnly = true)
    public Page<RecipeHistoryResponseDto> getHistory(Long memberId, Pageable pageable) {
        return historyRecipeRepository
                .findAllWithRecipeAndIngredientsByHistoryId(memberId, pageable)
                .map(RecipeHistoryResponseDto::from);
    }

    @Transactional
    public Page<RecipeHistoryResponseDto> removeBookMark(Long historyRecipeId, Long memberId, Pageable pageable) {
        History history = historyRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("히스토리가 존재하지 않습니다."));

        HistoryRecipe historyRecipe = historyRecipeRepository.findByIdAndHistory(historyRecipeId, history)
                .orElseThrow(() -> new RuntimeException("히스토리에 해당 레시피가 존재하지 않습니다."));
        history.deleteHistoryRecipes(historyRecipe);

        return historyRecipeRepository.findAllByHistoryOrderByCreatedAt(history, pageable)
                .map(RecipeHistoryResponseDto::from);
    }
}

package team3.recipick.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team3.recipick.dto.RecipeInfo;
import team3.recipick.dto.RecipeHistoryResponseDto;
import team3.recipick.dto.RecipeResponseDto;
import team3.recipick.jwt.MemberDetails;
import team3.recipick.service.RecipeScrapeService;
import team3.recipick.service.RecipeService;

@RestController
@RequestMapping("/api/recipe")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeScrapeService recipeScrapeService;

    // 레시피 검색(레시피 id 반환) -> 이때 장바구니 초기화
    @GetMapping
    public Page<RecipeResponseDto> searchRecipes(
            @AuthenticationPrincipal MemberDetails member,
            Pageable pageable
    ) {
        return recipeService.searchRecipes(member.getId(), pageable);
    }


    // 히스토리 북마크 목록 조회
    @GetMapping("/history")
    public Page<RecipeHistoryResponseDto> getHistory(
            @AuthenticationPrincipal MemberDetails member,
            Pageable pageable) {
        return recipeService.getHistory(member.getId(), pageable);
    }


    // 히스토리에 북마크 추가
    @PostMapping("/history")
    public void addBookmark(@RequestParam Long recipeId,
                            @AuthenticationPrincipal MemberDetails member){
        recipeService.addBookmark(recipeId, member.getId());
    }

    // 검색 내역 지우기(히스토리에서 삭제)
    @DeleteMapping("/history")
    public Page<RecipeHistoryResponseDto> removeBookmark(
            @RequestParam Long historyRecipeId,
            @AuthenticationPrincipal MemberDetails member,
            Pageable pageable) {
        return recipeService.removeBookMark(historyRecipeId, member.getId(), pageable);
    }

    //레시피 상세정보(조리과정)
    @GetMapping("/detail{recipeId}")
    public RecipeInfo getFood(@PathVariable Long recipeId,
                              @AuthenticationPrincipal MemberDetails member) {

        return recipeScrapeService.getFoodInfo(recipeId);
    }

}

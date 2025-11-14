package team3.recipick.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team3.recipick.dto.BasketIngredientResponse;
import team3.recipick.jwt.MemberDetails;
import team3.recipick.service.BasketService;


@RestController
@RequestMapping("/api/basket/ingredients")
@RequiredArgsConstructor
public class BasketController {

    private final BasketService basketService;

    // 장바구니 전체 조회
    @GetMapping
    public Page<BasketIngredientResponse> getBasket(
            @AuthenticationPrincipal MemberDetails member,
            Pageable pageable) {
        return basketService.getIngredients(member.getId(), pageable);
    }

    // 장바구니 재료 추가
    @PostMapping
    public Page<BasketIngredientResponse> addIngredient(
            @RequestParam Long ingredientId,
            @AuthenticationPrincipal MemberDetails member,
            Pageable pageable) {
        return basketService.addIngredient(ingredientId, member.getId(), pageable);
    }

    // 장바구니 재료 삭제
    @DeleteMapping("/{basketIngredientId}")
    public Page<BasketIngredientResponse> deleteIngredient(
            @PathVariable Long basketIngredientId,
            @AuthenticationPrincipal MemberDetails member,
            Pageable pageable) {
        return basketService.removeIngredient(basketIngredientId, member.getId(), pageable);
    }

    // 장바구니 초기화
    @DeleteMapping("/all")
    public Page<BasketIngredientResponse> clearBasket(
            @AuthenticationPrincipal MemberDetails member,
            Pageable pageable) {
        return basketService.deleteAllIngredients(member.getId(), pageable);
    }
}

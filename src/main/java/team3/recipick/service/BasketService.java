package team3.recipick.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team3.recipick.domain.Basket;
import team3.recipick.domain.BasketIngredient;
import team3.recipick.domain.Ingredient;
import team3.recipick.dto.BasketIngredientResponse;
import team3.recipick.repository.BasketIngredientRepository;
import team3.recipick.repository.BasketRepository;
import team3.recipick.repository.FridgeIngredientRepository;
import team3.recipick.repository.IngredientRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BasketService {

    private final BasketRepository basketRepository;
    private final IngredientRepository ingredientRepository;
    private final BasketIngredientRepository basketIngredientRepository;
    private final FridgeIngredientRepository fridgeIngredientRepository;

    @Transactional(readOnly = true)
    public Page<BasketIngredientResponse> getIngredients(Long memberId, Pageable pageable) {
        Basket basket = basketRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("장바구니를 찾을 수 없습니다."));

        List<Long> fridgeIngredientIds = fridgeIngredientRepository.findFridgeIngredientIdsByMemberId(memberId);

        return basketIngredientRepository.findByBasketOrderByCreatedAt(basket, pageable)
                .map(bi -> BasketIngredientResponse.from(
                        bi,
                        fridgeIngredientIds.contains(bi.getIngredient().getId())));
    }


    @Transactional
    public Page<BasketIngredientResponse> addIngredient(Long ingredientId, Long memberId, Pageable pageable) {
        Basket basket = basketRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("장바구니를 찾을 수 없습니다."));

        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 재료입니다."));

        if (basketIngredientRepository.existsByBasketAndIngredient(basket, ingredient)) {
            throw new RuntimeException("이미 장바구니에 존재하는 재료입니다.");
        }

        BasketIngredient basketIngredient = new BasketIngredient(basket, ingredient);
        basket.addIngredient(basketIngredient);

        List<Long> fridgeIngredientIds = fridgeIngredientRepository.findFridgeIngredientIdsByMemberId(memberId);

        return basketIngredientRepository.findByBasketOrderByCreatedAt(basket, pageable)
                .map(bi -> BasketIngredientResponse.from(
                        bi, fridgeIngredientIds.contains(bi.getIngredient().getId())));
    }

    @Transactional
    public Page<BasketIngredientResponse> removeIngredient(Long id, Long memberId, Pageable pageable) {
        Basket basket = basketRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("장바구니를 찾을 수 없습니다."));

        BasketIngredient basketIngredient = basketIngredientRepository.findByIdAndBasket(id, basket)
                .orElseThrow(() -> new RuntimeException("장바구니에 존재하지 않는 재료입니다."));

        basket.removeIngredient(basketIngredient);

        List<Long> fridgeIngredientIds = fridgeIngredientRepository.findFridgeIngredientIdsByMemberId(memberId);

        return basketIngredientRepository.findByBasketOrderByCreatedAt(basket, pageable)
                .map(bi -> BasketIngredientResponse.from(
                        bi, fridgeIngredientIds.contains(bi.getIngredient().getId())));
    }

    @Transactional
    public Page<BasketIngredientResponse> deleteAllIngredients(Long memberId, Pageable pageable) {
        Basket basket = basketRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("장바구니를 찾을 수 없습니다."));

        basket.deleteAllIngredients();

        List<Long> fridgeIngredientIds = fridgeIngredientRepository.findFridgeIngredientIdsByMemberId(memberId);


        return basketIngredientRepository.findByBasketOrderByCreatedAt(basket, pageable)
                .map(bi -> BasketIngredientResponse.from(
                        bi,
                        fridgeIngredientIds.contains(bi.getIngredient().getId())));
    }
}


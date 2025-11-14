package team3.recipick.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import team3.recipick.domain.Basket;
import team3.recipick.domain.BasketIngredient;
import team3.recipick.domain.Ingredient;

import java.util.List;
import java.util.Optional;

public interface BasketIngredientRepository extends JpaRepository<BasketIngredient, Long> {

    Optional<BasketIngredient> findByIdAndBasket(Long id, Basket basket);

    Page<BasketIngredient> findByBasketOrderByCreatedAt(Basket basket, Pageable pageable);

    boolean existsByBasketAndIngredient(Basket basket, Ingredient ingredient);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from BasketIngredient bi where bi.basket = :basket")
    int deleteByBasket(Basket basket);


}

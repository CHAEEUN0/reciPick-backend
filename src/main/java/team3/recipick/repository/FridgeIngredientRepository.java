package team3.recipick.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team3.recipick.domain.Fridge;
import team3.recipick.domain.FridgeIngredient;

import java.util.List;
import java.util.Optional;

public interface FridgeIngredientRepository extends JpaRepository<FridgeIngredient, Long> {

    Optional<FridgeIngredient> findByIdAndFridge(Long id, Fridge fridge);

    Page<FridgeIngredient> findAllByFridgeOrderByCreatedAt(Fridge fridge, Pageable pageable);

    @Query("select fi.ingredient.id from FridgeIngredient fi where fi.fridge.member.id = :memberId")
    List<Long> findFridgeIngredientIdsByMemberId(@Param("memberId") Long memberId);


}

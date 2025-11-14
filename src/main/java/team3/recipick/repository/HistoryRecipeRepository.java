package team3.recipick.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team3.recipick.domain.History;
import team3.recipick.domain.HistoryRecipe;
import team3.recipick.domain.Recipe;

import java.util.List;
import java.util.Optional;

public interface HistoryRecipeRepository extends JpaRepository<HistoryRecipe, Long> {


    Page<HistoryRecipe> findAllByHistoryOrderByCreatedAt(History history, Pageable pageable);

    Optional<HistoryRecipe> findByIdAndHistory(Long id, History history);

    @Query("""
        select distinct hr
        from HistoryRecipe hr
        join fetch hr.recipe r
        left join fetch r.ingredients
        where hr.history.id = :historyId
        order by hr.createdAt
        """)
    Page<HistoryRecipe> findAllWithRecipeAndIngredientsByHistoryId(Long historyId, Pageable pageable);


}

package team3.recipick.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team3.recipick.domain.Recipe;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query("""
        select r 
        from Recipe r join r.ingredients i 
        where i.id in :ingredientIds group by r.id 
        having count(distinct i.id) = :#{#ingredientIds.size()} """)
    Page<Recipe> findAll(@Param("ingredientIds") List<Long> ingredientIds, Pageable pageable);

}

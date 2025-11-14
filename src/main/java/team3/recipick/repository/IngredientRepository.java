package team3.recipick.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team3.recipick.domain.Ingredient;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    Optional<Ingredient> findByName(String name);

    Optional<Ingredient> findByNameIgnoreCase(String name);

    List<Ingredient> findByNameContainingIgnoreCaseOrderByNameAsc(String name, Pageable pageable);

    @Query("""
    SELECT i FROM Ingredient i
    WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :q, '%'))
    ORDER BY 
      CASE 
        WHEN LOWER(i.name) = LOWER(:q) THEN 0
        WHEN LOWER(i.name) LIKE LOWER(CONCAT(:q, '%')) THEN 1
        ELSE 2
      END,
      i.name ASC
""")
    List<Ingredient> autocomplete(@Param("q") String q, Pageable pageable);

}

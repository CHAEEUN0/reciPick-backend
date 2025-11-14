package team3.recipick.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
        name = "basket_ingredient",
        uniqueConstraints = @UniqueConstraint(columnNames = {"basket_id", "ingredient_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BasketIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basket_id",  nullable = false)
    private Basket basket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @CreatedDate
    private LocalDateTime createdAt;

    public BasketIngredient(Basket basket, Ingredient ingredient) {
        this.basket = basket;
        this.ingredient = ingredient;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasketIngredient)) return false;
        BasketIngredient other = (BasketIngredient) o;
        return Objects.equals(basket != null ? basket.getId() : null,
                other.basket != null ? other.basket.getId() : null)
                && Objects.equals(ingredient != null ? ingredient.getId() : null,
                other.ingredient != null ? other.ingredient.getId() : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                basket != null ? basket.getId() : null,
                ingredient != null ? ingredient.getId() : null
        );
    }
}

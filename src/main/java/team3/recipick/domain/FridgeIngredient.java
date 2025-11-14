package team3.recipick.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FridgeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fridge_id")
    private Fridge fridge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    private Long count;

    private String memo;

    @CreatedDate
    private LocalDateTime createdAt;

    public FridgeIngredient(Fridge fridge, Ingredient ingredient, Long count, String memo) {
        this.fridge = fridge;
        this.ingredient = ingredient;
        this.count = count;
        this.memo = memo;
    }

    public void update(Long count, String memo){
        if (count != null) this.count = count;
        if (memo != null) this.memo = memo;
    }
}

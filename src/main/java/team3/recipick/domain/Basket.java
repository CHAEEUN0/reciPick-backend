package team3.recipick.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Basket {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "basket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BasketIngredient> ingredients = new ArrayList<>();

    public Basket(Member member) {
        this.member = member;
    }


    public void addIngredient(BasketIngredient ingredient){
        if (!this.getIngredients().contains(ingredient)){
            this.getIngredients().add(ingredient);
        }
    }

    public void removeIngredient(BasketIngredient ingredient){
        this.getIngredients().remove(ingredient);
    }

    public void deleteAllIngredients(){
        this.getIngredients().clear();
    }
}

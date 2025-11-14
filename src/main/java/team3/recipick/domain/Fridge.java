package team3.recipick.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Fridge {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "fridge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FridgeIngredient> fridgeIngredients = new ArrayList<>();

    public Fridge(Member member) {
        this.member = member;
    }

    public void addFridgeIngredient(FridgeIngredient ingredient){
       this.getFridgeIngredients().add(ingredient);
    }



    public void deleteFridgeIngredient(FridgeIngredient ingredient){
        this.getFridgeIngredients().remove(ingredient);
    }

}

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
public class History {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "history", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoryRecipe> historyRecipes = new ArrayList<>();

    public History(Member member) {
        this.member = member;
    }


    public void addHistoryRecipes(HistoryRecipe historyRecipe){
        this.getHistoryRecipes().add(historyRecipe);
    }

    public void deleteHistoryRecipes(HistoryRecipe historyRecipe){
        this.getHistoryRecipes().remove(historyRecipe);
    }

}

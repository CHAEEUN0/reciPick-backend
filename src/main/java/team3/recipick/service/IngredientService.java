package team3.recipick.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import team3.recipick.dto.IngredientLiteResponse;
import team3.recipick.repository.IngredientRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public List<IngredientLiteResponse> autocomplete(String q, int size) {
        if (q == null || q.isBlank()) return List.of();

        return ingredientRepository
                .autocomplete(q.trim(), PageRequest.of(0, size))
                .stream()
                .map(IngredientLiteResponse::from)
                .toList();
    }


}

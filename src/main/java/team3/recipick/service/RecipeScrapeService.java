package team3.recipick.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import team3.recipick.domain.Recipe;
import team3.recipick.dto.RecipeInfo;
import team3.recipick.repository.RecipeRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecipeScrapeService {

    private static final String RECIPE_URL = "https://www.10000recipe.com/recipe/";
    private final ObjectMapper mapper = new ObjectMapper();
    private final RecipeRepository recipeRepository;

    public RecipeScrapeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    /**
     * 10만개 레시피 사이트의 상세 페이지를 id로 조회하여
     * 재료/조리순서/대표이미지를 파싱해 반환한다.
     * - 제목(title)은 DB의 Recipe에서 가져온다.
     */
    public RecipeInfo getFoodInfo(Long recipeId) {
        try {
            // 0) 제목은 DB에서
            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 레시피Id 입니다. id=" + recipeId));
            String title = recipe.getName();

            // 1) 레시피 상세 페이지 접속
            String url = RECIPE_URL + recipeId;
            Document detailDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .timeout(10_000)
                    .get();

            // 2) JSON-LD 파싱
            Element jsonLdEl = detailDoc.selectFirst("script[type=application/ld+json]");
            if (jsonLdEl == null) {
                throw new IllegalStateException("JSON-LD 스키마를 찾지 못했습니다. (recipeId=" + recipeId + ")");
            }

            JsonNode root = mapper.readTree(jsonLdEl.data());
            JsonNode recipeNode = findRecipeNode(root);
            if (recipeNode == null) {
                throw new IllegalStateException("레시피 노드를 찾지 못했습니다. (recipeId=" + recipeId + ")");
            }

            // 3) 재료
            List<String> ingredients = new ArrayList<>();
            JsonNode ingNode = recipeNode.get("recipeIngredient");
            if (ingNode != null && ingNode.isArray()) {
                for (JsonNode ing : ingNode) {
                    String txt = ing.asText();
                    if (txt != null && !txt.isBlank()) {
                        ingredients.add(txt);
                    }
                }
            }
            String ingredientsJoined = String.join(",", ingredients);

            // 4) 조리 순서 (번호 붙이기)
            List<String> steps = new ArrayList<>();
            JsonNode instNode = recipeNode.get("recipeInstructions");
            if (instNode != null) {
                if (instNode.isArray()) {
                    int idx = 1;
                    for (JsonNode step : instNode) {
                        // HowToStep 객체이거나 단순 텍스트일 수 있음
                        String text = step.has("text") ? step.get("text").asText() : step.asText();
                        if (text != null && !text.isBlank()) {
                            steps.add(idx++ + ". " + text.trim());
                        }
                    }
                } else if (instNode.isTextual()) {
                    steps.add("1. " + instNode.asText());
                }
            }

            // 5) 이미지 URL (image 배열의 두 번째 요소 우선)
            String imageUrl = extractSecondImage(recipeNode.get("image"));

            return new RecipeInfo(title, ingredientsJoined, steps, imageUrl);

        } catch (Exception e) {
            throw new RuntimeException("크롤링 실패(recipeId=" + recipeId + "): " + e.getMessage(), e);
        }
    }

    /**
     * JSON-LD가 배열이면 @type=Recipe 노드를 찾아 반환.
     * 단일 객체면 그대로 반환.
     */
    private JsonNode findRecipeNode(JsonNode root) {
        if (root == null) return null;

        if (root.isArray()) {
            for (JsonNode n : root) {
                JsonNode type = n.get("@type");
                if (type != null) {
                    if (type.isArray()) {
                        if (containsText(type, "Recipe")) return n;
                    } else if ("Recipe".equalsIgnoreCase(type.asText())) {
                        return n;
                    }
                }
            }
            // 못 찾으면 첫 요소 fallback
            return root.size() > 0 ? root.get(0) : null;
        }
        return root;
    }

    private boolean containsText(JsonNode arrayNode, String target) {
        if (arrayNode == null || !arrayNode.isArray()) return false;
        for (JsonNode x : arrayNode) {
            if (target.equalsIgnoreCase(x.asText())) return true;
        }
        return false;
    }

    /**
     * image 필드는 다음 중 하나일 수 있음:
     * - 배열: ["..._f.jpg", "... .jpg", ...]  → 크기가 2 이상이면 index 1 사용, 아니면 index 0
     * - 문자열: "https://..."
     * - 객체: { "url": "https://..." }
     */
    private String extractSecondImage(JsonNode imageNode) {
        if (imageNode == null) return null;

        if (imageNode.isArray()) {
            if (imageNode.size() >= 2) {
                return safeText(imageNode.get(1));
            } else if (imageNode.size() == 1) {
                return safeText(imageNode.get(0));
            } else {
                return null;
            }
        } else if (imageNode.isTextual()) {
            return imageNode.asText();
        } else if (imageNode.isObject()) {
            JsonNode urlNode = imageNode.get("url");
            return urlNode != null ? urlNode.asText() : null;
        }
        return null;
    }

    private String safeText(JsonNode node) {
        return node == null ? null : node.asText();
    }
}

package team3.recipick.config;

import com.opencsv.CSVReader;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import team3.recipick.domain.Ingredient;
import team3.recipick.domain.Recipe;
import team3.recipick.repository.IngredientRepository;
import team3.recipick.repository.RecipeRepository;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
//@Component
@RequiredArgsConstructor
public class CsvToDbLoader {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    @PersistenceContext
    private EntityManager em;

    // ========= Precompiled Patterns (속도 ↑) =========
    private static final Pattern P_CNTRL      = Pattern.compile("\\p{Cntrl}+");
    private static final Pattern P_SPACE      = Pattern.compile("\\s+");
    private static final Pattern P_HAN        = Pattern.compile("\\p{IsHan}");
    private static final Pattern P_PUNCT_L    = Pattern.compile("^[\\p{Punct}]+");
    private static final Pattern P_PUNCT_R    = Pattern.compile("[\\p{Punct}]+$");
    private static final Pattern P_RANGE      = Pattern.compile("(?<![A-Za-z가-힣])\\d+(?:\\.\\d+)?\\s*(?:~|〜|\\-|~)\\s*\\d+(?:\\.\\d+)?[A-Za-z%가-힣]*(?![A-Za-z가-힣])");
    private static final Pattern P_FRACTION   = Pattern.compile("\\d+\\/\\d+[a-zA-Z가-힣%]*");
    private static final Pattern P_NUMUNIT    = Pattern.compile("\\d+(?:\\.\\d+)?[a-zA-Z가-힣%]*");
    private static final Pattern P_UNIT_TAIL  = Pattern.compile("(?i)(tbs|tbsp|tsp|ts|kg|ml|cup|ea|cm|cc|msg|모|알|포기|움큼|톨|큐브|주먹|컷|공기|봉지|단|줄|인분|조각|통|박스|머그컵|토막|잔|수저|여장|국공기|국그릇|구)$");
    private static final Pattern P_UNIT_CNT   = Pattern.compile("(반|한|두|세|네)(모|알|포기|움큼|주먹|컷|공기|봉지|단|줄|인분|조각|통|박스|잔|수저|여장|국공기|국그릇|구)$");
    private static final Pattern P_VERB       = Pattern.compile("(데친|다진|갈은|갈아서|갈아|썬|자른|말린|삶은|볶은|구운|찐|절인|절여)(것|거)?");

    // 불용어/노이즈
    private static final Pattern P_STOPWORDS  = Pattern.compile(
            "(원하는만큼|얹고싶은만큼|는둥만둥|마음껏|자유롭게|양껏|자세한재료는본문내용에서|자작히|자박할정도|작당히|째끔|톡톡톡|탈탈"
                    + "|기호|기호따라|기호대로|기호껏|기호에따라|기호에맞게|필요시|확인해보실수있습니다|취향에따라|넉넉한"
                    + "|크게|작게|씻은거|자른|취향것"
                    + "|포기|움큼|톨|큐브|주먹|노랑|공기|컷|톡톡"
                    + "|곱게뽑은|반모|한모|등분|머그컵|가득|갈아서|갈은|갈아"
                    + "|양념된거|그릇|간것|꼬집|다진거|다진것|못난이|면포|썬것|손질후"
                    + "|넉넉하게|넉넉히|듬뿍|가닥"
                    + "|다진|데친|작은거|작은것|작은사이즈|작은|많이|매운|먹을만큼|먹고싶은만큼"
                    + "|간맞추기|중간사이즈|중간크기|중사이즈|중자|중크기|중간"
                    + "|삶은것|삶은|적당량|소량|취향껏|알맞게|약간|조금|적당히"
                    + "|큰거|큰것|큰사이즈"
                    + "|종이컵|가루분|가루류|껍질깐것|껍질벗긴것"
                    + "|한꼬집|두꼬집|세꼬집|네꼬집"
                    + "|HACCP인증제품|HACCP인증|(?i)HACCP|(?i)haccp"
                    + "|▶재료|▶|※|☆|♥|α|×|℃물|냉장보관|냉동보관|가위|가위냉장보관|저울|가정용|제빙기"
                    + "|큰술|작은술"
                    + ")"
    );

    private static final Pattern P_DASHES     = Pattern.compile("[%~‐‑–—-]");
    private static final Pattern P_SYMBOLS    = Pattern.compile("[,;:·•/(){}\\[\\]<>]");
    private static final Pattern P_EXTRA_SYMBOLS = Pattern.compile("[\\?★↑±♧ⓐ]");
    private static final Pattern P_BRACKETS_ANGLE = Pattern.compile("【.*?】");
    private static final Pattern P_DUPJOIN    = Pattern.compile("^(.{1,20})\\1$");
    private static final Pattern P_ONLYSYM    = Pattern.compile("^[.+~\\-%\\-]+$");
    private static final Pattern P_BLACKLIST  = Pattern.compile("(?i)^(개|컵|큰술|작은술|티스푼|스푼|숟가락|숟갈|마리|근|봉|팩|캔|장|알|쪽|줌|송이|줄기|가닥|모|통|조각|인분|박스|머그컵|토막|한알|잔|포기|움큼|톨|큐브|주먹|공기|컷|봉지|단|줄|수저|국자|여장|국공기|국그릇|구|대|중|소|c|cc|cm|cup|ea|g|kg|l|ml|msg|t|tbs|ts|tsp|%)$");
    private static final Pattern P_UNICODE_FRAC = Pattern.compile("[½¼]");

    @Transactional
    public void loadCsv(String path) throws Exception {
        int saved = 0, readRows = 0;

        Map<String, Ingredient> ingredientCache = new HashMap<>();
        List<Ingredient> newIngredients = new ArrayList<>();
        List<Recipe> recipeBuffer = new ArrayList<>();
        final int BATCH = 5000;

        try (CSVReader reader = new CSVReader(
                new InputStreamReader(new FileInputStream(path), Charset.forName("MS949")))) {

            String[] line;
            reader.readNext();

            while ((line = reader.readNext()) != null) {
                readRows++;
                if (line.length <= 13) continue;

                Long recipeId   = tryParseLong(line[0]); // RCP_SNO
                String recipeNm = safe(line[2]);         // CKG_NM
                String material = safe(line[13]);        // CKG_MTRL_CN
                if (recipeId == null || recipeNm.isBlank() || material.isBlank()) continue;

                // 0) [] / () / 【】 안의 내용 통째 제거
                String removedBrackets = material
                        .replaceAll("\\[.*?\\]", "")
                        .replaceAll("\\(.*?\\)", "");
                removedBrackets = P_BRACKETS_ANGLE.matcher(removedBrackets).replaceAll("");

                // 1) 1차 구분자 정규화
                String normalized = P_CNTRL.matcher(removedBrackets).replaceAll("|")
                        .replace('·', '|')
                        .replace('•', '|')
                        .replace('/', '|')
                        .replace(',', '|');

                // 2) split & 정제 (or/또는/+ & 모두 분리 + 후춧가루/후추 접두 복합어 분리)
                Set<String> ingredientNames = new LinkedHashSet<>();
                Arrays.stream(normalized.split("\\|+"))
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .flatMap(this::splitSecondary)          // or / 또는 / + / &
                        .flatMap(this::expandPepperPrefixes)    // 후춧가루XXX / 후추XXX -> 두 토큰
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .map(this::normalizeAndCleanIngredient) // 정제
                        .map(this::normalizeSynonyms)           // 동의어/오타 통일
                        .filter(s -> !s.isBlank())
                        .forEach(ingredientNames::add);

                if (ingredientNames.isEmpty()) continue;

                // 레시피 생성(재료는 캐시에서 참조)
                Recipe recipe = Recipe.builder()
                        .id(recipeId)
                        .name(recipeNm)
                        .build();

                for (String ingName : ingredientNames) {
                    String key = keyOf(ingName);
                    Ingredient ing = ingredientCache.get(key);
                    if (ing == null) {
                        ing = Ingredient.builder().name(ingName).build();
                        ingredientCache.put(key, ing);
                        newIngredients.add(ing);
                    }
                    recipe.getIngredients().add(ing);
                }

                recipeBuffer.add(recipe);
                saved++;

                if (saved % BATCH == 0) {
                    if (!newIngredients.isEmpty()) {
                        ingredientRepository.saveAll(newIngredients);
                        newIngredients.clear();
                    }
                    if (!recipeBuffer.isEmpty()) {
                        recipeRepository.saveAll(recipeBuffer);
                        recipeBuffer.clear();
                    }
                    em.flush();
                    em.clear();
                    log.info("{}건 저장 완료 (읽은 행: {})", saved, readRows);
                }
            }
        }

        if (!newIngredients.isEmpty()) {
            ingredientRepository.saveAll(newIngredients);
            newIngredients.clear();
        }
        if (!recipeBuffer.isEmpty()) {
            recipeRepository.saveAll(recipeBuffer);
            recipeBuffer.clear();
        }
        em.flush();
        em.clear();

        log.info("최종 저장 건수: {}", saved);
    }

    // 캐시 키 표준화: NFKC + lowercase
    private String keyOf(String name) {
        if (name == null) return "";
        String nfkc = Normalizer.normalize(name, Normalizer.Form.NFKC);
        return nfkc.toLowerCase(Locale.ROOT);
    }

    private Long tryParseLong(String s) {
        try { return Long.valueOf(s.trim()); } catch (Exception e) { return null; }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    /** 2차 분리: 'or' (대소문자 무시), '또는', '+', '&' */
    private Stream<String> splitSecondary(String token) {
        String s = token
                .replaceAll("(?i)\\bor\\b", "|")
                .replace("또는", "|")
                .replace("+", "|")
                .replace("&", "|");
        return Arrays.stream(s.split("\\|+")).filter(t -> !t.isBlank());
    }

    /** 후춧가루XXX / 후추XXX → ["후춧가루","XXX"] or ["후추","XXX"] */
    private Stream<String> expandPepperPrefixes(String token) {
        if (token == null || token.isBlank()) return Stream.empty();
        String t = token.trim().replaceAll("\\s+", "");
        if (t.startsWith("후춧가루") && t.length() > "후춧가루".length()) {
            return Stream.of("후춧가루", t.substring("후춧가루".length()));
        }
        if (t.startsWith("후추") && t.length() > "후추".length()) {
            return Stream.of("후추", t.substring("후추".length()));
        }
        return Stream.of(token);
    }

    /** 동의어/오타 통일 */
    private String normalizeSynonyms(String m) {
        if (m.isBlank()) return m;

        // 케챱/깨진표기 → 케찹
        if (m.equals("케챱") || m.equals("케�y")) return "케찹";
        m = m.replaceAll("케챱", "케찹");

        // 체더치즈 → 체다치즈
        m = m.replaceAll("체더치즈", "체다치즈");

        // 인스턴드라이아이스 → 인스턴트드라이아이스
        m = m.replaceAll("인스턴드라이아이스", "인스턴트드라이아이스");

        // 설탕a/설탕b/설탕ⓐ → 설탕
        m = m.replaceAll("설탕(?:[aAbB]|ⓐ)", "설탕");

        // 채당근 → 당근
        m = m.replaceAll("채당근", "당근");

        // 청/홍고추, 청or홍고추 → 고추
        if (m.equals("청/홍고추") || m.equalsIgnoreCase("청or홍고추")) return "고추";
        if (m.equals("청고추") || m.equals("홍고추")) return "고추";

        return m;
    }

    /** 재료명 정규화 & 정제 */
    private String normalizeAndCleanIngredient(String raw) {
        if (raw == null) return "";
        String m = raw.trim();

        // 특수 예외: 다진마늘 보존
        if (m.equals("다진마늘")) return "다진마늘";

        // 내부 공백 제거
        m = P_SPACE.matcher(m).replaceAll("");

        // 한자 제거
        m = P_HAN.matcher(m).replaceAll("");

        // 선행/후행 특수문자 제거
        m = P_PUNCT_L.matcher(m).replaceAll("");
        m = P_PUNCT_R.matcher(m).replaceAll("");

        // 유니코드 분수(½, ¼) 제거
        m = P_UNICODE_FRAC.matcher(m).replaceAll("");

        // 수량 범위 제거
        m = P_RANGE.matcher(m).replaceAll("");

        // 숫자/분수/숫자+단위 제거
        m = P_FRACTION.matcher(m).replaceAll("");
        m = P_NUMUNIT.matcher(m).replaceAll("");

        // 단위 꼬리 제거
        m = P_UNIT_TAIL.matcher(m).replaceAll("");
        m = P_UNIT_CNT.matcher(m).replaceAll("");

        // 조리 동사 제거
        m = P_VERB.matcher(m).replaceAll("");

        // 불용어/노이즈 제거
        m = P_STOPWORDS.matcher(m).replaceAll("");

        // 특수 정규화: 간OO → OO
        m = m.replaceAll("^간(양파|오이|마늘|파|무|배|사과|배추)$", "$1");

        // 남은 특수기호 제거
        m = P_DASHES.matcher(m).replaceAll("");
        m = P_SYMBOLS.matcher(m).replaceAll("");
        m = P_EXTRA_SYMBOLS.matcher(m).replaceAll("");

        // ===== 접두/접미 통일 규칙 =====
        if (m.startsWith("대파") && m.length() > 2) m = "대파";        // 대파뒤 꼬리 제거
        if (m.startsWith("가래떡") && m.length() > 3) m = "가래떡";    // 가래떡뒤 꼬리 제거
        if (m.startsWith("간마늘")) m = "마늘";                        // 간마늘* → 마늘

        // 중복 결합 축약: "양파양파" → "양파"
        m = P_DUPJOIN.matcher(m).replaceAll("$1");

        // === 블랙리스트 드랍 ===
        if (m.isBlank() || P_ONLYSYM.matcher(m).matches()) return "";
        if (P_BLACKLIST.matcher(m).matches()) return "";

        // 마지막 선행/후행 특수문자 정리
        m = P_PUNCT_L.matcher(m).replaceAll("");
        m = P_PUNCT_R.matcher(m).replaceAll("");

        // ★ 예외 보존: A1스테이크소스
        if (m.equalsIgnoreCase("A1스테이크소스")) return "A1스테이크소스";

        return m.trim();
    }
}

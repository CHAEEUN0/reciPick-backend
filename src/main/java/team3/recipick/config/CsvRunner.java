package team3.recipick.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;

//Component
@RequiredArgsConstructor
public class CsvRunner implements CommandLineRunner {

    private final CsvToDbLoader csvToDbLoader;

    @Override
    public void run(String... args) throws Exception {
        csvToDbLoader.loadCsv("C:/Users/djw72/Downloads/TB_RECIPE_SEARCH-20231130.csv");
    }
}

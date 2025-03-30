package me.nasukhov.TukitaLearner;

import me.nasukhov.TukitaLearner.db.Collection;
import me.nasukhov.TukitaLearner.db.Connection;
import me.nasukhov.TukitaLearner.dictionary.DictionaryRepository;
import me.nasukhov.TukitaLearner.dictionary.ImportDictionary;
import me.nasukhov.TukitaLearner.study.GenerateQuestion;
import me.nasukhov.TukitaLearner.study.QuestionRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Stream;

@Component
public class Updater {
    private static final String MIGRATIONS_DIRECTORY = "src/main/resources/migrations";

    private final Connection db;
    private final DictionaryRepository dictionary;
    private final QuestionRepository questionRepository;

    public Updater(
            Connection db,
            DictionaryRepository dictionary,
            QuestionRepository questionRepository
    ) {
        this.db = db;
        this.dictionary = dictionary;
        this.questionRepository = questionRepository;
    }

    public void execute() throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(MIGRATIONS_DIRECTORY))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(this::runMigration);
        }
        generateData();
    }

    private void runMigration(Path migrationFile) {
        if (!db.tableExists("application_version")) {
            db.executeQuery(getFileContents(migrationFile));

            return;
        }

        String migrationVersion = migrationFile.getFileName().toString().replaceFirst("[.][^.]+$", "");

        Collection result = db.fetchByQuery("SELECT version FROM application_version LIMIT 1");
        result.next();
        String currentVersion = result.getCurrentEntryProp("version");
        result.free();

        if (migrationVersion.compareTo(currentVersion) <= 0) {
            return;
        }

        db.executeQuery(getFileContents(migrationFile));
        db.executeQuery("UPDATE application_version SET version=?", new HashMap<>() {{
            put(1, migrationVersion);
        }});
    }

    private void generateData() {
        if (!dictionary.isEmpty()) {
            System.out.println("Tables already contain generated data. Skipping");

            return;
        }
        new ImportDictionary(dictionary).run();

        new GenerateQuestion(dictionary, questionRepository).run();
    }

    private static String getFileContents(Path file) {
        try {
            return Files.readString(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

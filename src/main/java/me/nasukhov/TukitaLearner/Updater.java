package me.nasukhov.TukitaLearner;

import jakarta.persistence.EntityManager;
import me.nasukhov.TukitaLearner.dictionary.DictionaryRepository;
import me.nasukhov.TukitaLearner.dictionary.ImportDictionary;
import me.nasukhov.TukitaLearner.study.GenerateQuestion;
import me.nasukhov.TukitaLearner.study.QuestionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;


@Component
class Migrator {

    private final EntityManager db;


    public Migrator(EntityManager db) {
        this.db = db;
    }

    @Transactional
    public void runMigration(Path migrationFile) {
        if (!tableExists("application_version")) {
            try {
                db.createNativeQuery(getFileContents(migrationFile)).executeUpdate();
            } catch (Throwable e) {
                throw new RuntimeException("Migration " + migrationFile.toString() + " failed", e);
            }

            return;
        }

        String migrationVersion = migrationFile.getFileName().toString().replaceFirst("[.][^.]+$", "");

        var currentVersion = db.createNativeQuery("SELECT version FROM application_version LIMIT 1").getSingleResult().toString();
        if (migrationVersion.compareTo(currentVersion) <= 0) {
            return;
        }

        db.createNativeQuery(getFileContents(migrationFile)).executeUpdate();
        db.createNativeQuery("UPDATE application_version SET version = :newVersion")
                .setParameter("newVersion", migrationVersion)
                .executeUpdate();
    }

    private boolean tableExists(String tableName) {
        var result = db.createNativeQuery(String.format("SELECT EXISTS (SELECT * FROM information_schema.tables WHERE table_name = '%s')", tableName))
                .getSingleResult();

        return result.equals(true);
    }

    private static String getFileContents(Path file) {
        try {
            return Files.readString(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

@Component
public class Updater {
    private static final String MIGRATIONS_DIRECTORY = "src/main/resources/migrations";

    private final DictionaryRepository dictionary;
    private final QuestionRepository questionRepository;

    private final Migrator migrator;

    public Updater(
            Migrator migrator,
            DictionaryRepository dictionary,
            QuestionRepository questionRepository
    ) {
        this.migrator = migrator;
        this.dictionary = dictionary;
        this.questionRepository = questionRepository;
    }

    public void execute() throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(MIGRATIONS_DIRECTORY))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(migrator::runMigration);
        }
        generateData();
    }

    private void generateData() {
        if (dictionary.count() != 0) {
            System.out.println("Tables already contain generated data. Skipping");

            return;
        }
        new ImportDictionary(dictionary).run();

        new GenerateQuestion(dictionary, questionRepository).run();
    }
}

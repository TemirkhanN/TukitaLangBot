package me.nasukhov;

import me.nasukhov.DI.ServiceLocator;
import me.nasukhov.db.Collection;
import me.nasukhov.db.Connection;
import me.nasukhov.dictionary.DictionaryRepository;
import me.nasukhov.dictionary.ImportDictionary;
import me.nasukhov.study.GenerateQuestion;
import me.nasukhov.study.QuestionRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

// TODO this is more of first-time bootstrap rather than migrator
public class Updater {
    private static final String MIGRATIONS_DIRECTORY = "src/main/resources/migrations";

    private static final ServiceLocator serviceLocator = new ServiceLocator();

    public static void main(String[] args) {
        Path folder = Paths.get(MIGRATIONS_DIRECTORY);

        try {
            Files.walk(folder)
                    .filter(Files::isRegularFile)
                    .forEach(Updater::runMigration);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        generateData();
    }

    private static void runMigration(Path migrationFile) {
        Connection db = serviceLocator.locate(Connection.class);

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

    private static void generateData() {
        DictionaryRepository dictionary = serviceLocator.locate(DictionaryRepository.class);
        if (!dictionary.isEmpty()) {
            System.out.println("Tables already contain generated data. Skipping");

            return;
        }
        new ImportDictionary(dictionary).run();

        QuestionRepository questionRepository = serviceLocator.locate(QuestionRepository.class);
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

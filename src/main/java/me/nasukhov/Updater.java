package me.nasukhov;

import me.nasukhov.db.Connection;
import me.nasukhov.dictionary.DictionaryRepository;
import me.nasukhov.dictionary.ImportDictionary;
import me.nasukhov.study.GenerateQuestion;
import me.nasukhov.study.QuestionRepository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;

// TODO this is more of first-time bootstrap rather than migrator
public class Updater {
    public static void main(String[] args) {
        Connection db = Connection.getInstance();
        ResultSet result = db.fetchByQuery("SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'application_version')");

        try {
            boolean isAlreadyUpdated = result.next() && result.getBoolean(1);
            if (!isAlreadyUpdated) {
                db.executeQuery(getResourceContent("migrations/initial_schema.sql"));

                DictionaryRepository dictionary = new DictionaryRepository();
                new ImportDictionary(dictionary).run();

                QuestionRepository questionRepository = new QuestionRepository();
                new GenerateQuestion(dictionary, questionRepository).run();
            }
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String getResourceContent(String resource) {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        if (inputStream == null) {
            throw new RuntimeException("Resource not found");
        }

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        return bufferedReader.lines().collect(Collectors.joining("\n"));
    }
}

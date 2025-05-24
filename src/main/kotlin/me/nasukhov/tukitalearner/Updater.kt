package me.nasukhov.tukitalearner

import jakarta.persistence.EntityManager
import me.nasukhov.tukitalearner.dictionary.DictionaryRepository
import me.nasukhov.tukitalearner.dictionary.ImportDictionary
import me.nasukhov.tukitalearner.study.GenerateQuestion
import me.nasukhov.tukitalearner.study.QuestionRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Component
class Migrator(
    private val db: EntityManager,
) {
    @Transactional
    fun runMigration(migrationFile: Path) {
        if (!tableExists("application_version")) {
            db.createNativeQuery(getFileContents(migrationFile)).executeUpdate()

            return
        }

        val migrationVersion = migrationFile.fileName.toString().replaceFirst("[.][^.]+$".toRegex(), "")

        val currentVersion =
            db.createNativeQuery("SELECT version FROM application_version LIMIT 1").singleResult.toString()
        if (migrationVersion <= currentVersion) {
            return
        }

        db.createNativeQuery(getFileContents(migrationFile)).executeUpdate()
        db
            .createNativeQuery("UPDATE application_version SET version = :newVersion")
            .setParameter("newVersion", migrationVersion)
            .executeUpdate()
    }

    private fun tableExists(tableName: String?): Boolean {
        val result =
            db
                .createNativeQuery(
                    "SELECT EXISTS (SELECT * FROM information_schema.tables WHERE table_name = '$tableName')",
                ).singleResult

        return result == true
    }

    companion object {
        private fun getFileContents(file: Path): String = Files.readString(file)
    }
}

@Component
class Updater(
    private val migrator: Migrator,
    private val dictionary: DictionaryRepository,
    private val questionRepository: QuestionRepository,
) {
    fun execute() {
        Files.walk(Paths.get(MIGRATIONS_DIRECTORY)).use { paths ->
            paths
                .filter { path: Path -> Files.isRegularFile(path) }
                .forEach { migrationFile: Path -> migrator.runMigration(migrationFile) }
        }
        generateData()
    }

    private fun generateData() {
        if (dictionary.count() != 0L) {
            println("Tables already contain generated data. Skipping")

            return
        }
        ImportDictionary(dictionary).run()

        GenerateQuestion(dictionary, questionRepository).run()
    }

    companion object {
        private const val MIGRATIONS_DIRECTORY = "src/main/resources/migrations"
    }
}

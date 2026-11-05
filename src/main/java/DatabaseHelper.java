import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles creation and lifecycle management of the SQLite database.
 * <p>
 * This class opens a JDBC connection to a local SQLite file (created on first
 * use), creates the required tables if they do not yet exist, and exposes the
 * live {@link Connection}.
 */
public class DatabaseHelper {
    private static final String DEFAULT_PATH = "parla.db";

    private Connection conn;

    /**
     * Opens the SQLite database and ensures the schema exists.
     * <p>
     * The database file defaults to {@code parla.db} in the working directory
     * and can be overridden with the {@code PARLA_DB_PATH} environment variable.
     * The file is created automatically if it does not exist.
     *
     * @throws SQLException if the database cannot be opened or initialized
     */
    public void connect() throws SQLException {
        String path = System.getenv().getOrDefault("PARLA_DB_PATH", DEFAULT_PATH);

        conn = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        initializeSchema();
    }

    /**
     * Creates the required tables if they do not already exist.
     *
     * @throws SQLException if table creation fails
     */
    private void initializeSchema() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS words ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "language TEXT NOT NULL CHECK (language IN (" + languageCheckList() + ")), "
                    + "foreign_word TEXT NOT NULL, "
                    + "english TEXT NOT NULL, "
                    + "lookups INTEGER NOT NULL, "
                    + "added_date TEXT NOT NULL"
                    + ")");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS quizzes ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "date TEXT NOT NULL, "
                    + "score INTEGER NOT NULL, "
                    + "num_questions INTEGER NOT NULL"
                    + ")");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS quiz_words ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "quiz_id INTEGER NOT NULL REFERENCES quizzes(id), "
                    + "foreign_word TEXT NOT NULL, "
                    + "translation TEXT NOT NULL"
                    + ")");
        }
    }

    /**
     * Builds a quoted, comma-separated list of every {@link Language} constant name
     * for use in a SQL CHECK constraint.
     *
     * @return e.g. {@code 'ENGLISH', 'FRENCH', ...}
     */
    private static String languageCheckList() {
        StringBuilder sb = new StringBuilder();
        for (Language l : Language.values()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append('\'').append(l.name()).append('\'');
        }
        return sb.toString();
    }

    /**
     * Returns the active database connection.
     *
     * @return the open {@link Connection}
     */
    public Connection getConnection() {
        return conn;
    }

    /**
     * Closes the database connection if it is open.
     *
     * @throws SQLException if closing the connection fails
     */
    public void close() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}

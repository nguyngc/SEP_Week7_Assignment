import java.sql.*;

public class ResultService {

    private static String env(String key, String def) {
        String v = System.getenv(key);
        return (v == null || v.isBlank()) ? def : v.trim();
    }

    private static String requiredEnv(String key) {
        String v = System.getenv(key);
        if (v == null || v.isBlank()) {
            throw new IllegalStateException("Missing required environment variable: " + key);
        }
        return v.trim();
    }

    private static String dbName()     { return env("DB_NAME", "calc_data"); }
    private static String dbUser()     { return env("DB_USER", "root"); }
    private static String dbPassword() { return requiredEnv("DB_PASSWORD"); }
    private static String dbHost()     { return env("DB_HOST", "localhost"); }
    private static String dbPort()     { return env("DB_PORT", "3306"); } // default = host-mapped port for local dev

    private static String dbUrl() {
        // MariaDB driver accepts jdbc:mariadb:// ... (more correct than jdbc:mysql://)
        return "jdbc:mariadb://" + dbHost() + ":" + dbPort() + "/" + dbName()
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    static {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void saveResult(double n1, double n2, double sum, double product, double subtract, Double division) {
        String url = dbUrl();

        try (Connection conn = DriverManager.getConnection(url, dbUser(), dbPassword());
             Statement stmt = conn.createStatement()) {

            String createTable = """
          CREATE TABLE IF NOT EXISTS calc_results (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    number1 DOUBLE NOT NULL,
                    number2 DOUBLE NOT NULL,
                    sum_result DOUBLE NOT NULL,
                    product_result DOUBLE NOT NULL,
                    subtract_result DOUBLE NOT NULL,
                    division_result DOUBLE NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;
            stmt.executeUpdate(createTable);

            String insert ="""
                INSERT INTO calc_results
                (number1, number2, sum_result, product_result, subtract_result, division_result)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setDouble(1, n1);
                ps.setDouble(2, n2);
                ps.setDouble(3, sum);
                ps.setDouble(4, product);
                ps.setDouble(5, subtract);

                if (division == null) {
                    ps.setNull(6, java.sql.Types.DOUBLE);
                } else {
                    ps.setDouble(6, division);
                }

                ps.executeUpdate();
            }

            System.out.println("✅ Result saved successfully.");

        } catch (SQLException e) {
            System.err.println("❌ Failed to save result to DB: " + url);
            e.printStackTrace();
        }
    }
}
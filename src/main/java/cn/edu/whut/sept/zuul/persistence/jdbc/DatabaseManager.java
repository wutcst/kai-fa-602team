package cn.edu.whut.sept.zuul.persistence.jdbc;

import cn.edu.whut.sept.zuul.persistence.PersistenceException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * H2 数据库连接与初始化，供 JDBC 持久化实现共享。
 */
public class DatabaseManager
{
    private static final String DEFAULT_JDBC_URL =
            "jdbc:h2:file:./data/zuul;MODE=MySQL";

    private static DatabaseManager instance;

    private final JdbcDataSource dataSource;
    private volatile boolean initialized;

    public DatabaseManager()
    {
        this(DEFAULT_JDBC_URL);
    }

    public DatabaseManager(String jdbcUrl)
    {
        dataSource = new JdbcDataSource();
        dataSource.setURL(jdbcUrl);
        dataSource.setUser("sa");
        dataSource.setPassword("");
    }

    public static synchronized DatabaseManager getInstance()
    {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public static void resetInstance()
    {
        instance = null;
    }

    public Connection getConnection() throws PersistenceException
    {
        ensureInitialized();
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new PersistenceException("无法连接数据库", e);
        }
    }

    /**
     * 启动时预热连接，避免首次存档操作卡顿。
     */
    public void warmUp() throws PersistenceException
    {
        ensureInitialized();
        try (Connection conn = getConnection()) {
            conn.isValid(2);
        } catch (SQLException e) {
            throw new PersistenceException("数据库预热失败", e);
        }
    }

    public synchronized void ensureInitialized() throws PersistenceException
    {
        if (initialized) {
            return;
        }
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            runScript(stmt, "/db/schema.sql");
            runScript(stmt, "/db/seed-items.sql");
            initialized = true;
        } catch (SQLException | IOException e) {
            throw new PersistenceException("数据库初始化失败", e);
        }
    }

    private void runScript(Statement stmt, String resourcePath) throws IOException, SQLException
    {
        String sql = readResource(resourcePath);
        for (String statement : sql.split(";")) {
            String trimmed = statement.trim();
            if (!trimmed.isEmpty()) {
                stmt.execute(trimmed);
            }
        }
    }

    private String readResource(String resourcePath) throws IOException
    {
        try (InputStream in = DatabaseManager.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IOException("找不到资源: " + resourcePath);
            }
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        }
    }
}

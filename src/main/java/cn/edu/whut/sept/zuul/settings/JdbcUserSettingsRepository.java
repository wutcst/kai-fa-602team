<<<<<<< HEAD
package cn.edu.whut.sept.zuul.settings;

import cn.edu.whut.sept.zuul.persistence.PersistenceException;
import cn.edu.whut.sept.zuul.persistence.jdbc.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * 基于 JDBC 的用户设置存储实现。
 */
public class JdbcUserSettingsRepository implements UserSettingsRepository
{
    private final DatabaseManager databaseManager;

    public JdbcUserSettingsRepository(DatabaseManager databaseManager)
    {
        this.databaseManager = databaseManager;
    }

    @Override
    public void save(UserSettings settings) throws PersistenceException
    {
        databaseManager.ensureInitialized();
        String sql = "MERGE INTO user_settings "
                + "(settings_key, language, default_player_name, sound_enabled) "
                + "KEY(settings_key) VALUES (?, ?, ?, ?)";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, settings.getSettingsKey());
            ps.setString(2, settings.getLanguage());
            ps.setString(3, settings.getDefaultPlayerName());
            ps.setBoolean(4, settings.isSoundEnabled());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("保存用户设置失败", e);
        }
    }

    @Override
    public Optional<UserSettings> load(String settingsKey) throws PersistenceException
    {
        databaseManager.ensureInitialized();
        String sql = "SELECT settings_key, language, default_player_name, sound_enabled "
                + "FROM user_settings WHERE settings_key = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, settingsKey);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                UserSettings settings = new UserSettings();
                settings.setSettingsKey(rs.getString("settings_key"));
                settings.setLanguage(rs.getString("language"));
                settings.setDefaultPlayerName(rs.getString("default_player_name"));
                settings.setSoundEnabled(rs.getBoolean("sound_enabled"));
                return Optional.of(settings);
            }
        } catch (SQLException e) {
            throw new PersistenceException("读取用户设置失败", e);
        }
    }
}
=======
package cn.edu.whut.sept.zuul.settings;

import cn.edu.whut.sept.zuul.persistence.PersistenceException;
import cn.edu.whut.sept.zuul.persistence.jdbc.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * 基于 JDBC 的用户设置存储实现。
 */
public class JdbcUserSettingsRepository implements UserSettingsRepository
{
    private final DatabaseManager databaseManager;

    public JdbcUserSettingsRepository(DatabaseManager databaseManager)
    {
        this.databaseManager = databaseManager;
    }

    @Override
    public void save(UserSettings settings) throws PersistenceException
    {
        databaseManager.ensureInitialized();
        String sql = "MERGE INTO user_settings "
                + "(settings_key, language, default_player_name, sound_enabled) "
                + "KEY(settings_key) VALUES (?, ?, ?, ?)";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, settings.getSettingsKey());
            ps.setString(2, settings.getLanguage());
            ps.setString(3, settings.getDefaultPlayerName());
            ps.setBoolean(4, settings.isSoundEnabled());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("保存用户设置失败", e);
        }
    }

    @Override
    public Optional<UserSettings> load(String settingsKey) throws PersistenceException
    {
        databaseManager.ensureInitialized();
        String sql = "SELECT settings_key, language, default_player_name, sound_enabled "
                + "FROM user_settings WHERE settings_key = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, settingsKey);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                UserSettings settings = new UserSettings();
                settings.setSettingsKey(rs.getString("settings_key"));
                settings.setLanguage(rs.getString("language"));
                settings.setDefaultPlayerName(rs.getString("default_player_name"));
                settings.setSoundEnabled(rs.getBoolean("sound_enabled"));
                return Optional.of(settings);
            }
        } catch (SQLException e) {
            throw new PersistenceException("读取用户设置失败", e);
        }
    }
}
>>>>>>> 210fdf462ccacad1294a3b412c05a259a656f9cf

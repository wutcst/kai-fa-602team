<<<<<<< HEAD
package cn.edu.whut.sept.zuul;

import cn.edu.whut.sept.zuul.engine.GameEngine;
import cn.edu.whut.sept.zuul.model.World;
import cn.edu.whut.sept.zuul.persistence.PersistenceException;
import cn.edu.whut.sept.zuul.persistence.jdbc.DatabaseManager;
import cn.edu.whut.sept.zuul.persistence.jdbc.ItemCatalog;
import cn.edu.whut.sept.zuul.persistence.jdbc.JdbcGameStateRepository;
import cn.edu.whut.sept.zuul.settings.I18n;
import cn.edu.whut.sept.zuul.settings.JdbcUserSettingsRepository;
import cn.edu.whut.sept.zuul.settings.UserSettingsService;

/**
 * 应用启动装配：注入 JDBC 存档仓库与用户设置，供 UI 与控制台入口复用。
 */
public final class GameBootstrap
{
    private final DatabaseManager databaseManager;
    private final UserSettingsService userSettingsService;
    private final ItemCatalog itemCatalog;

    private GameBootstrap(DatabaseManager databaseManager)
    {
        this.databaseManager = databaseManager;
        this.itemCatalog = new ItemCatalog(databaseManager);
        this.userSettingsService = new UserSettingsService(
                new JdbcUserSettingsRepository(databaseManager));
    }

    public static GameBootstrap createDefault()
    {
        return new GameBootstrap(DatabaseManager.getInstance());
    }

    public GameEngine createGameEngine() throws PersistenceException
    {
        databaseManager.ensureInitialized();
        databaseManager.warmUp();
        itemCatalog.preloadAll();
        String playerName = userSettingsService.getDefaultPlayerName();
        return new GameEngine(
                new World(),
                playerName,
                new JdbcGameStateRepository(databaseManager),
                itemCatalog);
    }

    public UserSettingsService getUserSettingsService()
    {
        return userSettingsService;
    }

    public I18n createI18n() throws PersistenceException
    {
        return new I18n(userSettingsService.getLanguage());
    }
}
=======
package cn.edu.whut.sept.zuul;

import cn.edu.whut.sept.zuul.engine.GameEngine;
import cn.edu.whut.sept.zuul.model.World;
import cn.edu.whut.sept.zuul.persistence.PersistenceException;
import cn.edu.whut.sept.zuul.persistence.jdbc.DatabaseManager;
import cn.edu.whut.sept.zuul.persistence.jdbc.ItemCatalog;
import cn.edu.whut.sept.zuul.persistence.jdbc.JdbcGameStateRepository;
import cn.edu.whut.sept.zuul.settings.I18n;
import cn.edu.whut.sept.zuul.settings.JdbcUserSettingsRepository;
import cn.edu.whut.sept.zuul.settings.UserSettingsService;

/**
 * 应用启动装配：注入 JDBC 存档仓库与用户设置，供 UI 与控制台入口复用。
 */
public final class GameBootstrap
{
    private final DatabaseManager databaseManager;
    private final UserSettingsService userSettingsService;
    private final ItemCatalog itemCatalog;

    private GameBootstrap(DatabaseManager databaseManager)
    {
        this.databaseManager = databaseManager;
        this.itemCatalog = new ItemCatalog(databaseManager);
        this.userSettingsService = new UserSettingsService(
                new JdbcUserSettingsRepository(databaseManager));
    }

    public static GameBootstrap createDefault()
    {
        return new GameBootstrap(DatabaseManager.getInstance());
    }

    public GameEngine createGameEngine() throws PersistenceException
    {
        databaseManager.ensureInitialized();
        databaseManager.warmUp();
        itemCatalog.preloadAll();
        String playerName = userSettingsService.getDefaultPlayerName();
        return new GameEngine(
                new World(),
                playerName,
                new JdbcGameStateRepository(databaseManager),
                itemCatalog);
    }

    public UserSettingsService getUserSettingsService()
    {
        return userSettingsService;
    }

    public I18n createI18n() throws PersistenceException
    {
        return new I18n(userSettingsService.getLanguage());
    }
}
>>>>>>> 210fdf462ccacad1294a3b412c05a259a656f9cf

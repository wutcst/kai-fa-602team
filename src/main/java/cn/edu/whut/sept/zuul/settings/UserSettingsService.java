package cn.edu.whut.sept.zuul.settings;

import cn.edu.whut.sept.zuul.persistence.PersistenceException;

import java.util.Optional;

/**
 * 用户设置应用服务：封装读写逻辑，供 UI 层使用，不侵入游戏引擎。
 */
public class UserSettingsService
{
    private final UserSettingsRepository repository;
    private UserSettings cachedSettings;

    public UserSettingsService(UserSettingsRepository repository)
    {
        this.repository = repository;
    }

    public UserSettings getSettings() throws PersistenceException
    {
        if (cachedSettings != null) {
            return cachedSettings;
        }
        Optional<UserSettings> loaded = repository.load(UserSettings.DEFAULT_KEY);
        cachedSettings = loaded.orElseGet(UserSettings::new);
        return cachedSettings;
    }

    public void updateSettings(UserSettings settings) throws PersistenceException
    {
        repository.save(settings);
        cachedSettings = settings;
    }

    public String getDefaultPlayerName() throws PersistenceException
    {
        return getSettings().getDefaultPlayerName();
    }

    public String getLanguage() throws PersistenceException
    {
        return getSettings().getLanguage();
    }
}

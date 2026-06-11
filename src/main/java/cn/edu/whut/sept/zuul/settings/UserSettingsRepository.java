package cn.edu.whut.sept.zuul.settings;

import cn.edu.whut.sept.zuul.persistence.PersistenceException;

import java.util.Optional;

/**
 * 用户设置持久化接口，与游戏引擎解耦。
 */
public interface UserSettingsRepository
{
    void save(UserSettings settings) throws PersistenceException;

    Optional<UserSettings> load(String settingsKey) throws PersistenceException;
}

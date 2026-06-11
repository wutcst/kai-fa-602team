package cn.edu.whut.sept.zuul.settings;

/**
 * 用户设置 DTO，与存储实现解耦。
 */
public class UserSettings
{
    public static final String DEFAULT_KEY = "default";

    private String settingsKey = DEFAULT_KEY;
    private String language = "zh_CN";
    private String defaultPlayerName = "探险者";
    private boolean soundEnabled = true;

    public String getSettingsKey()
    {
        return settingsKey;
    }

    public void setSettingsKey(String settingsKey)
    {
        this.settingsKey = settingsKey;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public String getDefaultPlayerName()
    {
        return defaultPlayerName;
    }

    public void setDefaultPlayerName(String defaultPlayerName)
    {
        this.defaultPlayerName = defaultPlayerName;
    }

    public boolean isSoundEnabled()
    {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled)
    {
        this.soundEnabled = soundEnabled;
    }
}

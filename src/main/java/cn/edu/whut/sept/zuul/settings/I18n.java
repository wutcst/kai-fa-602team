package cn.edu.whut.sept.zuul.settings;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 国际化消息加载，基于 resources/i18n 资源文件，与游戏引擎解耦。
 */
public class I18n
{
    private static final String BUNDLE_BASE = "i18n.messages";

    private final ResourceBundle bundle;

    public I18n(String languageTag)
    {
        Locale locale = Locale.forLanguageTag(languageTag.replace('_', '-'));
        this.bundle = ResourceBundle.getBundle(BUNDLE_BASE, locale);
    }

    public String get(String key)
    {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    public String format(String key, Object... args)
    {
        return MessageFormat.format(get(key), args);
    }
}

package cn.edu.whut.sept.zuul.command;

/**
 * 游戏中文命令常量，供解析器、界面与命令注册统一使用。
 */
public final class GameCommands
{
    public static final String GO = "去";
    public static final String HELP = "帮助";
    public static final String QUIT = "退出";
    public static final String LOOK = "查看";
    public static final String BACK = "返回";
    public static final String TAKE = "拾取";
    public static final String DROP = "丢弃";
    public static final String ITEMS = "物品";
    public static final String EAT = "吃";
    public static final String SAVE = "存档";
    public static final String LOAD = "读档";

    public static final String EAT_COOKIE_ARG = "魔法饼干";
    public static final String DEFAULT_SAVE_SLOT = "默认";

    private GameCommands()
    {
    }
}

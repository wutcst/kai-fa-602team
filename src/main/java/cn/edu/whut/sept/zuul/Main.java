package cn.edu.whut.sept.zuul;

import cn.edu.whut.sept.zuul.console.ConsoleGame;
import cn.edu.whut.sept.zuul.ui.GameApp;
import javafx.application.Application;

/**
 * 程序入口：默认启动 JavaFX 图形界面，{@code --console} 使用文本模式。
 */
public class Main
{
    public static void main(String[] args)
    {
        if (args.length > 0 && "--console".equalsIgnoreCase(args[0])) {
            new ConsoleGame().play();
        } else {
            Application.launch(GameApp.class, args);
        }
    }
}

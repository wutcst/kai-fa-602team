package cn.edu.whut.sept.zuul.console;

import cn.edu.whut.sept.zuul.GameBootstrap;
import cn.edu.whut.sept.zuul.command.GameCommands;
import cn.edu.whut.sept.zuul.engine.CommandResult;
import cn.edu.whut.sept.zuul.engine.GameEngine;
import cn.edu.whut.sept.zuul.persistence.PersistenceException;

import java.util.Scanner;

/**
 * 文本模式入口，保留原有控制台玩法。
 */
public class ConsoleGame
{
    private final GameEngine engine;

    public ConsoleGame()
    {
        engine = createEngine();
    }

    private static GameEngine createEngine()
    {
        try {
            return GameBootstrap.createDefault().createGameEngine();
        } catch (PersistenceException e) {
            System.err.println("数据库初始化失败，使用内存存档: " + e.getMessage());
            return new GameEngine();
        }
    }

    public void play()
    {
        System.out.println(engine.getWelcomeMessage());
        System.out.println(engine.getPlayer().getCurrentRoom().getLongDescription());

        Scanner reader = new Scanner(System.in);
        boolean finished = false;
        while (!finished) {
            System.out.print("> ");
            CommandResult result = engine.processCommandLine(
                    reader.hasNextLine() ? reader.nextLine() : GameCommands.QUIT);
            for (String line : result.getMessages()) {
                System.out.println(line);
            }
            finished = result.isFinished();
        }
    }
}

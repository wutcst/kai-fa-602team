package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.engine.CommandResult;
import cn.edu.whut.sept.zuul.engine.GameEngine;

public class GoCommand extends Command
{
    @Override
    public CommandResult execute(GameEngine engine)
    {
        return engine.go(getSecondWord());
    }
}

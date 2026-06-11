package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.engine.CommandResult;
import cn.edu.whut.sept.zuul.engine.GameEngine;

import java.util.Collections;

public class LoadCommand extends Command
{
    @Override
    public CommandResult execute(GameEngine engine)
    {
        if (!hasSecondWord()) {
            return engine.listSaves();
        }
        return engine.loadGame(getSecondWord());
    }
}

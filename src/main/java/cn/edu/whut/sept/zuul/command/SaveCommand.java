package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.engine.CommandResult;
import cn.edu.whut.sept.zuul.engine.GameEngine;

import java.util.Collections;

public class SaveCommand extends Command
{
    @Override
    public CommandResult execute(GameEngine engine)
    {
        if (!hasSecondWord()) {
            return CommandResult.ongoingWithoutStateChange(Collections.singletonList(
                    "请为存档命名，例如：存档 我的冒险"));
        }
        return engine.saveGame(getSecondWord());
    }
}

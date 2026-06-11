package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.engine.CommandResult;
import cn.edu.whut.sept.zuul.engine.GameEngine;

import java.util.Collections;

public class DeleteSaveCommand extends Command
{
    @Override
    public CommandResult execute(GameEngine engine)
    {
        if (!hasSecondWord()) {
            return CommandResult.ongoingWithoutStateChange(Collections.singletonList(
                    "请指定要删除的存档名称，例如：删档 我的冒险"));
        }
        return engine.deleteGame(getSecondWord());
    }
}

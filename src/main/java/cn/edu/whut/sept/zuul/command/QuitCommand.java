package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.engine.CommandResult;
import cn.edu.whut.sept.zuul.engine.GameEngine;

public class QuitCommand extends Command
{
    @Override
    public CommandResult execute(GameEngine engine)
    {
        if (hasSecondWord()) {
            return CommandResult.ongoingWithoutStateChange(
                    java.util.Collections.singletonList("退出什么？直接输入「退出」即可。"));
        }
        return engine.quit();
    }
}

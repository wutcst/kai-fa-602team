package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.engine.CommandResult;
import cn.edu.whut.sept.zuul.engine.GameEngine;

public class SaveCommand extends Command
{
    @Override
    public CommandResult execute(GameEngine engine)
    {
        String slot = hasSecondWord() ? getSecondWord() : GameCommands.DEFAULT_SAVE_SLOT;
        return engine.saveGame(slot);
    }
}

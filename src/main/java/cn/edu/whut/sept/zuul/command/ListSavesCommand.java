<<<<<<< HEAD
package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.engine.CommandResult;
import cn.edu.whut.sept.zuul.engine.GameEngine;

public class ListSavesCommand extends Command
{
    @Override
    public CommandResult execute(GameEngine engine)
    {
        return engine.listSaves();
    }
}
=======
package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.engine.CommandResult;
import cn.edu.whut.sept.zuul.engine.GameEngine;

public class ListSavesCommand extends Command
{
    @Override
    public CommandResult execute(GameEngine engine)
    {
        return engine.listSaves();
    }
}
>>>>>>> 210fdf462ccacad1294a3b412c05a259a656f9cf

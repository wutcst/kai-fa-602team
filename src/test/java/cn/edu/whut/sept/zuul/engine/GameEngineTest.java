package cn.edu.whut.sept.zuul.engine;

import cn.edu.whut.sept.zuul.model.World;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameEngineTest
{
    @Test
    void goEastFromOutsideReachesTheater()
    {
        GameEngine engine = new GameEngine();
        CommandResult result = engine.go(World.DIR_EAST);
        assertFalse(result.isFinished());
        assertTrue(result.getMessageText().contains("演讲厅"));
    }

    @Test
    void backReturnsToPreviousRoom()
    {
        GameEngine engine = new GameEngine();
        engine.go(World.DIR_EAST);
        CommandResult back = engine.back();
        assertTrue(back.getMessageText().contains("主入口"));
    }

    @Test
    void processCommandLineAcceptsChineseCommands()
    {
        GameEngine engine = new GameEngine();
        CommandResult result = engine.processCommandLine("去 东");
        assertTrue(result.getMessageText().contains("演讲厅"));
    }
}

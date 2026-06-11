<<<<<<< HEAD
package cn.edu.whut.sept.zuul.engine;

import cn.edu.whut.sept.zuul.model.World;
import cn.edu.whut.sept.zuul.persistence.InMemoryGameStateRepository;
import cn.edu.whut.sept.zuul.persistence.SaveSlotSummary;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SaveSlotTest
{
    @Test
    void multipleNamedSavesCanBeListedLoadedAndDeleted() throws Exception
    {
        GameEngine engine = new GameEngine(new World(), "探险者", new InMemoryGameStateRepository());
        engine.go(World.DIR_EAST);
        engine.saveGame("第一次冒险");
        engine.go(World.DIR_WEST);
        engine.saveGame("门口记录");

        List<SaveSlotSummary> saves = engine.listSaveSummaries();
        assertEquals(2, saves.size());

        CommandResult load = engine.loadGame("第一次冒险");
        assertTrue(load.getMessageText().contains("演讲厅"));

        CommandResult deleted = engine.deleteGame("门口记录");
        assertTrue(deleted.getMessageText().contains("已删除"));

        assertEquals(1, engine.listSaveSummaries().size());
    }

    @Test
    void loadWithoutNameShowsSaveList()
    {
        GameEngine engine = new GameEngine();
        engine.saveGame("测试档");
        CommandResult result = engine.processCommandLine("读档");
        assertTrue(result.getMessageText().contains("测试档"));
    }
}
=======
package cn.edu.whut.sept.zuul.engine;

import cn.edu.whut.sept.zuul.model.World;
import cn.edu.whut.sept.zuul.persistence.InMemoryGameStateRepository;
import cn.edu.whut.sept.zuul.persistence.SaveSlotSummary;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SaveSlotTest
{
    @Test
    void multipleNamedSavesCanBeListedLoadedAndDeleted() throws Exception
    {
        GameEngine engine = new GameEngine(new World(), "探险者", new InMemoryGameStateRepository());
        engine.go(World.DIR_EAST);
        engine.saveGame("第一次冒险");
        engine.go(World.DIR_WEST);
        engine.saveGame("门口记录");

        List<SaveSlotSummary> saves = engine.listSaveSummaries();
        assertEquals(2, saves.size());

        CommandResult load = engine.loadGame("第一次冒险");
        assertTrue(load.getMessageText().contains("演讲厅"));

        CommandResult deleted = engine.deleteGame("门口记录");
        assertTrue(deleted.getMessageText().contains("已删除"));

        assertEquals(1, engine.listSaveSummaries().size());
    }

    @Test
    void loadWithoutNameShowsSaveList()
    {
        GameEngine engine = new GameEngine();
        engine.saveGame("测试档");
        CommandResult result = engine.processCommandLine("读档");
        assertTrue(result.getMessageText().contains("测试档"));
    }
}
>>>>>>> 210fdf462ccacad1294a3b412c05a259a656f9cf

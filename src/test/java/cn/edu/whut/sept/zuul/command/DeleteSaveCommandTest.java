package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.engine.GameEngine;
import org.junit.jupiter.api.Test;

class DeleteSaveCommandTest {

    @Test
    void execute_NoSecondWord() {
        GameEngine engine = new GameEngine();
        DeleteSaveCommand cmd = new DeleteSaveCommand();
        cmd.execute(engine);
    }

    @Test
    void execute_HasSecondWord() {
        GameEngine engine = new GameEngine();
        DeleteSaveCommand cmd = new DeleteSaveCommand();
        cmd.setSecondWord("存档1");
        cmd.execute(engine);
    }
}
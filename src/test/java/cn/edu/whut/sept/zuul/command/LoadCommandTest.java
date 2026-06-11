package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.engine.GameEngine;
import org.junit.jupiter.api.Test;

class LoadCommandTest {

    @Test
    void execute_NoSecondWord() {
        GameEngine engine = new GameEngine();
        LoadCommand cmd = new LoadCommand();
        cmd.execute(engine);
    }

    @Test
    void execute_HasSecondWord() {
        GameEngine engine = new GameEngine();
        LoadCommand cmd = new LoadCommand();
        cmd.setSecondWord("存档1");
        cmd.execute(engine);
    }
}
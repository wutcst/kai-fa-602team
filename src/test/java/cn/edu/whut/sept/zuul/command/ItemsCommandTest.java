package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.engine.GameEngine;
import org.junit.jupiter.api.Test;

class ItemsCommandTest {
    @Test
    void execute() {
        GameEngine engine = new GameEngine();
        ItemsCommand cmd = new ItemsCommand();
        cmd.execute(engine);
    }
}
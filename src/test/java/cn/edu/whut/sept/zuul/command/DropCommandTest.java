package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.engine.GameEngine;
import org.junit.jupiter.api.Test;

class DropCommandTest {
    @Test
    void execute() {
        GameEngine engine = new GameEngine();
        DropCommand cmd = new DropCommand();
        cmd.setSecondWord("讲义");
        cmd.execute(engine);
    }
}
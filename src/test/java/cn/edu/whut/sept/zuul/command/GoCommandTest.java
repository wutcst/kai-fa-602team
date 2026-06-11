package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.engine.GameEngine;
import org.junit.jupiter.api.Test;

class GoCommandTest {
    @Test
    void execute() {
        GameEngine engine = new GameEngine();
        GoCommand cmd = new GoCommand();
        cmd.setSecondWord("北");
        cmd.execute(engine);
    }
}
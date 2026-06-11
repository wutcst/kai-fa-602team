package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.engine.GameEngine;
import org.junit.jupiter.api.Test;

class LookCommandTest {
    @Test
    void execute() {
        GameEngine engine = new GameEngine();
        LookCommand cmd = new LookCommand();
        cmd.execute(engine);
    }
}
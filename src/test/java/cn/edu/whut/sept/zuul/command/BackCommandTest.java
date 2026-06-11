package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.engine.GameEngine;
import org.junit.jupiter.api.Test;

class BackCommandTest {
    @Test
    void execute() {
        GameEngine engine = new GameEngine();
        BackCommand cmd = new BackCommand();
        cmd.execute(engine);
    }
}
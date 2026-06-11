package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.engine.GameEngine;
import org.junit.jupiter.api.Test;

class HelpCommandTest {
    @Test
    void execute() {
        GameEngine engine = new GameEngine();
        HelpCommand cmd = new HelpCommand();
        cmd.execute(engine);
    }
}
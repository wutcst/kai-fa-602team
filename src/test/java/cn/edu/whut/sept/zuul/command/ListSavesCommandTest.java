package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.engine.GameEngine;
import org.junit.jupiter.api.Test;

class ListSavesCommandTest {
    @Test
    void execute() {
        GameEngine engine = new GameEngine();
        ListSavesCommand cmd = new ListSavesCommand();
        cmd.execute(engine);
    }
}
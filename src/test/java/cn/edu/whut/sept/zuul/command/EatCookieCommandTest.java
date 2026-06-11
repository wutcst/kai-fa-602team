package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.engine.GameEngine;
import org.junit.jupiter.api.Test;

class EatCookieCommandTest {
    @Test
    void execute() {
        GameEngine engine = new GameEngine();
        EatCookieCommand cmd = new EatCookieCommand();
        cmd.execute(engine);
    }
}
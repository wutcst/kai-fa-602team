package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.engine.GameEngine;
import org.junit.jupiter.api.Test;

class TakeCommandTest {

    @Test
    void execute_InvokeEngineTakeWithParam() {
        // 无参构造，内部自动new World，不会报错
        GameEngine engine = new GameEngine();
        TakeCommand takeCmd = new TakeCommand();

        takeCmd.setSecondWord("讲义");
        // 执行覆盖代码
        takeCmd.execute(engine);
    }
}
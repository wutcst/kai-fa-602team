package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.engine.GameEngine;
import org.junit.jupiter.api.Test;

class QuitCommandTest {

    // 分支1：附带第二个单词，走提示分支
    @Test
    void execute_HasSecondWord_Tip() {
        GameEngine engine = new GameEngine();
        QuitCommand cmd = new QuitCommand();
        cmd.setSecondWord("随便");
        cmd.execute(engine);
    }

    // 分支2：无第二个单词，调用引擎quit方法
    @Test
    void execute_NoSecondWord_CallQuit() {
        GameEngine engine = new GameEngine();
        QuitCommand cmd = new QuitCommand();
        cmd.execute(engine);
    }
}
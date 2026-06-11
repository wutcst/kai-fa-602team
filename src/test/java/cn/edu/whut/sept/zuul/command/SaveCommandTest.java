package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.engine.GameEngine;
import org.junit.jupiter.api.Test;

class SaveCommandTest {

    // 分支1：无第二个单词，进入提示分支
    @Test
    void execute_NoSecondWord() {
        GameEngine engine = new GameEngine();
        SaveCommand cmd = new SaveCommand();
        // 不设置secondWord，走if内逻辑
        cmd.execute(engine);
    }

    // 分支2：有第二个单词，调用引擎saveGame
    @Test
    void execute_HasSecondWord() {
        GameEngine engine = new GameEngine();
        SaveCommand cmd = new SaveCommand();
        cmd.setSecondWord("存档1");
        cmd.execute(engine);
    }
}
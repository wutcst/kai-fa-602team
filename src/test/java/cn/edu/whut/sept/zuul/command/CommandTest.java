package cn.edu.whut.sept.zuul.command;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CommandTest {

    // 借用子类实例测试抽象父类公共方法
    private final Command command = new GoCommand();

    @Test
    void hasSecondWord_无第二个单词_返回false() {
        command.setSecondWord(null);
        assertFalse(command.hasSecondWord());

        command.setSecondWord("   ");
        assertFalse(command.hasSecondWord());
    }

    @Test
    void hasSecondWord_有第二个单词_返回true() {
        command.setSecondWord("北");
        assertTrue(command.hasSecondWord());
    }

    @Test
    void setAndGetSecondWord_赋值取值正常() {
        String testWord = "讲义";
        command.setSecondWord(testWord);
        assertEquals(testWord, command.getSecondWord());
    }
}
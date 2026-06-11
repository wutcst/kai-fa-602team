package cn.edu.whut.sept.zuul.command;

import org.junit.jupiter.api.Test;
import java.util.List;

class CommandWordsTest {

    @Test
    void testGet_ValidWord() {
        CommandWords commandWords = new CommandWords();
        commandWords.get("go");
    }

    @Test
    void testGet_NullWord() {
        CommandWords commandWords = new CommandWords();
        commandWords.get(null);
    }

    @Test
    void testGet_InvalidWord() {
        CommandWords commandWords = new CommandWords();
        commandWords.get("abc");
    }

    @Test
    void testGetCommandNames() {
        CommandWords commandWords = new CommandWords();
        List<String> names = commandWords.getCommandNames();
    }
}
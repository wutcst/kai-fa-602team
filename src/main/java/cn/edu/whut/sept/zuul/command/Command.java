package cn.edu.whut.sept.zuul.command;

import cn.edu.whut.sept.zuul.engine.CommandResult;
import cn.edu.whut.sept.zuul.engine.GameEngine;

public abstract class Command
{
    private String secondWord;

    public String getSecondWord()
    {
        return secondWord;
    }

    public boolean hasSecondWord()
    {
        return secondWord != null && !secondWord.isBlank();
    }

    public void setSecondWord(String secondWord)
    {
        this.secondWord = secondWord;
    }

    public abstract CommandResult execute(GameEngine engine);
}

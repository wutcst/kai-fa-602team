package cn.edu.whut.sept.zuul.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 命令执行结果，供控制台或 GUI 统一消费。
 */
public class CommandResult
{
    private final boolean finished;
    private final boolean stateChanged;
    private final List<String> messages;

    private CommandResult(boolean finished, boolean stateChanged, List<String> messages)
    {
        this.finished = finished;
        this.stateChanged = stateChanged;
        this.messages = messages;
    }

    public static CommandResult ongoing(String message)
    {
        return ongoing(Collections.singletonList(message));
    }

    public static CommandResult ongoing(List<String> messages)
    {
        return new CommandResult(false, true, new ArrayList<>(messages));
    }

    public static CommandResult ongoingWithoutStateChange(List<String> messages)
    {
        return new CommandResult(false, false, new ArrayList<>(messages));
    }

    public static CommandResult gameOver(List<String> messages)
    {
        return new CommandResult(true, true, new ArrayList<>(messages));
    }

    public boolean isFinished()
    {
        return finished;
    }

    public boolean isStateChanged()
    {
        return stateChanged;
    }

    public List<String> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }

    public String getMessageText()
    {
        return String.join("\n", messages);
    }
}

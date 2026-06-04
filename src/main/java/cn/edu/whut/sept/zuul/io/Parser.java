package cn.edu.whut.sept.zuul.io;

import cn.edu.whut.sept.zuul.command.Command;
import cn.edu.whut.sept.zuul.command.CommandWords;
import cn.edu.whut.sept.zuul.command.GameCommands;

import java.util.Scanner;

/**
 * 将用户输入解析为命令对象，不执行 I/O 输出。
 */
public class Parser
{
    public Command parseCommand(String inputLine, CommandWords commandWords)
    {
        if (inputLine == null || inputLine.isBlank()) {
            return null;
        }

        String trimmed = inputLine.trim();
        int space = trimmed.indexOf(' ');
        String word1 = space < 0 ? trimmed : trimmed.substring(0, space);
        String rest = space < 0 ? null : trimmed.substring(space + 1).trim();

        if (GameCommands.EAT.equals(word1) && rest != null
                && (rest.startsWith(GameCommands.EAT_COOKIE_ARG) || rest.startsWith("饼干"))) {
            Command eat = commandWords.get(GameCommands.EAT);
            if (eat != null) {
                eat.setSecondWord(GameCommands.EAT_COOKIE_ARG);
            }
            return eat;
        }

        Command command = commandWords.get(word1);
        if (command != null) {
            command.setSecondWord(rest == null || rest.isEmpty() ? null : rest);
        }
        return command;
    }

    public Command parseCommandFromScanner(Scanner reader, CommandWords commandWords)
    {
        if (!reader.hasNextLine()) {
            return null;
        }
        return parseCommand(reader.nextLine(), commandWords);
    }
}

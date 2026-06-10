package cn.edu.whut.sept.zuul.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandWords
{
    private final Map<String, Command> commands = new HashMap<>();

    public CommandWords()
    {
        register(GameCommands.GO, new GoCommand());
        register(GameCommands.HELP, new HelpCommand());
        register(GameCommands.QUIT, new QuitCommand());
        register(GameCommands.LOOK, new LookCommand());
        register(GameCommands.BACK, new BackCommand());
        register(GameCommands.TAKE, new TakeCommand());
        register(GameCommands.DROP, new DropCommand());
        register(GameCommands.ITEMS, new ItemsCommand());
        register(GameCommands.EAT, new EatCookieCommand());
        register(GameCommands.SAVE, new SaveCommand());
        register(GameCommands.LOAD, new LoadCommand());
        register(GameCommands.DELETE_SAVE, new DeleteSaveCommand());
        register(GameCommands.LIST_SAVES, new ListSavesCommand());
    }

    private void register(String name, Command command)
    {
        commands.put(name, command);
    }

    public Command get(String word)
    {
        if (word == null) {
            return null;
        }
        return commands.get(word.trim());
    }

    public List<String> getCommandNames()
    {
        List<String> names = new ArrayList<>(commands.keySet());
        Collections.sort(names);
        names.add(GameCommands.EAT + " " + GameCommands.EAT_COOKIE_ARG);
        return names;
    }
}

package ru.ifmo.se.lab6.client;

import ru.ifmo.se.lab6.client.command.ClientCommand;

import java.util.HashMap;
import java.util.Map;

public class Application {
    private final Map<String, ClientCommand> commandHelperMap = new HashMap<>();

    public void register(String commandName, ClientCommand command) {
        commandHelperMap.put(commandName, command);
    }

    public String[] execute(String commandName, boolean mode, String... args) {
        ClientCommand command = commandHelperMap.get(commandName);
        try {
            if (command != null)
                return command.execute(mode, args);
            else
                return null;
        } catch (NullPointerException e) {
            System.out.println("Конец ввода");
            System.exit(0);
        }
        return null;
    }

    public Map<String, ClientCommand> getCommandHelperMap() {
        return commandHelperMap;
    }
}


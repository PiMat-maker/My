package ru.ifmo.se.lab6.server;

import ru.ifmo.se.lab6.Command;
import ru.ifmo.se.lab6.server.command.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * В классе {@code Application} определяются все команды, которые могут быть использованы пользователем,
 * для изменения коллекции.
 * invoker - вызывающий объект
 */
public class Application {
    /**
     * Получает экземпляр функционального интерфейса - команду, исполняет её, вызывая метод {@code exec},
     * и выводит возвращаемое методом значение.
     *
     * @param command Экземпляр функционального интерфейса, который используется для вызова у него метода {@code exec}.
     * @param products Коллекция, над которой будут выполняться соответстсвующие команды.
     * @param input Поток ввода, используется для получения данных командами.
     * @param mode Режим выполнения команд: интерактивный, если <bold>false</bold>, и скрипт, если <bold>true</bold>
     */
    private final Map<String, ServerCommand> commandMap = new HashMap<>();
    private final Map<String, ServerCommand> serviceCommandMap = new HashMap<>();

    public String execute(String commandName, String... args) {
        ServerCommand command = commandMap.getOrDefault(commandName, new ErrorCommand());
        try {
            return command.execute(args);
        } catch (NullPointerException e) {
            System.out.println("Конец ввода");
            return "exit";
        }
    }

    void register(String commandName, ServerCommand command) {
        commandMap.put(commandName, command);
    }

    String executeServiceCmd(String commandName) {
        ServerCommand command = serviceCommandMap.getOrDefault(commandName, (String... args) -> "Ошибка: данная команда не найдена.");
        return command.execute();
    }

    void registerServiceCmd(String commandName, ServerCommand command) {
        serviceCommandMap.put(commandName, command);
    }

    public String help() {
        String result = commandMap.values().stream()
                .map(Command::getDescription)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.joining("\n"));
        return result + "\n";
    }
}
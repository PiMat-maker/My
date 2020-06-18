package ru.ifmo.se.lab6.server.command;

import java.util.*;

public class ScriptCommand implements ServerCommand {
    private List<ServerCommand> commands;
    static class FileSet {
        private static Set<String> fileSet = new HashSet<>();
        static boolean addFileSet(String file) {
            return fileSet.add(file);
        }
        static Set<String> getFileSet() {
            return fileSet;
        }
        static boolean removeFile(String file) {
            return fileSet.remove(file);
        }
        static void eraseSet(){
            fileSet.clear();
        }
        static boolean isEmpty() { return fileSet.isEmpty(); }
    }

    public ScriptCommand(ServerCommand... commands) {
        this.commands = new ArrayList<>(Arrays.asList(commands));
    }

    public void setCommands(List<ServerCommand> commands) {
        this.commands = commands;
    }

    @Override
    public String execute(String... args) {
        System.out.println("---------Скрипт выполняется---------");
        for (ServerCommand command : commands) {
            command.execute();
        }
        System.out.println("---------Результат выполнения скрипта был выведен на экран---------\n");
        return null;
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме;");
    }
}

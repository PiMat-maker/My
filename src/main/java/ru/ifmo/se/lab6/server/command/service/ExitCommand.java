package ru.ifmo.se.lab6.server.command.service;

import ru.ifmo.se.lab6.server.command.ServerCommand;

public class ExitCommand implements ServerCommand {
    @Override
    public String execute(String... args) {
        System.out.println("Завершение программы.");
        System.exit(0);
        return null;
    }
}

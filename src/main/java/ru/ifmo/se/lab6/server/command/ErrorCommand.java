package ru.ifmo.se.lab6.server.command;

import ru.ifmo.se.lab6.ServiceCommand;

public class ErrorCommand implements ServerCommand {
    @Override
    public String execute(String... args) {
        return "ERROR_COMMAND";
    }
}



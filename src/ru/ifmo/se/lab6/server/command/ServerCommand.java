package ru.ifmo.se.lab6.server.command;

import ru.ifmo.se.lab6.Command;

public interface ServerCommand extends Command{
    String execute(String... args);
}

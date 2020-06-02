package ru.ifmo.se.lab6.client.command;

import ru.ifmo.se.lab6.Command;

@FunctionalInterface
public interface ClientCommand extends Command {
    String[] execute(boolean mode, String... args);
}

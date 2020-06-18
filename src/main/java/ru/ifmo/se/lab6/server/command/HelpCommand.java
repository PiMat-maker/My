package ru.ifmo.se.lab6.server.command;

import ru.ifmo.se.lab6.Command;
import ru.ifmo.se.lab6.server.Application;

import java.util.Optional;

public class HelpCommand implements ServerCommand {
    private Application application;

    public HelpCommand(Application application) {
        this.application = application;
    }

    @Override
    public String execute(String... args) {
        return application.help();
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("help : вывести справку по доступным командам;");
    }
}

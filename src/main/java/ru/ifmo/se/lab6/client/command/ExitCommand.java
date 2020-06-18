package ru.ifmo.se.lab6.client.command;

import ru.ifmo.se.lab6.client.ClientInOut;

import java.util.Optional;

public class ExitCommand implements ClientCommand {
    private final ClientInOut inOut;

    public ExitCommand(ClientInOut inOut) {
        this.inOut = inOut;
    }

    @Override
    public String[] execute(boolean mode, String... args) {
        inOut.writeLine("Завершение программы.");
        System.exit(0);
        return null;
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("exit : завершить программу (отключиться от сервера);");
    }
}

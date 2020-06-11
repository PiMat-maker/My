package ru.ifmo.se.lab6.server.command;

import ru.ifmo.se.lab6.Command;
import ru.ifmo.se.lab6.server.ProductCollection;

import java.util.Optional;

public class ClearCommand implements ServerCommand {
    private final ProductCollection productCollection;

    public ClearCommand(ProductCollection productCollection) {
        this.productCollection = productCollection;
    }

    @Override
    public String execute(String... args) {
        return productCollection.clear();
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("clear : очистить коллекцию;");
    }
}

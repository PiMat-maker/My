package ru.ifmo.se.lab6.server.command;

import ru.ifmo.se.lab6.Command;
import ru.ifmo.se.lab6.server.ProductCollection;

import java.util.Optional;

public class AddCommand implements ServerCommand {
    private final ProductCollection productCollection;

    public AddCommand(ProductCollection productCollection) {
        this.productCollection = productCollection;
    }

    @Override
    public String execute(String... args) {
        return productCollection.add(args);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("add {element} : добавить новый элемент в коллекцию;");
    }
}

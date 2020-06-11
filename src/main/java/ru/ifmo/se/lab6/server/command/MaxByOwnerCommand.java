package ru.ifmo.se.lab6.server.command;

import ru.ifmo.se.lab6.server.ProductCollection;

import java.util.Optional;

public class MaxByOwnerCommand implements ServerCommand {
    private final ProductCollection productCollection;

    public MaxByOwnerCommand(ProductCollection productCollection) {
        this.productCollection = productCollection;
    }

    @Override
    public String execute(String... args) {
        return productCollection.getMaxByOwner();
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("max_by_owner : вывести любой объект из коллекции, " +
                "значение поля owner которого является максимальным;");
    }
}

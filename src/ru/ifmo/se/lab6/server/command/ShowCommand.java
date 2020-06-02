package ru.ifmo.se.lab6.server.command;

import ru.ifmo.se.lab6.Command;
import ru.ifmo.se.lab6.server.ProductCollection;

import java.util.Optional;

public class ShowCommand implements ServerCommand {
    private final ProductCollection productCollection;

    public ShowCommand(ProductCollection productCollection) {
        this.productCollection = productCollection;
    }

    @Override
    public String execute(String... args) {
        return productCollection.show();
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении;");
    }
}

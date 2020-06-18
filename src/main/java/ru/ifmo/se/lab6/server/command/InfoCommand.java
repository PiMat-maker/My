package ru.ifmo.se.lab6.server.command;

import ru.ifmo.se.lab6.Command;
import ru.ifmo.se.lab6.server.ProductCollection;

import java.util.Optional;

public class InfoCommand implements ServerCommand {
    private final ProductCollection productCollection;

    public InfoCommand(ProductCollection productsCollection) {
        this.productCollection = productsCollection;
    }

    @Override
    public String execute(String... args) {
        return productCollection.info();
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов);");
    }

}

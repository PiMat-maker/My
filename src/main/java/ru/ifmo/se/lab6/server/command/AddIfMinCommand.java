package ru.ifmo.se.lab6.server.command;

import ru.ifmo.se.lab6.server.ProductCollection;

import java.util.Optional;

public class AddIfMinCommand implements ServerCommand {
    private final ProductCollection productCollection;

    public AddIfMinCommand(ProductCollection productCollection) {
        this.productCollection = productCollection;
    }

    @Override
    public String execute(String... args) {
        if (args.length > 0)
            return productCollection.addIfMin(args[0]);
        else
            return "Ошибка: недостаточно аргументов";
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("add_if_min {element} : добавить новый элемент в коллекцию," +
                " если его значение меньше, чем у наименьшего элемента этой коллекции;");
    }
}

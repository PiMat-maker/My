package ru.ifmo.se.lab6.server.command;

import ru.ifmo.se.lab6.client.ClientInOut;
import ru.ifmo.se.lab6.server.ProductCollection;

import java.util.Optional;

public class RemoveGreaterCommand implements ServerCommand {
    private final ProductCollection productCollection;

    public RemoveGreaterCommand(ProductCollection productCollection) {
        this.productCollection = productCollection;
    }

    @Override
    public String execute(String... args) {
        if (args.length > 0) {
            return productCollection.removeGreater(args[0]);
        }
        else
            return "Ошибка: недостаточно аргументов.";
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("remove_greater {element} : удалить из коллекции все элементы, превышающие заданный;");
    }
}

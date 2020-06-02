package ru.ifmo.se.lab6.server.command;

import ru.ifmo.se.lab6.server.ProductCollection;

import java.util.Optional;

public class RemoveByIdCommand implements ServerCommand {
    private final ProductCollection productCollection;

    public RemoveByIdCommand(ProductCollection productCollection) {
        this.productCollection = productCollection;
    }

    @Override
    public String execute(String... args) {
        if (args.length > 0) {
            int id = Integer.parseInt(args[0]);
            return productCollection.removeById(id);
        }
        else
            return "Ошибка: недостаточно аргументов для выполнения команд.";
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("remove_by_id id : удалить элемент из коллекции по его id;");
    }
}
package ru.ifmo.se.lab6.server.command;

import ru.ifmo.se.lab6.collection.UnitOfMeasure;
import ru.ifmo.se.lab6.server.ProductCollection;

import java.util.Optional;

public class RemoveAllByUnitOfMeasureCommand implements ServerCommand {
    private final ProductCollection productCollection;

    public RemoveAllByUnitOfMeasureCommand(ProductCollection productCollection) {
        this.productCollection = productCollection;
    }

    @Override
    public String execute(String... args) {
        if (args.length > 0) {
            return productCollection.removeAllByUnitOfMeasure(UnitOfMeasure.valueOf(args[0]));
        }
        else
            return "Ошибка: недостаточно аргументов.";
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("remove_all_by_unit_of_measure unitOfMeasure : удалить из коллекции все элементы, " +
                "значение поля unitOfMeasure которого эквивалентно заданному;");
    }
}

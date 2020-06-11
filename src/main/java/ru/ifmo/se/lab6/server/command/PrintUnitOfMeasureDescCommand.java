package ru.ifmo.se.lab6.server.command;

import ru.ifmo.se.lab6.ServiceCommand;
import ru.ifmo.se.lab6.collection.UnitOfMeasure;
import ru.ifmo.se.lab6.server.ProductCollection;

import java.util.Optional;

public class PrintUnitOfMeasureDescCommand implements ServerCommand {
    private final ProductCollection productCollection;

    public PrintUnitOfMeasureDescCommand(ProductCollection productCollection) {
        this.productCollection = productCollection;
    }

    @Override
    public String execute(String... args) {
        return productCollection.getUnitOfMeasureDesc();
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("print_field_descending_unit_of_measure unitOfMeasure : " +
                "вывести значения поля unitOfMeasure в порядке убывания;");
    }
}

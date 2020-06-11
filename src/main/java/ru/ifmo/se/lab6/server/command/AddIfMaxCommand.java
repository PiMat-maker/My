package ru.ifmo.se.lab6.server.command;

import ru.ifmo.se.lab6.server.ProductCollection;

import java.util.Optional;

public class AddIfMaxCommand implements ServerCommand {
    private final ProductCollection productCollection;

    public AddIfMaxCommand(ProductCollection productCollection) {
        this.productCollection = productCollection;
    }

    @Override
    public String execute(String... args) {
        if (args.length > 0)
            return productCollection.addIfMax(args[0]);
        else
            return "Ошибка: недостаточно аргументов";
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("add_if_max {element} : " +
                "добавить новый элемент в коллекцию, " +
                "если его значение превышает значение наибольшего элемента этой коллекции;");
    }
}

//в клиентском приложении нужно сделать проверку на наличие в названии команды слова Add,
//чтобы вызвать команду AddCommand, вместо создания и последующего вызова команд AddIfMax(Min)
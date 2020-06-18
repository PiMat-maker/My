package ru.ifmo.se.lab6.server.command;

import ru.ifmo.se.lab6.Command;
import ru.ifmo.se.lab6.ServiceCommand;
import ru.ifmo.se.lab6.collection.Product;
import ru.ifmo.se.lab6.server.ProductCollection;
import java.util.Optional;

public class UpdateCommand implements ServerCommand {
    private final ProductCollection productCollection;
    private int id = 0;

    public UpdateCommand(ProductCollection productCollection) {
        this.productCollection = productCollection;
    }

    @Override
    public String execute(String... args) {
        if (args.length != 0) {
            if (args.length == 1) {
                int id = Integer.parseInt(args[0]);
                this.id = id;
                if (productCollection.getProducts().stream().map(Product::getId).anyMatch(k -> k == id)) {
                    return "INPUT,add";
                } else {
                    return "Элемент с данным id: " + this.id + " - не найден.\n\n";
                }
            }
            else {
                //значит аргумент не число, а массив (поля продукта)
                return productCollection.update(this.id, args);
            }
        }
        else
            return "";
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("update id {element} : обновить значение элемента коллекции, id которого равен заданному;");
    }
}

package ru.ifmo.se.lab6.client.command;

import ru.ifmo.se.lab6.client.ClientInOut;
import ru.ifmo.se.lab6.collection.Product;

import java.util.ArrayList;
import java.util.List;

public class UpdateCommand implements ClientCommand {
    private final ClientInOut inOut;

    public UpdateCommand(ClientInOut inOut) {
        this.inOut = inOut;
    }

    @Override
    public String[] execute(boolean mode, String... args) {
        if (args.length == 0) {
            if (!mode) {
                inOut.writeLine("Введите id продукта, который нужно обновить:\n$");
            } else {
                inOut.readLine();
            }
            int id;
            try {
                id = Integer.parseInt(inOut.readLine());
            } catch (NumberFormatException e) {
                inOut.writeLine("Ошибка: некорректный ввод.\n");
                return null;
            }
            return new String[]{String.valueOf(id)};
        }
        else {
            return new AddCommand(inOut).execute(mode);
        }
    }
}

package ru.ifmo.se.lab6.client.command;

import ru.ifmo.se.lab6.client.ClientInOut;

public class RemoveByIdCommand implements ClientCommand {
    private final ClientInOut inOut;

    public RemoveByIdCommand(ClientInOut inOut) {
        this.inOut = inOut;
    }

    @Override
    public String[] execute(boolean mode, String... args) {
        if (!mode) { inOut.writeLine("Введите id продукта, который нужно удалить:\n$"); }
        else { inOut.readLine(); }
        int id;
        try {
            id = Integer.parseInt(inOut.readLine());
        }
        catch (NumberFormatException e) {
            inOut.writeLine("Ошибка: некорректный ввод.");
            return null;
        }
        return new String[]{String.valueOf(id)};
    }
}

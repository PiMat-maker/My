package ru.ifmo.se.lab6.client.command;

import ru.ifmo.se.lab6.client.ClientInOut;
import ru.ifmo.se.lab6.collection.UnitOfMeasure;

public class RemoveAllByUnitOfMeasure implements ClientCommand {
    private final ClientInOut inOut;

    public RemoveAllByUnitOfMeasure(ClientInOut inOut) {
        this.inOut = inOut;
    }

    @Override
    public String[] execute(boolean mode, String... args) {
        if (!mode)
            inOut.writeLine("Выберите единицы измерения: " + UnitOfMeasure.enumsList().toString() + "\n$");
        else
            inOut.readLine();
        String str = "";
        if (inOut.hasNextLine()) str = inOut.readLine();
        while (!UnitOfMeasure.enumsList().contains(str.toUpperCase()) || str.equals("")) {
            if (mode) { return new String[]{"Ошибка: некорректный ввод единиц измерения"}; }
            inOut.writeLine("Некорректный ввод! " +
                    "Выберите единицы измерения: " + UnitOfMeasure.enumsList().toString() + "\n$");
            str = inOut.readLine();
        }
        return new String[]{str.toUpperCase()};
    }
}

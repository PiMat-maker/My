package ru.ifmo.se.lab6.client.command;

import ru.ifmo.se.lab6.client.ClientInOut;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddCommand implements ClientCommand {
    private final ClientInOut inOut;

    public AddCommand(ClientInOut inOut) {
        this.inOut = inOut;
    }

    @Override
    public String[] execute(boolean mode, String... args) {
        String[] fields = new String[11];
        if (!mode) { inOut.writeLine("Введите имя продукта: \n$"); }
        else { inOut.readLine(); }
        String str = inOut.readLine();
        Pattern pattern = Pattern.compile("\\s+");
        Matcher matcher;
        matcher = pattern.matcher(str);
        while (matcher.matches() || str.equals("")) {
            if (mode) {
                inOut.writeLine("Ошибка: неверный ввод в поле <Имя продукта>.");
            }
            inOut.writeLine("Ошибка: данное поле не может быть пустым. Введите имя продукта ещё раз: \n$");
            str = inOut.readLine();
            matcher.reset(str);
        }
        fields[0] = str;

        long lng = 0;
        if (!mode) inOut.writeLine("Введите координату X: \n$");
        while (true) {
            try {
                if (inOut.hasNextLine()) {
                    lng = Long.parseLong(inOut.readLine());
                }
            } catch (NumberFormatException e) {
                if (mode) {
                    inOut.writeLine("Ошибка: неверный ввод в поле <Координата X>.");
                    return null;
                }
                inOut.writeLine("Ошибка: неверный ввод. Введите число: \n$");
                continue;
            }
            break;
        }
        fields[1] = String.valueOf(lng);

        if (!mode) inOut.writeLine("Введите координату Y: \n$");
        float flt;
        while (true) {
            try {
                flt = Float.parseFloat(inOut.readLine());
            } catch (NumberFormatException e) {
                if (mode) {
                    inOut.writeLine("Ошибка: неверный ввод в поле <Координата Y>.");
                    return null;
                }
                inOut.writeLine("Ошибка: неверный ввод. Введите число: \n$");
                continue;
            }
            if (flt <= -970.0) {
                inOut.writeLine("Ошибка: число должно быть больше -970. Введите число ещё раз: \n$");
                continue;
            }
            break;
        }
        fields[2] = String.valueOf(flt);

        if (!mode) inOut.writeLine("Введите стоимость продукта: \n$");
        while (true) {
            try {
                flt = Float.parseFloat(inOut.readLine());
            } catch (NumberFormatException e) {
                if (mode) {
                    inOut.writeLine("Ошибка: неверный ввод в поле <Стоимость продукта>.");
                    return null;
                }
                inOut.writeLine("Ошибка: неверный ввод. Введите число: \n$");
                continue;
            }
            if (flt <= 0.0) {
                inOut.writeLine("Ошибка: число должно быть больше 0. Введите число ещё раз: \n$");
                continue;
            }
            break;
        }
        fields[3] = String.valueOf(flt);

        if (!mode) inOut.writeLine("Введите себестоимость продукта: \n$");
        while (true) {
            try {
                lng = Long.parseLong(inOut.readLine());
            } catch (NumberFormatException e) {
                if (mode) {
                    inOut.writeLine("Ошибка: неверный ввод в поле <Себетоимость продукта>.");
                    return null;
                }
                inOut.writeLine("Ошибка: неверный ввод. Введите число: \n$");
                continue;
            }
            if (lng <= 0) {
                inOut.writeLine("Ошибка: число должно быть больше 0. Введите число ещё раз: \n$");
                continue;
            }
            break;
        }
        fields[4] = String.valueOf(lng);

        if (!mode) inOut.writeLine("Выберите единицы измерения: " + ru.ifmo.se.lab6.collection.UnitOfMeasure.enumsList().toString() + "\n$");
        str = inOut.readLine();
        while (!ru.ifmo.se.lab6.collection.UnitOfMeasure.enumsList().contains(str.toUpperCase()) || str.length() == 0) {
            if (mode) {
                inOut.writeLine("Ошибка: неверный ввод в поле <Единицы измерения>.");
                return null;
            }
            inOut.writeLine("Ошибка: некорректный ввод. " +
                    "Введите единицы измерения ещё раз: \n$");
            str = inOut.readLine();
        }
        ru.ifmo.se.lab6.collection.UnitOfMeasure units = ru.ifmo.se.lab6.collection.UnitOfMeasure.valueOf(str.toUpperCase());
        fields[5] = units.toString();

        if (!mode) inOut.writeLine("Введите имя владельца продукта: \n$");
        str = "";
        if (inOut.hasNextLine()) str = inOut.readLine();
        matcher.reset(str);
        while (matcher.matches() || str.equals("")) {
            if (mode) {
                inOut.writeLine("Ошибка: неверный ввод в поле <Имя владельца>.");
                return null;
            }
            inOut.writeLine("Ошибка: данное поле не может быть пустым. Введите имя продукта ещё раз: \n$");
            str = inOut.readLine();
            matcher.reset(str);
        }
        fields[6] = str;

        if (!mode) inOut.writeLine("Введите рост владельца (" + units.getRussian()
                + "):\n$");
        while (true) {
            try {
                flt = Float.parseFloat(inOut.readLine());
            } catch (NumberFormatException e) {
                if (mode) {
                    inOut.writeLine("Ошибка: неверный ввод в поле <Рост владельца>.");
                    return null;
                }
                inOut.writeLine("Ошибка: неверный ввод. Введите число: \n$");
                continue;
            }
            if (flt <= 0.0) {
                inOut.writeLine("Ошибка: число должно быть больше 0. Введите число ещё раз: \n$");
                continue;
            }
            break;
        }
        fields[7] = String.valueOf(flt);

        if (!mode) inOut.writeLine("Выберите цвет глаз владельца: " + ru.ifmo.se.lab6.collection.Color.enumsList().toString() + "\n$");
        str = inOut.readLine();
        while (!ru.ifmo.se.lab6.collection.Color.enumsList().contains(str.toUpperCase()) || str.length() == 0) {
            if (mode) {
                inOut.writeLine("Ошибка: неверный ввод в поле <Цвет глаз владельца>.");
                return null;
            }
            inOut.writeLine("Ошибка: некорректный ввод. " +
                    "Введите цвет глаз ещё раз: \n$");
            str = inOut.readLine();
        }
        fields[8] = str.toUpperCase();

        if (!mode) inOut.writeLine("Выберите цвет волос владельца: " + ru.ifmo.se.lab6.collection.Color.enumsList().toString() + "\n$");
        str = inOut.readLine();
        while (!ru.ifmo.se.lab6.collection.Color.enumsList().contains(str.toUpperCase()) || str.length() == 0) {
            if (mode) {
                inOut.writeLine("Ошибка: неверный ввод в поле <Цвет волос владельца>.");
                return null;
            }
            inOut.writeLine("Ошибка: некорректный ввод. " +
                    "Введите цвет волос ещё раз: \n$");
            str = inOut.readLine();
        }
        fields[9] = str.toUpperCase();

        if (!mode) {
            inOut.writeLine("Выберите национальность: " + ru.ifmo.se.lab6.collection.Country.enumsList().toString() + "\n$");
        }
        str = inOut.readLine();
        while (!ru.ifmo.se.lab6.collection.Country.enumsList().contains(str.toUpperCase()) || str.length() == 0) {
            if (mode) {
                inOut.writeLine("Ошибка: неверный ввод в поле <Национальность>.");
                return null;
            }
            inOut.writeLine("Ошибка: некорректный ввод. " +
                    "Введите национальность ещё раз: \n$");
            str = inOut.readLine();
        }
        fields[10] = str.toUpperCase();

        return fields;
    }
}

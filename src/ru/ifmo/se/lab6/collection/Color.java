package ru.ifmo.se.lab6.collection;

import java.io.Serializable;
import java.util.*;
/**
 * Класс перечислений {@code Color} содержит доступный список цветов.
 *  Используется для определения двух полей объекта класса {@code Person}: {@code eyeColor, hairColor}.
 */
public enum Color implements Serializable {

    GREEN,
    RED,
    ORANGE,
    WHITE,
    BROWN;

    /**
     * @return Возвращает немодифицируемый список элементов данного класса.
     */
    public static Collection<String> enumsList() {
        List<String> enums = new ArrayList<>();
        for (Color e : Color.values()) {
            enums.add(e.toString());
        }
        return Collections.unmodifiableCollection(enums);
    }

}

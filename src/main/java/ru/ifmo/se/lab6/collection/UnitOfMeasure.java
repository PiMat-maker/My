package ru.ifmo.se.lab6.collection;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
/**
 * Класс перечислений {@code UnitOfMeasure} определяет использующиеся единицы измерения.
 *  Задаётся в соответствующем поле класса {@code Product}.
 */
public enum UnitOfMeasure implements Serializable {

    METERS("метры"),
    CENTIMETERS("сантиметры"),
    FEET("футы"),
    INCHES("дюймы");

    private String name_rus;
    UnitOfMeasure(String name_rus) {
        this.name_rus = name_rus;
    }

    public String getRussian() {
        return this.name_rus;
    }

    /**
     * @return Возвращает список элементов данного класса.
     */
    public static List<String> enumsList() {
        List<String> enums = new ArrayList<>();
        for (UnitOfMeasure e : UnitOfMeasure.values()) {
            enums.add(e.toString());
        }
        return new ArrayList<>(enums);
    }

}

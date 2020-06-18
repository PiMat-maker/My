package ru.ifmo.se.lab6.collection;

import java.io.Serializable;
import java.util.*;
/**
  * Класс перечислений {@code Country} содержит доступный список стран.
  * Используется для определения одного из полей объекта класса {@code Person}.
  */
public enum Country implements Serializable {

    INDIA,
    ITALY,
    SOUTH_KOREA;

    /**
     * @return Возвращает немодифицируемый список элементов данного класса.
     */
    public static Collection<String> enumsList() {
        List<String> enums = new ArrayList<>();
        for (Country e : Country.values()) {
            enums.add(e.toString());
        }
        return Collections.unmodifiableCollection(enums);
    }
}

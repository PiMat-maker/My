package ru.ifmo.se.lab6.collection;

import java.io.Serializable;
import java.util.Objects;

/**
 * Объект класса {@code Person} определяет владельца продукта.
 * Содержится в одном из полей класса {@code Product}.
 */
public class Person implements Comparable<Person>, Serializable {

    private String name; //Поле не может быть null, Строка не может быть пустой
    private float height; //Значение поля должно быть больше 0
    private Color eyeColor; //Поле может быть null
    private Color hairColor; //Поле не может быть null
    private Country nationality; //Поле может быть null

    Person(String name, float height, Color eyeColor, Color hairColor, Country nationality){
        setName(name);
        setHeight(height);
        setEyeColor(eyeColor);
        setHairColor(hairColor);
        setNationality(nationality);
    }
    public Person() {
        name = "";
        height = 0;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setEyeColor(Color eyeColor) {
        this.eyeColor = eyeColor;
    }
    public Color getEyeColor() {
        return eyeColor;
    }
    public void setHairColor(Color hairColor) {
        this.hairColor = hairColor;
    }
    public Color getHairColor() {
        return hairColor;
    }
    public void setHeight(float height) {
        this.height = height;
    }
    public Country getNationality() {
        return nationality;
    }
    public void setNationality(Country nationality) {
        this.nationality = nationality;
    }
    public float getHeight() {
        return height;
    }

    @Override
    public String toString(){
        return "[name = " + name + ", height = " + height + ", color of eyes = " + eyeColor +
                ", hair color = " + hairColor + ", country = " + nationality + "]";
    }

    @Override
    public int hashCode(){
        return Objects.hash(name, hairColor, eyeColor, height, nationality);
    }

    /**
     * Выполняет сравнение двух объектов {@code Person} сначала по имени, а потом по росту.
     */
    @Override
    public int compareTo(Person other) {
        int i = this.name.compareTo(other.name);
        if (i == 0) return Float.compare(this.height, other.height);
        else return i;
    }
}

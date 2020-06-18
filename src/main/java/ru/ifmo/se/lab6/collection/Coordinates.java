package ru.ifmo.se.lab6.collection;

import java.io.Serializable;
import java.util.Objects;
/**
 * Объект класса {@code Coordinates} определяет соответствующие координы <bold>X</bold> и <bold>Y</bold>.
 * Содержится в одном из полей класса {@code Product}.
 */
public class Coordinates implements Serializable {

    private long x;
    private Float y; //Значение поля должно быть больше -970, Поле не может быть null


    Coordinates(long x, Float y){
        this.x = x;
        this.y = y;
    }

    public Coordinates() {

    }

    public void setX(long x) {
        this.x = x;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public long getX() {
        return x;
    }

    public Float getY() {
        return y;
    }

    @Override
    public String toString(){
        return "[x = " + x + ", y = " + y + "]" ;
    }

    @Override
    public int hashCode(){
        return Objects.hash(x, y);
    }
}

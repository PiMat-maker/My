package ru.ifmo.se.lab6.collection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
/**
 * Объекты класса {@code Product} используются для заполнения коллекции в классе {@code Main}.
 *
 */
public class Product implements Comparable<Product>, Serializable {
    /**
     * Время создания класса (первого объекта класса).
     */
    private static final ZonedDateTime CREATION_CLASS_DATE;

    static {
        ZoneId zone = ZoneId.of("Europe/Moscow");
        CREATION_CLASS_DATE = Instant.now().atZone(zone);
    }

    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private float price; //Значение поля должно быть больше 0
    private Long manufactureCost; //Поле не может быть null
    private UnitOfMeasure unitOfMeasure; //Поле не может быть null
    private Person owner; //Поле не может быть null

    /**
     * Разбивает строку на элементы, в качестве разделителя - запятая.
     * Заполняет получившимися элементами поля объекта.
     *
     * @param str Строка, содержащая все поля создаваемого объекта, разделённые запятыми.
     */


    public Product(String str) {
        this(str.split(", "));

    }

    public Product(String[] s) {
        Instant instant = Instant.now();
        Coordinates coordinates = new Coordinates(Long.parseLong(s[1]), Float.parseFloat(s[2]));
        setId(Math.abs(UUID.randomUUID().hashCode()));
        setName(s[0]);
        setCoordinates(coordinates);
        setCreationDate(instant);
        setPrice(Float.parseFloat(s[3]));
        setManufactureCost(Long.parseLong(s[4]));
        setUnitOfMeasure(UnitOfMeasure.valueOf(s[5]));
        Person owner = new Person(s[6], Float.parseFloat(s[7]), Color.valueOf(s[8]), Color.valueOf(s[9]), Country.valueOf(s[10]));
        setOwner(owner);
    }

    public Product() {
        name = "";
        price = 0;
        setId(Math.abs(UUID.randomUUID().hashCode()));
        coordinates = new Coordinates();
        owner = new Person();
        setCreationDate(Instant.now());
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCreationDate(Instant instant) {
        ZoneId zone = ZoneId.of("Europe/Moscow");
        this.creationDate = instant.atZone(zone);
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public static ZonedDateTime getCreateTime() {
        return CREATION_CLASS_DATE;
    }

    public void setManufactureCost(Long manufactureCost) {
        this.manufactureCost = manufactureCost;
    }

    public Long getManufactureCost() {
        return manufactureCost;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getPrice() {
        return price;
    }

    public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public Person getOwner() {
        return this.owner;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    /**
     * Сравнивает два объекта класса {@code Product} по цене.
     *
     * @param other Объект, с которым сравнивается текущий экземпляр.
     */
    @Override
    public int compareTo(Product other) {
        return Float.compare(this.price, other.price);
    }

    @Override
    public String toString() {
        return "[id = " + id + ", name = " + name + ", coordinates = " + coordinates +
                ", date = " + creationDate + ",\nprice  = " + price + ", cost = " +
                manufactureCost + ", unit of measure = " + unitOfMeasure + ", owner = " + owner + "]\n\n";
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, price, manufactureCost, unitOfMeasure, owner);
    }

    //Чтобы сравнить два объекта по размеру достаточно сравнить поля: Product.name и Owner.name - у двух объектов,
    //так как остальные поля занимают почти одинаковый объём памяти.
    //Понятно, что в данном способе изначально заложена погрешность, зато способ простой.
    public int getSizeToCompare() {
        int sizeOfProductName = this.name.length();
        int sizeOfOwnerName = this.getOwner().getName().length();
        return (sizeOfOwnerName + sizeOfProductName);
    }

    public static void main(String[] args) throws IOException {
        //Проверка правильности сравнения размера объёма, занимаемой объектами Product, памяти
        //Метод с сериализацией тоже не является точным, но в данном случае его можно использовать,
        //учтя небольшую погрешность измерений.
        //Результаты показывают, что разница между созданными объектами примерно одинакова.
        Product first0 = new Product();
        ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
        ObjectOutputStream objectStream = new ObjectOutputStream(byteArr);
        objectStream.writeObject(first0);
        objectStream.close();
        System.out.println(byteArr.size());

        Product first1 = new Product();
        first1.setName("First");
        first1.getOwner().setName("First");
        byteArr = new ByteArrayOutputStream();
        objectStream = new ObjectOutputStream(byteArr);
        objectStream.writeObject(first1);
        objectStream.close();
        System.out.println(byteArr.size());

        Product first2 = new Product();
        first2.setName("FirstFirst");
        first2.getOwner().setName("FirstFirst");
        byteArr = new ByteArrayOutputStream();
        objectStream = new ObjectOutputStream(byteArr);
        objectStream.writeObject(first2);
        objectStream.close();
        System.out.println(byteArr.size());

        Product first10 = new Product();
        first10.setName("FirstFirstFirstFirstFirstFirstFirstFirstFirstFirst");
        first10.getOwner().setName("FirstFirstFirstFirstFirstFirstFirstFirstFirstFirst");
        byteArr = new ByteArrayOutputStream();
        objectStream = new ObjectOutputStream(byteArr);
        objectStream.writeObject(first10);
        objectStream.close();
        System.out.println(byteArr.size());

        System.out.println(first0.getSizeToCompare());
        System.out.println(first1.getSizeToCompare());
        System.out.println(first2.getSizeToCompare());
        System.out.println(first10.getSizeToCompare());
    }
}

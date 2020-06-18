package ru.ifmo.se.lab6.server;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import ru.ifmo.se.lab6.collection.*;
import ru.ifmo.se.lab6.server.command.*;

/**
 * receiver - приёмник комманд
 */
public class ProductCollection {
    private final HashSet<Product> products;
    private final Time time;

    public ProductCollection(HashSet<Product> products) {
        this.products = products;
        time = new Time();
    }

    public ProductCollection() {
        this(new HashSet<>());
    }

    public Set<Product> getProducts() {
        return products;
    }

    public Time getTime() {
        return time;
    }

    public String info() {
        return "Тип коллекции ("
                + products.getClass().getName().substring(products.getClass().getName().lastIndexOf(".")+1)
                + "), дата инициализации (" + this.getTime()
                + "), количество элементов (" + products.size() + ").\n";
    }

    public String show() {
        String show = products.stream()
                .sorted(Comparator.comparing(Product::getSizeToCompare))
                .map(Product::toString)
                .collect(Collectors.joining());
        return show + "Было выведено " + products.size() + " элемент(-а/-ов) коллекции.\n";
    }

    public String add(String[] fields) {
        try {
            products.add(new Product(fields));
            return "Продукт успешно добавлен в коллекцию. \n";
        }
        catch(NullPointerException e) {
            return "Ошибка: некорректные данные. Продукт не был добавлен в коллекцию. \n";
        }
    }

    public String update(int id, String[] fields) {
        boolean check = products.stream()
                .map(Product::getId).anyMatch(k -> k == id);
        if (!check)
            return "Ошибка: элемен с данным id: не найден. \n";
        this.removeById(id);
        List<Product> p = new ArrayList<>(products);
        p.add(new Product(fields));
        p.get(p.size() - 1).setId(id);
        products.add(p.get(p.size() - 1));
        return "Элемент с данным id: " + id + " - изменён. \n";
    }

    public String removeById(int id) {
        int size = products.size();
        products.removeIf(product -> product.getId().equals(id));
        if (products.size() != size) {
            return "Элемент удалён.\n";
        }
        else {
            return "Элемент не найден.\n";
        }
    }

    public String clear() {
        products.clear();
        return "Коллекция очищена.\n";
    }

    public String addIfMax(String fields) {
        Product productAdd = new Product(fields);
        int size = products.size();
        Optional<Product> productMax = products.stream()
                .max(Comparator.comparing(Product::getPrice).thenComparing(Product::getOwner));
        productMax.ifPresentOrElse((max -> {if (productAdd.compareTo(max) > 0) products.add(productAdd);}),
                () -> products.add(productAdd));
        if (products.size() > size) {
            return "Элемент добавлен в коллекцию.\n";
        }
        else {
            return "Данный элемент не превышает максимальный элемент в коллекции.\n";
        }
    }

    public String addIfMin(String fields) {
        Product productAdd = new Product(fields);
        int size = products.size();
        Optional<Product> productMin = products.stream()
                .min(Comparator.comparing(Product::getPrice).thenComparing(Product::getOwner));
        productMin.ifPresentOrElse((min -> { if (productAdd.compareTo(min) < 0) products.add(productAdd); }),
                () -> products.add(productAdd));
        if (products.size() > size) {
            return "Элемент добавлен в коллекцию.\n";
        }
        else {
            return "Данный элемент не меньше минимального элемента в коллекции.\n";
        }
    }

    public String removeGreater(String fields) {
        Product greater = new Product(fields);
        int size = products.size();
        products.stream().filter(it -> it.compareTo(greater) > 0)
                .forEach(products::remove);
        size -= products.size();
        return "Было удалено " + size + " элемент(-а/ов) из коллекции.\n";
    }

    public String removeAllByUnitOfMeasure(UnitOfMeasure unit) {
        int size = products.size();
        products.stream()
                .filter(it -> it.getUnitOfMeasure() == unit)
                .forEach(products::remove);
        size -= products.size();
        return "Было удалено " + size + " элемент(-а/ов) из коллекции.\n";
    }

    public String getMaxByOwner() {
        Optional<Person> max = products.stream().map(Product::getOwner).max(Person::compareTo);
        if (max.isPresent()) {
            Optional<Product> result = products.stream()
                    .filter(it -> it.getOwner().equals(max.get()))
                    .findAny();
            return result.toString() + "\n";
        }
        else
            return "В коллекции нет ни одного элемента.\n";
    }

    public String getUnitOfMeasureDesc() {
        Iterator iterator = UnitOfMeasure.enumsList().iterator();
        String result = UnitOfMeasure.enumsList()
                .stream()
                .sorted(Comparator.comparing(String::length).reversed())
                .collect(Collectors.joining(", "));
        return result + ".\n";
    }

    public static void main(String[] args) {
        ProductCollection productCollection = new ProductCollection();
        productCollection.getProducts().add(new Product());
        productCollection.getProducts().add(new Product());
        Iterator<Product> iterator = productCollection.products.iterator();
        System.out.println(productCollection.show());
        System.out.println(productCollection.removeById(iterator.next().getId()));
        productCollection.getProducts().add(new Product());
        iterator = productCollection.products.iterator();
        System.out.println(productCollection.show());

    }
}
class Time {
    private ZoneId zone;
    private ZonedDateTime creationDate;

    public ZonedDateTime getCreationDate() {
        return this.creationDate;
    }

    public Time() {
        zone = ZoneId.of("Europe/Moscow");
        this.creationDate = Instant.now().atZone(this.zone);
    }

    public String toString() {
        return this.creationDate.toString();
    }
}



    /*

    *//**
     * Данный метод используется для того, чтобы считать сначала имя файла,
     * а потом выполнить последовательно команды, записанные в нём.
     * @param products Коллекция, над которой выполняются команды.
     * @param input Поток ввода, из которого берутся данные, запрашиваемые командой.
     * @param mode Режим выполнения команд: интерактивный, если <bold>false</bold>, и скрипт, если <bold>true</bold>
     * @return Выводит результаты команд или сообщения об ошибках при некорректной записи команд в скрипте.
     *//*
    private static String execute_script(Collection<com.company.Product> products, Scanner input, boolean mode) {
        if (!mode) { System.out.print("Введите имя файла, содержащего скрипт:\n$"); }
        else { input.nextLine(); }
        String file = "";
        if (input.hasNextLine()) file = input.nextLine();
        Scanner fileScript;
        while (true) {
            try {
                fileScript = new Scanner(new FileReader(file));
            }
            catch (FileNotFoundException e) {
                if (mode) return "Ошибка: файл не найден";
                System.out.print("Файл не найден. Введите имя файла:\n$");
                file = input.nextLine();
                continue;
            }
            break;
        }
        if (!com.company.Command.FileSet.fileSet.add(file)) {
            return "Ошибка: вызван открытый файл.";
        }
        System.out.println(".......................Скрипт выполняется.......................");
        Map<String, com.company.CommandInterface> command_map = com.company.Command.makeMapCommands();
        while (fileScript.hasNextLine()) {
            String com = fileScript.next();
            if (com.equals("exit")) {
                com.company.Command.FileSet.fileSet.remove(file);
                break;
            }
            com.company.Command.exec((command_map.getOrDefault(com, command_map.get("error"))), products, fileScript, true);
        }
        fileScript.close();
        return ".......Результат выполнения скрипта был выведен на экран........";
    }


    private static String print_field_descending_unit_of_measure(Collection<com.company.Product> products, Scanner input, boolean mode) {

    }
}*/

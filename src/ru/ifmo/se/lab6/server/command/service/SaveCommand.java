package ru.ifmo.se.lab6.server.command.service;

import com.google.gson.Gson;
import ru.ifmo.se.lab6.server.ProductCollection;
import ru.ifmo.se.lab6.server.Server;
import ru.ifmo.se.lab6.server.command.ServerCommand;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;

public class SaveCommand implements ServerCommand {
    private final ProductCollection productCollection;
    private File file;

    public SaveCommand(ProductCollection productCollection, File file) {
        this.productCollection = productCollection;
        this.file = file;
    }

    @Override
    public String execute(String... args) {
        try {
            if (file.canWrite()) {
                FileWriter out = new FileWriter(file);
                String line = productCollection.getProducts().toString();
                System.out.println(line);
                line = new Gson().toJson(line);
                System.out.println(line);
                out.write(line);
                out.flush();
                out.close();
            }
            else {
                String absolutePath = file.getAbsolutePath();
                int index;
                if ((index = absolutePath.lastIndexOf("\\")) != -1) {
                    absolutePath = absolutePath.substring(0, index) + "\\CollectionApp.txt";
                }
                else {
                    index = absolutePath.lastIndexOf("/");
                    absolutePath = absolutePath.substring(0, index) + "/CollectionApp.txt";
                }
                Path path = Paths.get(absolutePath);
                FileWriter out = new FileWriter(path.toFile());
                String line = new Gson().toJson(productCollection.getProducts().toString());
                out.write(line);
                out.flush();
                out.close();
            }
        } catch(IOException e) {
            System.out.println("Ошибка: не удалось сохранить коллекцию в файл.");
        }
        return null;
    }


    public static void main(String[] args) {
        Path path = Paths.get("output.txt");
        File file = path.toFile();
        String absolutePath = file.getAbsolutePath();
        int index = absolutePath.lastIndexOf("\\");
        if (index != -1) {
            absolutePath = absolutePath.substring(0, index) + "\\CollectionApp.txt";
        }
        else if ((index = absolutePath.lastIndexOf("/")) != -1){
            absolutePath = absolutePath.substring(0, index) + "/CollectionApp.txt";
        }
        else
            absolutePath = "CollectionApp.txt";
        path = Paths.get(absolutePath);
        System.out.println(path.toString());
    }
}


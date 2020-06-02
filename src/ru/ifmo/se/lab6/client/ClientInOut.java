package ru.ifmo.se.lab6.client;

import java.io.*;
import java.util.Scanner;

public class ClientInOut {
    private PrintStream out;
    private Scanner in;

    public ClientInOut(OutputStream out, InputStream in) {
        this.out = new PrintStream(out);
        this.in = new Scanner(in);
    }

    public ClientInOut() {
        out = System.out;
        in = new Scanner(System.in);
    }

    public void writeLine(String msg) {
        out.print(msg);
    }

    public String readLine() {
        return in.nextLine();
    }

    public boolean hasNextLine() {
        return in.hasNextLine();
    }

    public void close() {
        out.close();
        in.close();
    }
}

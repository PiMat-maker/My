package ru.ifmo.se.lab6;

import java.io.Serializable;

public enum ServiceCommand implements Serializable {

    INPUT,
    FILE_ERROR,
    DATA_ERROR,
    EXIT,
    OKAY,
    READY,
    ERROR_COMMAND,
    ERROR,
    DISCONNECT;

    public static void main(String[] args) {
        System.out.println(ServiceCommand.ERROR_COMMAND.toString());
        System.out.println(ServiceCommand.ERROR_COMMAND.ordinal());
    }
}

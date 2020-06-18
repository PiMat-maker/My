package ru.ifmo.se.lab6.server;

import ru.ifmo.se.lab6.client.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

public class TestUnBlocking {
    private DatagramChannel channel = DatagramChannel.open();
    private SocketAddress localAddress = new InetSocketAddress(3132);
    private byte[] bytes = new byte[12000];
    private ByteBuffer buffer = ByteBuffer.wrap(bytes);
    private SocketAddress remoteAddress;

    public TestUnBlocking() throws IOException {
    }

    public static void main(String[] args) throws IOException {
        TestUnBlocking test = new TestUnBlocking();
        test.channel.configureBlocking(false);
        test.channel.bind(test.localAddress);
        String serviceCommand = "";
        int i = System.in.available();
        while(true) {
            System.out.println("Ожидаю ввод массива");
            test.remoteAddress = test.receiveByteArr();
            System.out.println("Введите команду");
            while(test.buffer.limit() == 0 || test.buffer.limit() == test.buffer.capacity()) {
                if (System.in.available() != 0) {
                    int len = System.in.read(test.bytes, 0, System.in.available());
                    serviceCommand = new String(test.bytes, 0, len-1);
                    if (serviceCommand.equals("exit")) {
                        System.out.println("Завершение программы");
                        test.channel.close();
                        System.exit(0);
                    }
                    else {
                        System.out.println("Данная команда не найдена");
                        System.out.println("Введите команду");
                    }
                }
                test.remoteAddress = test.receiveByteArr();
            }
            System.out.println("Получил массив (строку)" + new String(test.bytes, 0, test.buffer.limit()));
        }
    }

    private SocketAddress receiveByteArr() throws IOException {
        this.buffer.clear();
        SocketAddress remoteAddr = channel.receive(buffer);
        buffer.flip();
        return remoteAddr;
    }
}

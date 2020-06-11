package ru.ifmo.se.lab6.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

public class Test {
    private DatagramChannel channel = DatagramChannel.open();
    private SocketAddress remoteAddress = new InetSocketAddress(InetAddress.getLocalHost(), 3132);
    private byte[] bytes = new byte[12000];
    private ByteBuffer buffer = ByteBuffer.wrap(bytes);

    public Test() throws IOException {
    }

    public static void main(String[] args) throws IOException {
        Test test = new Test();
        test.channel.connect(test.remoteAddress);
        test.channel.configureBlocking(true);
        test.channel.socket().setSoTimeout(10000);
        Scanner in = new Scanner(System.in);
        while(true) {
            System.out.println("Введите строку");
            String string = in.nextLine();
            test.bytes = string.getBytes();
            test.buffer.put(test.bytes).flip();
            test.sendByteArr();
            System.out.println("Ну а пока он отправляется, ты можешь повводить всякий бред");
            System.out.println("Отправил, наконец");
        }
    }

    private void sendByteArr() throws IOException {
        channel.send(buffer, remoteAddress);
        buffer.clear();
    }
}

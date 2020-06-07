package ru.ifmo.se.lab6.client;

import ru.ifmo.se.lab6.ServiceCommand;
import ru.ifmo.se.lab6.ServiceManager;
import ru.ifmo.se.lab6.client.command.*;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Client implements Runnable {

    private static int capacity = 65536;
    private byte[] bytes = new byte[capacity];
    private SocketAddress remoteAddress;
    private DatagramSocket socket;
    private ServiceManager serviceManager;

//    private BlockingQueue<String[]> queue = new LinkedBlockingQueue<>();

    public Client() {
        try {
            remoteAddress = new InetSocketAddress(InetAddress.getLocalHost(), 3131);
            socket = new DatagramSocket();
            socket.setSoTimeout(10000);
            socket.connect(remoteAddress);
        } catch (UnknownHostException e) {
            System.out.println("Ошибка: неизвестный хост.");
        } catch (SocketException e) {
            System.out.println("Ошибка: не удалось создать сокет.");
        }
    }

    public Client(InetAddress inetAddress, int port) {
        try {
            remoteAddress = new InetSocketAddress(inetAddress, port);
            socket = new DatagramSocket();
            socket.setSoTimeout(10000);
        } catch (SocketException e) {
            System.out.println("Ошибка: не удалось создать сокет.");
        }
    }

    @Override
    public void run() {
        ClientInOut inOut = new ClientInOut(System.out, System.in);
        Application application = new Application();
        application.register("add", new AddCommand(inOut));
        application.register("exit", new ExitCommand(inOut));
        application.register("remove_all_by_unit_of_measure", new RemoveAllByUnitOfMeasure(inOut));
        application.register("remove_by_id", new RemoveByIdCommand(inOut));
        application.register("update", new UpdateCommand(inOut));
        serviceManager = new ServiceManager(ServiceCommand.FILE_ERROR);
        try {
            ServiceCommand check = serviceManager.getCode();
            Object obj;
            Scanner in = new Scanner(System.in);
            while (true) {
                try {
                    if (!check.equals(ServiceCommand.INPUT))
                        System.out.print(serviceManager.getMsg());
                    switch (check) {
                        case FILE_ERROR:
                            //String fileName;
                            //System.out.print("Введите имя файла с коллекцией, лежащего на сервере:\n$");
                            //fileName = "Ghbdtn";//"input.json";
                            //bytes = fileName.getBytes();
                            //try{
                            //    //socket.setSoTimeout(10000);
                            //    for (int i = 0; i<5; i+=1) {
                            //        Thread.sleep(i*1000);
                            //        sendByteArr();
                            //    }
                            //    receiveObj();
//
                            //} catch (SocketException e) {
                            //    System.out.println("Ошибка: не удалось создать сокет.");
                            //} catch (InterruptedException e) {
                            //    e.printStackTrace();
                            //}
                            //String[] s = {"Ghbdtn"};
                            for (int i=0;i<1;++i){
                                Thread.sleep(i*1000);
                                sendObj("Ghbdtn");
                            }
                            break;

                        case OKAY:
                            break;

                        case DATA_ERROR:
                            bytes = readNotNullLine(in).getBytes();
                            sendByteArr();
                            break;

                        case EXIT:
                            sendObj("exit");
                            application.execute("exit", false);
                            break;

                        case ERROR_COMMAND:
                            break;

                        case INPUT:
                            String[] args = application.execute(serviceManager.getMsg(), false);
                            sendObj(args);
                            break;

                        case READY:
                            String commandName = "";
                            commandName = readNotNullLine(in);

                            if (application.getCommandHelperMap().containsKey(commandName)
                                    && commandName.substring(0, 2).equals("add"))
                                args = application.execute("add", false);
                            else if (commandName.equals("Nope"))
                                args = application.execute("exit", false);
                            else
                                args = application.execute(commandName, false);
                            bytes = commandName.getBytes();
                            sendByteArr();
                            sendObj(args);
                            break;

                        case DISCONNECT:
                            socket.disconnect();
                            socket.close();
                            application.execute("exit", false);
                    }
                    obj = receiveObj();
                    serviceManager = (ServiceManager) obj;
                    check = serviceManager.getCode();
                } catch (ClassCastException e) {
                    System.out.println("Ошибка: получен неизвестный объект");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка: сервер недоступен.");
            //} catch ( e) {
            //    System.out.print("Ошибка: сервер недоступен.");
        }
        finally {
            socket.close();
            inOut.close();
        }
    }

    private String readNotNullLine(Scanner in) {
        try {
            String line = in.nextLine();
            Pattern pattern = Pattern.compile("\\s+");
            Matcher matcher = pattern.matcher(line);
            while (matcher.matches()) {
                System.out.print("Введите непустую строку:\n$");
                line = in.nextLine();
                matcher.reset(line);
            }
            return line;
        }
        catch(NoSuchElementException e){
            try {
                sendObj("exit");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println("Клиент закончил работу");
            return "Nope";
        }
    }

    private void sendByteArr() throws IOException {
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, remoteAddress);
        socket.send(packet);
    }

    private SocketAddress receiveByteArr() throws IOException {
        bytes = new byte[capacity];
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
        socket.receive(packet);
        bytes = Arrays.copyOfRange(bytes, 0, packet.getLength());
        return packet.getSocketAddress();
    }

    private Object receiveObj() throws IOException, ClassNotFoundException {
        receiveByteArr();
        ByteArrayInputStream byteArrIn = new ByteArrayInputStream(bytes);
        ObjectInputStream objIn = new ObjectInputStream(byteArrIn);
        Object obj = objIn.readObject();
        objIn.close();
        return obj;
    }

    private void sendObj(Object obj) throws IOException {
        ByteArrayOutputStream byteArrOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteArrOut);
        objOut.writeObject(obj);
        bytes = byteArrOut.toByteArray();
        DatagramPacket sendPacket = new DatagramPacket(bytes, bytes.length, remoteAddress);
        socket.send(sendPacket);
        objOut.close();
    }



    public static void main(String[] args) {
        Client client = new Client();
        client.run();
        /*client.bytes = "input.json".getBytes();
//        Arrays.fill(client.bytes, (byte) 0);
        System.out.println("Отправляю массив.");
        client.sendByteArr();
        System.out.println("Отправил массив.\nОжидаю массив.");
        client.receiveByteArr();
        System.out.println("Получил массив.");
        System.out.println(new String(client.bytes));*/
    }

}
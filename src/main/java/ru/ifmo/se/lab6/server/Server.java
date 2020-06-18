package ru.ifmo.se.lab6.server;

import com.google.gson.Gson;
import ru.ifmo.se.lab6.ServiceCommand;
import ru.ifmo.se.lab6.ServiceManager;
import ru.ifmo.se.lab6.collection.Product;
import ru.ifmo.se.lab6.server.command.*;
import ru.ifmo.se.lab6.server.command.service.ExitCommand;
import ru.ifmo.se.lab6.server.command.service.SaveCommand;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server implements Runnable{

    private final static Logger logger = LogManager.getLogger("ru.ifmo.se.lab6.server.Server");
    private static int capacity = 65536;
    private byte[] bytes = new byte[capacity];
    private SocketAddress localAddress;
    private SocketAddress remoteAddress;
    private DatagramChannel channel;
    private final List<ServiceManager> serviceManager = new ArrayList<>();
    private ByteBuffer buffer = ByteBuffer.allocate(capacity);
    private final Application application = new Application();

    public Server() {
        this(3131);
    }

    public Server(int port) {
        if (port < 0 || port > 65535) {
            System.out.println("Ошибка: недопустимый номер порта. Использован порт по умолчанию: 3131.");
            logger.error("Ошибка: недопустимый номер порта. Использован порт по умолчанию: 3131.");
            localAddress = new InetSocketAddress(3131);
        }
        else if (port < 1024) {
            System.out.println("Предупреждение: нерекомендуемый номер порта.");
            logger.warn("Предупреждение: нерекомендуемый номер порта.");
        }
        else
            localAddress = new InetSocketAddress(port);

        // Заполнение списка менеджера сервисных команд
        // номер индекса сервисной команды в списке соответсвтует
        // результату выполнения команды ServiceCommand.<имя команды>.ordinal()
        serviceManager.add(new ServiceManager(ServiceCommand.INPUT));
        serviceManager.add(new ServiceManager(ServiceCommand.FILE_ERROR));
        serviceManager.add(new ServiceManager(ServiceCommand.DATA_ERROR));
        serviceManager.add(new ServiceManager(ServiceCommand.EXIT, "До новых встреч.\n"));
        serviceManager.add(new ServiceManager(ServiceCommand.OKAY));
        serviceManager.add(new ServiceManager(ServiceCommand.READY));
        serviceManager.add(new ServiceManager(ServiceCommand.ERROR_COMMAND,
                "Ошибка: данная команда не найдена!\nИспользуйте команду 'help' для вывода справки.\n"));
        serviceManager.add(new ServiceManager(ServiceCommand.ERROR));
        serviceManager.add(new ServiceManager(ServiceCommand.DISCONNECT, "Ошибка: время ожидания сервером истекло.\n"));

        try {
            channel = DatagramChannel.open();
            channel.bind(localAddress);
            channel.socket().setSoTimeout(10000);
        } catch (IOException e) {
            System.out.println("Ошибка: не удалось открыть канал.");
            logger.fatal("Не удалось открыть канал");
        }

        application.registerServiceCmd("exit", new ExitCommand());
    }

    class ProcessorHook extends Thread{

        @Override
        public void run() {
            try {
                sendObj("Stopped");
                logger.info("Выключение серевера");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        logger.info("Начало работы сервера");
        boolean clientStatus = false;
        boolean serverStatus = false;
        Runtime.getRuntime().addShutdownHook(new ProcessorHook());

        while (true) {
            try {
                remoteAddress = receiveByteArr();
                buffer.get(bytes, 0, buffer.limit());
                String commandName = new String(bytes, 0, buffer.limit());
                buffer.clear();
                if (commandName.contains("exit")){
                    logger.info("Клиент отключился");
                    clientStatus = false;
                    application.executeServiceCmd("save");
                    channel.disconnect();
                    continue;
                }
                if (commandName.contains("Ghbdtn") && clientStatus){continue;}
                if (commandName.contains("Ghbdtn") && (!serverStatus || !clientStatus)) {
                    if (channel.isConnected())
                        channel.disconnect();
                    serverStatus = true;
                    clientStatus = true;
                    String file = "input.json";
                    Path filePath;
                    try {
                        filePath = Paths.get(file);
                    } catch (InvalidPathException e) {
                        serviceManager.get(ServiceCommand.FILE_ERROR.ordinal())
                                .setMsg("Ошибка: некорректное имя файла. ");
                        throw new FileNotFoundException();
                    }
                    if (Files.exists(filePath) && !Files.isReadable(filePath)) {
                        serviceManager.get(ServiceCommand.FILE_ERROR.ordinal())
                                .setMsg("Ошибка: отсутствие необходимых прав доступа к файлу. ");
                        throw new FileNotFoundException();
                    }

                    ProductCollection productCollection = new ProductCollection(downloadCollection(channel, file));
                    if (commandName.contains("Ghbdtn")) {
                        serviceManager.get(ServiceCommand.OKAY.ordinal()).setMsg("Коллекция успешно заполнена.\n");
                        sendObj(serviceManager.get(ServiceCommand.OKAY.ordinal()));
                        logger.info("Успешно загружена коллекция");
                    }


                    application.register("add", new AddCommand(productCollection));
                    application.register("add_if_max", new AddIfMaxCommand(productCollection));
                    application.register("add_if_min", new AddIfMinCommand(productCollection));
                    application.register("clear", new ClearCommand(productCollection));
                    application.register("help", new HelpCommand(application));
                    application.register("info", new InfoCommand(productCollection));
                    application.register("max_by_owner", new MaxByOwnerCommand(productCollection));
                    application.register("print_unit_of_measure", new PrintUnitOfMeasureDescCommand(productCollection));
                    application.register("remove_all_by_unit_of_measure", new RemoveAllByUnitOfMeasureCommand(productCollection));
                    application.register("remove_by_id", new RemoveByIdCommand(productCollection));
                    application.register("remove_greater", new RemoveGreaterCommand(productCollection));
                    application.register("execute_script", new ScriptCommand());
                    application.register("show", new ShowCommand(productCollection));
                    application.register("update", new UpdateCommand(productCollection));

                    application.registerServiceCmd("save", new SaveCommand(productCollection, filePath.toFile()));



                    if (commandName.contains("Ghbdtn")){
                        serviceManager.get(ServiceCommand.READY.ordinal()).setMsg("Введите команду ('help' для вывода справки):\n$");
                        sendObj(serviceManager.get(ServiceCommand.READY.ordinal()));
                    }
                    //if (commandName.contains("Ghbdtn")){continue;}
                    continue;
                }
                if (serverStatus){
                    if (!clientStatus){continue;}
                    try {
                        //System.out.println(1);
                        logger.info("Обработка команды");
                        channel.configureBlocking(false);
                        //serviceManager.get(ServiceCommand.READY.ordinal()).setMsg("Введите команду ('help' для вывода справки):\n$");
                        //sendObj(serviceManager.get(ServiceCommand.READY.ordinal()));
                        //String commandName;
                        String[] args;
                        int i = 0;
                        //while (++i < 5 * Math.pow(10, 5) && (buffer.limit() == 0 || buffer.limit() == buffer.capacity())) {
                        //    if (System.in.available() != 0) {
                        //        int len = System.in.read(bytes, 0, System.in.available());
                        //        String serverCommand = new String(bytes, 0, len - 1);
                        //        if (serverCommand.equals("exit")) {
                        //            channel.disconnect();
                        //            channel.close();
                        //        }
                        //        System.out.println(application.executeServiceCmd(serverCommand));
                        //        System.out.print("Введите команду:\n$");
                        //    }
                        //    //receiveByteArr();
                        //}
                        if (i == 5 * Math.pow(10, 5)) {
                            throw new PortUnreachableException();
                        }
                        channel.configureBlocking(true);
                        //buffer.get(bytes, 0, buffer.limit());
                        //commandName = new String(bytes, 0, buffer.limit());
                        //buffer.clear();
                        //System.out.println(2);
                        args = (String[]) receiveObj();
                        //System.out.println(3);
                        //System.out.println(commandName);
                        String result = "";
                        boolean isExec = true;
                        while (isExec) {
                            if (args != null)
                                result = application.execute(commandName, args);
                            else
                                result = application.execute(commandName);
                            if (result.contains(",")) {
                                try {
                                    String[] arr = result.split(",");
                                    ServiceCommand send = ServiceCommand
                                            .valueOf(arr[0]);
                                    String cmdName = arr[1];
                                    serviceManager.get(send.ordinal()).setMsg(cmdName);
                                    sendObj(serviceManager.get(send.ordinal()));
                                    args = (String[]) receiveObj();
                                } catch (IllegalArgumentException e) {
                                    isExec = false;
                                }
                            } else
                                isExec = false;
                            if (result.equals("ERROR_COMMAND")) {
                                sendObj(serviceManager.get(ServiceCommand.valueOf(result).ordinal()));
                                result = "";
                                logger.warn("Неправильная команда");
                            }
                        }
                        //System.out.println(result);
                        logger.info("Отправка результата");
                        serviceManager.get(ServiceCommand.OKAY.ordinal()).setMsg(result);
                        sendObj(serviceManager.get(ServiceCommand.OKAY.ordinal()));
                        serviceManager.get(ServiceCommand.READY.ordinal()).setMsg("Введите команду ('help' для вывода справки):\n$");
                        sendObj(serviceManager.get(ServiceCommand.READY.ordinal()));

                    } catch (PortUnreachableException e) {
                        sendObj(serviceManager.get(ServiceCommand.DISCONNECT.ordinal()));
                        channel.configureBlocking(true);
                        channel.disconnect();
                        logger.error("Недоступный порт");
                        throw new IOException(e);
                    } catch (IOException | ClassNotFoundException e) {
                        //e.printStackTrace();
                        serviceManager.get(ServiceCommand.ERROR.ordinal()).setMsg("Ошибка: проблемы на сервере.");
                        sendObj(serviceManager.get(ServiceCommand.ERROR.ordinal()));
                        logger.error("Проблема на сервере");
                    }
                }
            } catch (IOException e) {
                System.out.println("\nОшибка: проблемы с доступом к клиенту. Отключение...");
                logger.error("Нет доступа к клиенту");
                break;
            } //catch (ClassNotFoundException e) {
            //  e.printStackTrace();
            //}


            logger.info("Успешное выполнение команды");
        }
    }

    private SocketAddress receiveByteArr() throws IOException {
        buffer.clear();
        SocketAddress remoteAddr = channel.receive(buffer);
        buffer.flip();
        return remoteAddr;
    }

    private void sendByteArr() throws IOException {
        channel.send(buffer, remoteAddress);
        buffer.clear();
    }

    private Object receiveObj() throws IOException, ClassNotFoundException {
        logger.info("Прием сообщения от клиента");
        receiveByteArr();
        buffer.get(bytes, 0, buffer.limit());
        ByteArrayInputStream byteArrayIn = new ByteArrayInputStream(bytes);
        ObjectInputStream objIn = new ObjectInputStream(byteArrayIn);
        Object obj = objIn.readObject();
        objIn.close();
        buffer.clear();
        return obj;
    }

    private void sendObj(Object obj) throws IOException {
        logger.info("Отсылка сообщения клиенту");
        ByteArrayOutputStream byteArrOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteArrOut);
        objOut.writeObject(obj);
        channel.send(ByteBuffer.wrap(byteArrOut.toByteArray()), remoteAddress);
        objOut.close();
    }

    private HashSet<Product> downloadCollection(DatagramChannel channel, String file) throws IOException {
        //System.out.println("Сервер запущен");
        //boolean running = true;
        Scanner fileScanner;
        //Path filePath;
        //обработка ввода имени файла
        //отправил объект ServiceCommand, содержащий код ошибки и сообщение
        //System.out.println("Ожидаю ввода имени файла");
        logger.info("Ожидаю ввода имени файла");
        //System.out.print("Введите команду:\n$");
        channel.configureBlocking(false);
        //String serverCommand;
        //while (buffer.limit() == 0 || buffer.limit() == buffer.capacity()) {
        //    if (System.in.available() != 0) {
        //        int len = System.in.read(bytes, 0, System.in.available());
        //        serverCommand = new String(bytes, 0, len - 1);
        //        if (serverCommand.equals("exit")) {
        //            channel.disconnect();
        //            channel.close();
        //        }
        //        System.out.println(application.executeServiceCmd(serverCommand));
        //        //System.out.print("Введите команду:\n$");
        //    }
        //    remoteAddress = receiveByteArr();
        //}
        channel.socket().connect(remoteAddress);
        logger.info("Сформировано новое подключение");
        //sendObj("Receive");
        buffer.clear();
        channel.configureBlocking(true);
        //System.out.println("Получил имя файла");
        logger.info("Получил имя файла");
        while (true) {
            //buffer.get(bytes, 0, buffer.limit());
            //String file = new String(bytes, 0, buffer.limit());
            //buffer.clear();
            //serviceManager.get(ServiceCommand.FILE_ERROR.ordinal()).clearMsg();
            try {
                //try {
                //    filePath = Paths.get(file);
                //} catch (InvalidPathException e) {
                //    serviceManager.get(ServiceCommand.FILE_ERROR.ordinal())
                //            .setMsg("Ошибка: некорректное имя файла. ");
                //    throw new FileNotFoundException();
                //}
                //if (Files.exists(filePath) && !Files.isReadable(filePath)) {
                //    serviceManager.get(ServiceCommand.FILE_ERROR.ordinal())
                //            .setMsg("Ошибка: отсутствие необходимых прав доступа к файлу. ");
                //    throw new FileNotFoundException();
                //}
                try {
                    fileScanner = new Scanner(new FileReader(file));
                } catch (FileNotFoundException e) {
                    serviceManager.get(ServiceCommand.FILE_ERROR.ordinal())
                            .setMsg("Ошибка: файл не найден. ");
                    logger.error("Ошибка: файл не найден.");
                    throw new FileNotFoundException();
                }

            } catch (FileNotFoundException e) {
                serviceManager.get(ServiceCommand.FILE_ERROR.ordinal())
                        .appendMsg("Введите другое имя файла.\n");
                sendObj(serviceManager.get(ServiceCommand.FILE_ERROR.ordinal()));
                remoteAddress = receiveByteArr();
                continue;
            }
            break;
        }
        //sendObj(serviceManager.get(ServiceCommand.OKAY.ordinal()));
        //заполнение коллекции, отправил объект, содержащий код ошибки
        HashSet<Product> products = new HashSet<>();
        try {
            while (fileScanner.hasNextLine()) {
                String k = fileScanner.nextLine();
                String[] lines = new Gson().fromJson(k, String.class).split("\n\n");
                for (int i=0; i < lines.length - 1; ++i) {
                    if (lines[i].startsWith(",")){lines[i] = lines[i].replaceFirst(",", "");}
                    products.add(new Product(lines[i].split(",")));
                }
            }
            //sendObj(serviceManager.get(ServiceCommand.OKAY.ordinal()));
        } catch (Throwable e) {
            e.printStackTrace();
            serviceManager.get(ServiceCommand.DATA_ERROR.ordinal()).clearMsg();
            serviceManager.get(ServiceCommand.DATA_ERROR.ordinal())
                    .setMsg("Ошибка: некорректные данные.\nСоздать пустую коллекцию? (Да/Нет)\n$");
            sendObj(serviceManager.get(ServiceCommand.DATA_ERROR.ordinal()));
            logger.error("Ошибка: некорректные данные.");
            receiveByteArr();
            buffer.get(bytes, 0, buffer.limit());
            String cond = new String(bytes, 0, buffer.limit());
            buffer.clear();
            if (cond.equals("Да")) {
                products = new HashSet<>();
            } else {
                sendObj(serviceManager.get(ServiceCommand.EXIT.ordinal()));
                channel.disconnect();
                return products;
            }
        }
        return products;
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        while (true) {
            server.run();
        }
        /*System.out.println("Ожидаю массив.");
        server.remoteAddress = server.receiveByteArr();
        server.buffer.get(server.bytes, 0, server.buffer.limit()).flip();
        System.out.println("Получил сообщение " + new String(server.bytes, 0, server.buffer.limit()));
        server.bytes = "Okey".getBytes();
        server.buffer.clear().put(server.bytes).flip();
        System.out.println("Отправляю массив");
        server.sendByteArr();
        System.out.println("Отправил массив");*/
    }
}

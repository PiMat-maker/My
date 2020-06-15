package ru.ifmo.se.lab6.server;

import com.google.gson.Gson;
import ru.ifmo.se.lab6.ServiceCommand;
import ru.ifmo.se.lab6.ServiceManager;
import ru.ifmo.se.lab6.collection.Product;
import ru.ifmo.se.lab6.server.command.*;
import ru.ifmo.se.lab6.server.command.service.ExitCommand;
import ru.ifmo.se.lab6.server.command.service.SaveCommand;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.logging.*;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Server implements Runnable{

    private static int capacity = 65536;
    private byte[] bytes = new byte[capacity];
    private SocketAddress localAddress;
    private SocketAddress remoteAddress;
    private DatagramChannel channel;
    private final List<ServiceManager> serviceManager;
    private ByteBuffer buffer = ByteBuffer.allocate(capacity);
    private final Application application = new Application();
    Logger logger = Logger.getLogger(Server.class.getName());

    public Server() {
        this(3131);
    }

    public Server(int port) {
        if (port < 0 || port > 65535) {
            System.out.println("Ошибка: недопустимый номер порта. Использован порт по умолчанию: 3131.");
            localAddress = new InetSocketAddress(3131);
        }
        else if (port < 1024)
            System.out.println("Предупреждение: нерекомендуемый номер порта.");
        else
            localAddress = new InetSocketAddress(port);

        // Заполнение списка менеджера сервисных команд
        // номер индекса сервисной команды в списке соответсвтует
        // результату выполнения команды ServiceCommand.<имя команды>.ordinal()
        serviceManager = new ArrayList<>();
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
            logger.severe("Не удалось открыть канал");
        }

        application.registerServiceCmd("exit", new ExitCommand());
    }

    class ProcessorHook extends Thread{

        @Override
        public void run() {
            //try {
            //sendObj("Stopped");
            logger.info("Выключение серевера");
            //} catch (IOException e) {
            //    e.printStackTrace();
            //}
        }
    }

    @Override
    public void run() {
        logger.info("Начало работы сервера");
        boolean clientStatus = false;
        boolean serverStatus = false;
        Runtime.getRuntime().addShutdownHook(new ProcessorHook());


        try {
            ProductCollection productCollection = new ProductCollection(downloadCollection(channel));
            applyCommands(productCollection);
            channel.configureBlocking(false);
            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);


            while (true) {
                try {
                    selector.select();
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (!key.isValid()){
                            continue;
                        }
                        if (key.isReadable()) {
                            logger.info("Сформировано новое подключение");
                            DatagramChannel dc = (DatagramChannel) key.channel();

                            //if (dc.isConnected()){dc.disconnect();}
                            remoteAddress = receiveByteArr(dc);
                            buffer.get(bytes, 0, buffer.limit());
                            String commandName = new String(bytes, 0, buffer.limit());
                            buffer.clear();
                            //dc.socket().connect(remoteAddress);
                            //if (commandName.contains("Ghbdtn") && clientStatus == true){continue;}
                            //if (commandName.contains("Ghbdtn") && (serverStatus == false || clientStatus == false)) {
                            //if (channel.isConnected())
                            //    channel.disconnect();

                            //}
                            //if (serverStatus == true){
                            //    if (clientStatus == false){continue;}
                            try {
                                //System.out.println(1);
                                logger.info("Обработка команды");
                                //channel.configureBlocking(false);
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
                                //channel.configureBlocking(true);
                                //buffer.get(bytes, 0, buffer.limit());
                                //commandName = new String(bytes, 0, buffer.limit());
                                //buffer.clear();
                                //System.out.println(2);
                                args = (String[]) receiveObj(dc);
                                //System.out.println(3);
                                //System.out.println(commandName);
                                String result = "";
                                boolean isExec = true;
                                while (isExec) {
                                    if (args != null) {
                                        result = application.execute(commandName, args);
                                        application.executeServiceCmd("save");
                                    }
                                    else
                                        result = application.execute(commandName);
                                    if (result.contains(",")) {
                                        try {
                                            String[] arr = result.split(",");
                                            ServiceCommand send = ServiceCommand
                                                    .valueOf(arr[0]);
                                            String cmdName = arr[1];
                                            serviceManager.get(send.ordinal()).setMsg(cmdName);
                                            sendObj(serviceManager.get(send.ordinal()), dc);
                                            args = (String[]) receiveObj(dc);
                                        } catch (IllegalArgumentException e) {
                                            isExec = false;
                                        }
                                    } else
                                        isExec = false;
                                    if (result.equals("ERROR_COMMAND")) {
                                        sendObj(serviceManager.get(ServiceCommand.valueOf(result).ordinal()), dc);
                                        result = "";
                                        logger.log(Level.WARNING, "Неправильная команда");
                                    }
                                }
                                //System.out.println(result);
                                logger.info("Отправка результата");
                                serviceManager.get(ServiceCommand.OKAY.ordinal()).setMsg(result);
                                sendObj(serviceManager.get(ServiceCommand.OKAY.ordinal()), dc);
                                serviceManager.get(ServiceCommand.READY.ordinal()).setMsg("Введите команду ('help' для вывода справки):\n$");
                                sendObj(serviceManager.get(ServiceCommand.READY.ordinal()), dc);

                            } catch (PortUnreachableException e) {
                                sendObj(serviceManager.get(ServiceCommand.DISCONNECT.ordinal()), dc);
                                //channel.configureBlocking(true);
                                channel.disconnect();
                                logger.severe("Недоступный порт");
                                throw new IOException(e);
                            } catch (IOException | ClassNotFoundException e) {
                                e.printStackTrace();
                                serviceManager.get(ServiceCommand.ERROR.ordinal()).setMsg("Ошибка: проблемы на сервере.");
                                sendObj(serviceManager.get(ServiceCommand.ERROR.ordinal()), dc);
                                logger.severe("Проблема на сервере");
                            }
                            //}
                        }
                    }
                } catch (IOException e) {
                    System.out.println("\nОшибка: проблемы с доступом к клиенту. Отключение...");
                    logger.severe("Нет доступа к клиенту");
                    break;
                }
                //catch (ClassNotFoundException e) {
                //  e.printStackTrace();
                //}


                //System.out.println("yeah");
                logger.fine("Успешное выполнение команды");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SocketAddress receiveByteArr(DatagramChannel channel) throws IOException {
        buffer.clear();
        SocketAddress remoteAddr = channel.receive(buffer);
        buffer.flip();
        return remoteAddr;
    }

    private void sendByteArr(DatagramChannel channel) throws IOException {
        channel.send(buffer, remoteAddress);
        buffer.clear();
    }

    private Object receiveObj(DatagramChannel channel) throws IOException, ClassNotFoundException {
        logger.log(Level.INFO, "Прием сообщения от клиента");
        receiveByteArr(channel);
        System.out.println(buffer.toString());
        buffer.get(bytes, 0, buffer.limit());
        ByteArrayInputStream byteArrayIn = new ByteArrayInputStream(bytes);
        ObjectInputStream objIn = new ObjectInputStream(byteArrayIn);
        Object obj = objIn.readObject();
        objIn.close();
        buffer.clear();
        return obj;
    }

    private void sendObj(Object obj, DatagramChannel channel) throws IOException {
        logger.log(Level.INFO, "Отсылка сообщения клиенту");
        ByteArrayOutputStream byteArrOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteArrOut);
        objOut.writeObject(obj);
        channel.send(ByteBuffer.wrap(byteArrOut.toByteArray()), remoteAddress);
        objOut.close();
    }

    private HashSet<Product> downloadCollection(DatagramChannel channel) throws IOException {
        //serverStatus = true;
        //clientStatus = true;
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

        //ProductCollection productCollection = new ProductCollection(downloadCollection(dc, file));
        //serviceManager.get(ServiceCommand.OKAY.ordinal()).setMsg("Коллекция успешно заполнена.\n");
        //sendObj(serviceManager.get(ServiceCommand.OKAY.ordinal()));
        logger.fine("Успешно загружена коллекция");



        //if (commandName.contains("Ghbdtn")){
        //    serviceManager.get(ServiceCommand.READY.ordinal()).setMsg("Введите команду ('help' для вывода справки):\n$");
        //    sendObj(serviceManager.get(ServiceCommand.READY.ordinal()));
        //}
        //if (commandName.contains("Ghbdtn")){continue;}
        //continue;
        //System.out.println("Сервер запущен");
        //boolean running = true;
        Scanner fileScanner;
        //Path filePath;
        //обработка ввода имени файла
        //отправил объект ServiceCommand, содержащий код ошибки и сообщение
        //System.out.println("Ожидаю ввода имени файла");
        logger.info("Ожидаю ввода имени файла");
        //System.out.print("Введите команду:\n$");
        //channel.configureBlocking(false);
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
        //channel.socket().connect(remoteAddress);
        //sendObj("Receive");
        buffer.clear();
        //channel.configureBlocking(true);
        //System.out.println("Получил имя файла");
        logger.fine("Получил имя файла");
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
                    logger.log(Level.SEVERE, "Ошибка: файл не найден.");
                    throw new FileNotFoundException();
                }

            } catch (FileNotFoundException e) {
                serviceManager.get(ServiceCommand.FILE_ERROR.ordinal())
                        .appendMsg("Введите другое имя файла.\n");
                //sendObj(serviceManager.get(ServiceCommand.FILE_ERROR.ordinal()));
                //remoteAddress = receiveByteArr();
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
            //sendObj(serviceManager.get(ServiceCommand.DATA_ERROR.ordinal()));
            logger.log(Level.WARNING,"Ошибка: некорректные данные.");
            //receiveByteArr();
            buffer.get(bytes, 0, buffer.limit());
            String cond = new String(bytes, 0, buffer.limit());
            buffer.clear();
            if (cond.equals("Да")) {
                products = new HashSet<>();
            } else {
                //sendObj(serviceManager.get(ServiceCommand.EXIT.ordinal()));
                channel.disconnect();
                return products;
            }
        }
        return products;
    }

    public void applyCommands(ProductCollection productCollection) throws FileNotFoundException {

        String file = "input.json";
        Path filePath;
        try {
            filePath = Paths.get(file);
        } catch (InvalidPathException e) {
            serviceManager.get(ServiceCommand.FILE_ERROR.ordinal())
                    .setMsg("Ошибка: некорректное имя файла. ");
            throw new FileNotFoundException();
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
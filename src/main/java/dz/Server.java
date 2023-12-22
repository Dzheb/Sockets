package dz;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
/**
 * 0. Разобраться с написанным кодом в классах Server и Client.
 * 1. Если в начале сообщения есть '@4' - то значит отсылаем сообщеине клиенту с идентификатором 4.
 * 2. Если в начале сообщения нет '@' - значит, это сообщение нужно послать остальным клиентам.
 * 3.* Добавить админское подключение, которое может кикать других клиентов.
 * 3.1 При подключении оно посылает спец. сообщение, подтверждающее, что это - админ.
 * 3.2 Теперь, если админ посылает сообщение kick 4 - то отключаем клиента с идентификатором 4.
 * 4.** Подумать, как лучше структурировать программу (раскидать код по классам).
 */
public class Server {
    public static final int PORT = 8181;
    private static final Map<Long, SocketWrapper> clients = new HashMap<>();
    private static long clientCounter = 1L;

    public static void main(String[] args) throws IOException {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен на порту " + PORT);
            while (true) {
                Socket client = server.accept();
                long clientId = clientCounter++;
                SocketWrapper wrapper = new SocketWrapper(clientId, client);
                System.out.println("Подключился новый клиент[" + wrapper + "]," +
                        " id = " + clientId);
                clients.put(clientId, wrapper);

                new Thread(() -> {
                    boolean admin = false;//метка админского соединения
                    try (Scanner input = wrapper.getInput(); PrintWriter output = wrapper.getOutput()) {
                        output.println("Подключение успешно. Список всех клиентов: " + clients);
                        if (Objects.equals("admin", input.nextLine())) {
                            admin = true;//метка админского соединения
                            clients.values().forEach(it ->
                                    it.getOutput().println("Подключился админ id [" +
                                            clientId + "]"));
                        }
                        while (input.hasNext()) {
                            String clientInput = input.nextLine();
                            if (admin && Objects.equals("kick", clientInput.split(" ")[0])) {
                                try {
                                    long kickClientId = Long.parseLong(clientInput.split(" ")[1]);
                                    clients.values().forEach(it ->
                                            it.getOutput().println("Клиент[" +
                                                    kickClientId + "] отключён администратором"));
                                    clients.get(kickClientId).getSocket().close();
                                    clients.remove(kickClientId);
                                    //break;
                                } catch (NumberFormatException | ArrayIndexOutOfBoundsException | IOException e) {
                                    clients.get(clientId).getOutput().println("Нет такого id клиента");

                                }
                            }
                            if (Objects.equals("q", clientInput)) {
                                // разослать всем остальным
                                clients.remove(clientId);
                                clients.values().forEach(it ->
                                        it.getOutput().println("Клиент[" +
                                                clientId + "] отключился"));
                                break;
                            }
                            // message format : "@number message"
                            if (Objects.equals('@', clientInput.charAt(0))) {
                                String msg = clientInput.substring(clientInput.split(" ")[0]
                                        .length()).trim();
                                try {
                                    // Отсылаем клиенту:
                                    long destinationId = Long.parseLong(clientInput
                                            .split(" ")[0]
                                            .substring(1));
                                    SocketWrapper destination = clients.get(destinationId);
                                    destination.getOutput().println(msg);
                                } catch (NumberFormatException | NullPointerException e) {
                                    if (e instanceof NumberFormatException) {
                                        System.out.println("Нет id клиента отсылаем всем: [" + clientInput
                                                .substring(1).trim() + "]");
                                        clients.values().forEach(it -> it.getOutput().println(msg));
                                    } else {
                                        System.out.println("Нет такого id клиента : [" + clientInput
                                                .substring(1).trim() + "]");
                                        clients.get(clientId).getOutput().println(
                                                "Нет такого id клиента : [" + Long.parseLong(clientInput
                                                        .split(" ")[0]
                                                        .substring(1)) + "]");
                                    }
                                }
                            } else {
                                System.out.println("Неправильный формат сообщения" +
                                        " - никуда не отсылаем");
                            }
                        }
                    }
                }).start();
            }
        }
    }
}

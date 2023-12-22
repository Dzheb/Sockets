package dz;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        final Socket client = new Socket("localhost", Server.PORT);
              new Thread(() -> {
                try(Scanner input = new Scanner(client.getInputStream())) {
                    while (input.hasNext()) {
                        System.out.println(input.nextLine());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
              }).start();
            new Thread(() -> {
                try(PrintWriter output = new PrintWriter(client.getOutputStream(),
                        true)) {
                    output.println("");// метка админ
                    Scanner consoleScanner = new Scanner(System.in);
                    while (true) {
                        String consoleInput = consoleScanner.nextLine();
                        output.println(consoleInput);
                        if (Objects.equals("q", consoleInput)) {
                            output.println("q");
                            client.close();
                             break;
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
    }
}

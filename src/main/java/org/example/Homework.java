package org.example;

import java.util.Objects;

import static java.lang.Integer.parseInt;

public class Homework {

    /**
     * 0. Разобраться с написанным кодом в классах Server и Client.
     * 1. Если в начале сообщения есть '@4' - то значит отсылаем сообщеине клиенту с идентификатором 4.
     * 2. Если в начале сообщения нет '@' - значит, это сообщение нужно послать остальным клиентам.
     * 3.* Добавить админское подключение, которое может кикать других клиентов.
     * 3.1 При подключении оно посылает спец. сообщение, подтверждающее, что это - админ.
     * 3.2 Теперь, если админ посылает сообщение kick 4 - то отключаем клиента с идентификатором 4.
     * 4.** Подумать, как лучше структурировать программу (раскидать код по классам).
     */
    public static void main(String[] args) {
        String test = "@Bla-Bla  Bla";
        if (Objects.equals('@', test.charAt(0))) {
            try {
                System.out.println("Отсылаем клиенту: " + parseInt(test
                        .split(" ")[0]
                        .substring(1)) + " текст [" + test.substring(test.split(" ")[0]
                        .length()).trim() + "]");
            } catch (NumberFormatException e) {
                System.out.println("Нет id клиента отсылаем всем: [" + test
                        .substring(1).trim() + "]");
            }
        } else {
            System.out.println("никуда не отсылаем");
        }
    }
}
package client;


import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import server.ServerSender;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Main {

    @Parameter(names = {"-t", "-type"})
    String type;
    @Parameter(names = {"-k", "-key"})
    String key;
    @Parameter(names = {"-v", "-value"})
    String value;
    @Parameter(names = {"-in", "-json"})
    String in;

    public String getType() {
        return type;
    }

    public String getFileName() {
        return in;
    }

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {


//        String value1 = "{" +"\"name\":\"Elon Musk\"," +"\"car\":{" +"\"model\":\"Tesla Roadster\"," +"\"year\":\"2018\"" +"}," +"\"rocket\":{" +"\"name\":\"Falcon 9\"," +"\"launches\":\"87\"" + "}" +"}";
//
//        String value2 = "Hello World!";
//        System.out.println(value1);
//        System.out.println(value2);
//
//        Gson gsona = new Gson();
//        JsonReader reader = new JsonReader( new StringReader( value2));
//        reader.setLenient(true);
//        JsonPrimitive jsonObject = gsona.fromJson(reader , JsonPrimitive.class);

        ExecutorService executor = Executors.newFixedThreadPool(4);
        boolean terminated = false;

        System.out.println("Client started!");
        Main main = new Main();
        JCommander.newBuilder().addObject(main).build().parse(args); // task 4
        Gson gson = new GsonBuilder().create();


        String fileName = main.getFileName();
        if (fileName == null || "".equals(fileName)) {
            String type = main.getType();
            if (type.equalsIgnoreCase("exit")) {
                String command = gson.toJson(main);
                ServerSender serverSender = new ServerSender(command);
                executor.submit(serverSender);
            } else {
                String command = gson.toJson(main);
                ServerSender serverSender = new ServerSender(command);
                executor.submit(serverSender);
            }
        } else {
            File file = new File("C:\\Users\\leonardo.diniz\\Kotlin\\JSON Database\\JSON Database\\task\\src\\client\\data\\" + fileName);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String command = scanner.nextLine();
                ServerSender serverSender = new ServerSender(command);
                executor.submit(serverSender);
            }
        }

        executor.shutdown();
        terminated = executor.awaitTermination(1000, TimeUnit.MILLISECONDS);

    }



}
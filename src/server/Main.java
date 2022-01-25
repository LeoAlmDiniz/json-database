package server;

import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {

    private static final String PATH = "C:\\Users\\leonardo.diniz\\Kotlin\\JSON Database\\JSON Database\\task\\src\\server\\data\\db.json";

    private static final String address = "127.0.0.1";
    private static final int PORT = 23456;
    private static JsonObject databaseModel;


    public static void main(String[] args) {

        Gson gson = new GsonBuilder().create();
        ReadWriteLock lock = new ReentrantReadWriteLock();
        Lock readLock = lock.readLock();
        Lock writeLock = lock.writeLock();
        try (ServerSocket server = new ServerSocket(PORT, 50, InetAddress.getByName(address))) {

            System.out.println("Server started!");
            while (true) {
                try (Socket socket = server.accept();
                     DataInputStream input = new DataInputStream(socket.getInputStream());
                     DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

                    readLock.lock();
                    setUpDatabase();
                    readLock.unlock();
                    String msgReceived = input.readUTF();
                    System.out.println(msgReceived);
                    Command command;
                    command = gson.fromJson(msgReceived, Command.class);

                    String type;
                    type = command.getType();

                    Object keyList = command.getKey();
                    if (keyList instanceof  ArrayList) {
                        keyList = gson.toJsonTree(keyList).getAsJsonArray();
                    } else {
                        JsonArray array = new JsonArray(1);
                        array.add((String)keyList);
                        keyList = array;
                    }
                    JsonArray formattedKey = (JsonArray)keyList;

                    Object value = command.getValueToSet();
                    if (value instanceof  LinkedTreeMap) {
                        value = gson.toJsonTree(value).getAsJsonObject();
                    } else if (value instanceof  ArrayList) {
                        value = gson.toJsonTree(value).getAsJsonArray();
                    }

                    if (type.equalsIgnoreCase("exit")) {
                        break;
                    }

                    if ("get".equalsIgnoreCase(type)) {
                        JsonElement innerDbValue = databaseModel;
                        try {
                            for ( JsonElement key : formattedKey ) {
                                innerDbValue = ((JsonObject)innerDbValue).get( key.getAsString() );
                            }
                            output.writeUTF( respondOK( gson.toJson( innerDbValue, JsonObject.class)) );
                        } catch (JsonSyntaxException ea) {
                            try {
                                output.writeUTF( respondOK( gson.toJson( innerDbValue, JsonArray.class)) );
                            } catch (JsonSyntaxException ep) {
                                output.writeUTF( respondOK( (innerDbValue.toString() )) );
                            }
                        } catch (Exception e) {
                            output.writeUTF( respondError("No such key") );
                        }
                    } else if ("delete".equalsIgnoreCase(type)) {
                        JsonElement outerDbValue = databaseModel;
                        JsonElement innerDbValue = databaseModel;
                        String innerKey = "";
                        try {
                            boolean first = true;
                            for ( JsonElement key : formattedKey ) {
                                if (!first) {
                                    outerDbValue = ((JsonObject)outerDbValue).get( innerKey );
                                }
                                innerDbValue = ((JsonObject)innerDbValue).get( key.getAsString() );
                                innerKey = key.getAsString();
                                first = false;
                            }
                            ((JsonObject)outerDbValue).remove( innerKey );
                            output.writeUTF( respondOK() );
                        } catch (JsonSyntaxException es) {
                            ((JsonObject)innerDbValue).remove( innerKey );
                            output.writeUTF( respondOK() );
                        } catch (Exception e) {
                            output.writeUTF( respondError("No such key") );
                        }
                    } else if ("set".equalsIgnoreCase(type)) {
                        JsonElement outerDbValue = databaseModel;
                        JsonElement innerDbValue = databaseModel.deepCopy();
                        for (int i=0; i < formattedKey.size(); i++ ) {
                            JsonElement key = formattedKey.get(i);
                            if (i < formattedKey.size()-1) {
                                innerDbValue = ( (JsonObject) innerDbValue).get(key.getAsString());
                                if (innerDbValue == null) {
                                    ( (JsonObject) outerDbValue).add(key.getAsString(), new JsonObject());
                                    innerDbValue = ( (JsonObject) outerDbValue).get(key.getAsString()).deepCopy();
                                    outerDbValue = ( (JsonObject) outerDbValue).get(key.getAsString());
                                } else {
                                    outerDbValue = ( (JsonObject) outerDbValue).get(key.getAsString());
                                }
                            } else {
                                if (value instanceof JsonObject) {
                                    ( (JsonObject) outerDbValue).add(key.getAsString(), (JsonObject)value );
                                } else if (value instanceof JsonArray) {
                                    ( (JsonObject) outerDbValue).add(key.getAsString(), ((JsonArray)value) );
                                } else {
                                    ( (JsonObject) outerDbValue).addProperty(key.getAsString(), (String)value );
                                }
                            }
                        }
                        output.writeUTF( respondOK() );
                    } else {
                        output.writeUTF( respondError("No valid command type specified") );
                    }

                    writeLock.lock();
                    updateDatabase();
                    writeLock.unlock();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setUpDatabase() throws FileNotFoundException {
        Gson gson = new GsonBuilder().create();
        JsonReader reader = new JsonReader(new FileReader(PATH));
        try {
            databaseModel = gson.fromJson(reader, JsonObject.class);
            if (databaseModel == null) {
                databaseModel = new JsonObject();
            }
        } catch (Exception e) {
            System.out.println("\"db.json\" was cleared or malformmed. For that reason, a new blank db.json was generated ");
            throw e;
        }
    }

    private static void updateDatabase() throws IOException {
        FileWriter myWriter = new FileWriter(PATH);
        Gson gson = new GsonBuilder().create();
        String outputString = gson.toJson(databaseModel);
        myWriter.write(outputString);
        myWriter.close();
    }

    private static String respondOK() {
        return "{\"response\":\"OK\"}";
    }

    private static String respondOK(String value) {
        return "{\"response\":\"OK\",\"value\":" + value + "}";
    }

    private static String respondError(String reason) {
        return "{\"response\":\"ERROR\",\"reason\":\""+reason+"\"}";
    }

    private static class Command {

        private final String type;
        private final Object key;
        private final Object value;

        public Command(String type, String key, String value) {
            this.type = type;
            this.key = key;
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public Object getKey() {
            return key;
        }

        public Object getValueToSet() {
            return value;
        }

    }

}
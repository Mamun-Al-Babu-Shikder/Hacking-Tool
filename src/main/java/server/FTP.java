package server;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FTP {

    public static final int port = Connection.port + 1;
    private static ServerSocket serverSocket;
    static {
        try {
            serverSocket = new ServerSocket(FTP.port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // /home/mamun/Desktop/file.txt

    public static void receiveFile(File file) throws IOException {
        Socket socket = serverSocket.accept();
        InputStream is = socket.getInputStream();
        OutputStream os = new FileOutputStream(file);
        byte[] bytes = new byte[1024];
        int len = 0;
        while ((len=is.read(bytes))!=-1){
            os.write(bytes, 0, len);
        }
        os.close();
        is.close();
        socket.close();
    }

    public static void receiveScreenshot(String fileNameWithPath) throws IOException {
        Socket socket = serverSocket.accept();
        InputStream is = socket.getInputStream();
        File file = new File(fileNameWithPath);
        BufferedImage image = ImageIO.read(is);
        ImageIO.write(image, "png", file);
        is.close();
        socket.close();
    }


}

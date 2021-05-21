package services;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileServer {
    private final String filePath;
    private final ObjectOutputStream objectOutputStream;
    private final OutputStream socketStream;

    public FileServer(String filePath, ObjectOutputStream objectOutputStream, OutputStream socketStream) {
        this.filePath = filePath;
        this.objectOutputStream = objectOutputStream;
        this.socketStream = socketStream;
    }

    public void transmit() {
        File rdp = new File(filePath);

        byte[] fileBytes = new byte[(int) rdp.length()];

        BufferedInputStream fileBuff = null;
        DataOutputStream fileStream = null;
        try {
            fileBuff = new BufferedInputStream(new FileInputStream(filePath));
            fileBuff.read(fileBytes, 0, fileBytes.length);

            fileStream = new DataOutputStream(socketStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // First send the name and size of the file
            // and after that the file itself.
            int pos = filePath.lastIndexOf('\\') + 1;
            objectOutputStream.writeObject(filePath.substring(pos, filePath.length()) + "#" + fileBytes.length);
            fileStream.write(fileBytes, 0, fileBytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                fileBuff.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

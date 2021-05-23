package services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileServer {
    private final String filePath;
    private final ObjectOutputStream objectOutputStream;
    private final OutputStream socketStream;

    private static final Logger LOGGER = LogManager.getLogger(FileServer.class);

    public FileServer(String filePath, ObjectOutputStream objectOutputStream, OutputStream socketStream) {
        this.filePath = filePath;
        this.objectOutputStream = objectOutputStream;
        this.socketStream = socketStream;
    }

    public void transmit() {
        LOGGER.info("New FileServer initialized.");
        File rdp = new File(filePath);

        byte[] fileBytes = new byte[(int) rdp.length()];

        BufferedInputStream fileBuff = null;
        DataOutputStream fileStream = null;
        try {
            fileBuff = new BufferedInputStream(new FileInputStream(filePath));
            fileBuff.read(fileBytes, 0, fileBytes.length);

            fileStream = new DataOutputStream(socketStream);

            LOGGER.info("Completed reading file: " + rdp.getName());
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }

        try {
            // First send the name and size of the file
            // and after that the file itself.
            int pos = filePath.lastIndexOf('\\') + 1;
            objectOutputStream.writeObject(filePath.substring(pos, filePath.length()) + "#" + fileBytes.length);
            fileStream.write(fileBytes, 0, fileBytes.length);

            LOGGER.info("File sent! File: " + rdp.getName());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
        finally {
            try {
                fileBuff.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}

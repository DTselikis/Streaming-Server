package services;

import java.io.*;

public class FileServer {
    private final String filePath;
    private final OutputStream fileStream;

    public FileServer(String filePath, OutputStream fileStream) {
        this.filePath = filePath;
        this.fileStream = fileStream;
    }

    public void transmit() {
        File rdp = new File(filePath);
        byte[] fileBytes = new byte[(int) rdp.length()];

        BufferedInputStream fileBuff = null;
        try {
            fileBuff = new BufferedInputStream(new FileInputStream(rdp));
            fileBuff.read(fileBytes, 0, fileBytes.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fileStream.write(fileBytes, 0, fileBytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                fileStream.close();
                fileBuff.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

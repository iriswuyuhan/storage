package storageservice;

import java.io.IOException;

public interface StorageService {
    byte[] getFile(String path) throws IOException;
    String writeFile(byte[] fileStream,String fileType) throws IOException;
}

package storageservice;

import util.DocType;

import java.io.IOException;

public interface StorageService {
    byte[] getFile(String path) throws IOException;
    String writeFile(byte[] fileStream, DocType docType) throws IOException;

    void setBigLimit(long bigLimit);
    void setSmallLimit(long smallLimit);
}

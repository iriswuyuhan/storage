package storageservice;

import util.DocType;

import java.io.IOException;

public interface StorageService {
    String createFile(long volume, DocType docType);
}

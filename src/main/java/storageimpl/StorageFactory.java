package storageimpl;

import storageservice.StorageService;

import java.io.IOException;

public class StorageFactory {
    private StorageService storageService;

    public StorageFactory() throws IOException {
        storageService=Storage.getInstance();
    }

    public StorageService getStorageService() {
        return storageService;
    }
}

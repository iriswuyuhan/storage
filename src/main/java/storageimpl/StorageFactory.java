package storageimpl;

import storageservice.StorageService;

import java.io.IOException;

public class StorageFactory {
    private static StorageFactory storageFactory;

    private StorageService storageService;

    private StorageFactory() throws IOException {
        storageService=Storage.getInstance();
    }

    public static StorageFactory getInstance() throws IOException {
        if(storageFactory==null){
            storageFactory=new StorageFactory();
        }
        return storageFactory;
    }

    public StorageService getStorageService() {
        return storageService;
    }
}

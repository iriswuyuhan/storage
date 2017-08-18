package storageimpl;

import storageservice.StorageService;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            StorageFactory storageFactory = StorageFactory.getInstance();
            StorageService storageService=storageFactory.getStorageService();

        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
}

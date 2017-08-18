package com.wisdom.nj.storage.storageimpl;

import com.wisdom.nj.storage.storageservice.StorageService;
import com.wisdom.nj.storage.util.DocType;

public class Main {
    public static void main(String[] args) {
        StorageService storageService = StorageFactory.getStorageService();
        storageService.createFile(1024, DocType.txt);
    }
}

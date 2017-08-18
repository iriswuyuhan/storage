package com.wisdom.nj.storage.storageimpl;

import com.wisdom.nj.storage.storageservice.StorageService;

public class StorageFactory {
    private static StorageService storageService;

    /**
     * 获取StorageService
     *
     * @return StorageService
     */
    public static StorageService getStorageService() {
        if (storageService == null) {
            storageService = Storage.getInstance();
        }
        return storageService;
    }
}

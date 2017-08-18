package com.wisdom.nj.storage.storageservice;

import com.wisdom.nj.storage.util.DocType;

public interface StorageService {
    /**
     * 只负责创建相应的文件夹及文件，并不负责具体的写入和读出
     *
     * @param volume  文件大小（单位：byte）
     * @param docType 文件种类（enum）
     * @return 文件路径
     */
    String createFile(long volume, DocType docType);
}

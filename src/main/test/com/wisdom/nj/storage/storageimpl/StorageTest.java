package com.wisdom.nj.storage.storageimpl;

import com.wisdom.nj.storage.util.DocType;
import com.wisdom.nj.storage.util.MyErrorCode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wisdom.nj.storage.storageexception.MyException;

import java.io.File;
import java.io.FileInputStream;

public class StorageTest {
    private Storage storage;
    private static final Logger logger = LoggerFactory.getLogger(StorageTest.class);

    @Before
    public void setUp() throws Exception {
        storage=Storage.getInstance();
    }

    @Test
    public void writeFile() throws Exception {
        logger.info("by wuyuhan");

        File dir = new File("D:\\test");//从D盘的test里读取数据
        File[] files=dir.listFiles();

        for(File file:files) {
            String type=file.getName().substring(file.getName().lastIndexOf(".")+1);
            FileInputStream fis = new FileInputStream(file);
            long size = file.length();
            byte[] filestream = new byte[(int) size];
            int offset = 0;
            int numRead = 0;
            while (offset < size
                    && (numRead = fis.read(filestream, offset, filestream.length - offset)) >= 0) {
                offset += numRead;
            }

            try {
                if (offset != filestream.length) {
                    throw new MyException(MyErrorCode.WRITEFILETOOBIG);
                }
            } catch (MyException me) {
                logger.error(me.getMessage(), me);
            }

            fis.close();
            storage.createFile(size, DocType.valueOf(type));
        }
    }

    //删除所有文件夹
    @After
    public void tearDown() throws Exception {
        for(File dir:Storage.dirs){
            fileDelete(dir);
        }
    }

    private boolean fileDelete(File dir){
        if(!dir.isDirectory()){
            return dir.delete();
        }
        File[] files=dir.listFiles();
        for (File file : files) {
            if (!fileDelete(file)) {
                return false;
            }
        }
        return dir.delete();
    }
}
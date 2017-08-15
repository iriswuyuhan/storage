package storageimpl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.DocType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class StorageBasicTest {
    private Storage storage;

    @Before
    public void setUp() throws Exception {
        storage=Storage.getInstance();
    }

    @Test
    public void getFile() throws Exception {

    }

    @Test
    public void writeFile() throws Exception {
//        File file=new File("D:\\a.txt");
//        FileInputStream fis = new FileInputStream(file);
//        long size = file.length();
//        byte[] filestream = new byte[(int) size];
//        int offset = 0;
//        int numRead = 0;
//        while (offset < size
//                && (numRead = fis.read(filestream, offset, filestream.length - offset)) >= 0) {
//            offset += numRead;
//        }
//
//        if (offset != filestream.length) {
//            throw new IOException("Could not completely read file " + file.getName());
//        }
//
//        fis.close();
//        storage.writeFile(filestream, DocType.txt);

//        File dir = new File("D:\\test");
//        File[] files=dir.listFiles();

//        for(File file:files) {
//            String type=file.getName().substring(file.getName().lastIndexOf(".")+1);
//            FileInputStream fis = new FileInputStream(file);
//            long size = file.length();
//            byte[] filestream = new byte[(int) size];
//            int offset = 0;
//            int numRead = 0;
//            while (offset < size
//                    && (numRead = fis.read(filestream, offset, filestream.length - offset)) >= 0) {
//                offset += numRead;
//            }
//
//            if (offset != filestream.length) {
//                throw new IOException("Could not completely read file " + file.getName());
//            }
//
//            fis.close();
//            storage.writeFile(filestream, type);
//        }

        Random r=new Random();
        for(int i=0;i<50000;i++){
            int length=(int)r.nextDouble()*1024*1024;
            byte[] file=new byte[length];
            storage.writeFile(file,DocType.pdf);
        }
    }

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

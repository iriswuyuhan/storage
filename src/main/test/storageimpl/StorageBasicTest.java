package storageimpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import storageexception.MyException;
import util.DocType;
import util.MyErrorCode;

import java.io.File;
import java.io.FileInputStream;

public class StorageBasicTest {
    private Storage storage;

    @Before
    public void setUp() throws Exception {
        storage=Storage.getInstance();
    }

    @Test
    public void writeFile2() throws Exception{
        File dir = new File("D:\\test");
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

            if (offset != filestream.length) {
                throw new MyException(MyErrorCode.WRITEFILETOOBIG);
            }

            fis.close();
            storage.createFile(size, DocType.valueOf(type));
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

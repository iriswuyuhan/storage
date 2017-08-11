package storageimpl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class StorageBasicTest {
    Storage storage;

    @Before
    public void setUp() throws Exception {
        storage=Storage.getInstance();
    }

    @Test
    public void getFile() throws Exception {
//        byte[] filestream=storage.getFile("a.txt");
//        String content=new String(filestream);
//        Assert.assertEquals("ievrn",content);
    }

    @Test
    public void writeFile() throws Exception {
        File file=new File("D:\\lalala.docx");
        FileInputStream fis=new FileInputStream(file);
        long size=file.length();
        byte[] filestream=new byte[(int)size];
        int offset=0;
        int numRead=0;
        while (offset<size
                &&(numRead=fis.read(filestream,offset,filestream.length-offset))>=0){
            offset+=numRead;
        }

        if(offset!=filestream.length){
            throw new IOException("Could not completely read file "+file.getName());
        }
        fis.close();

//        Assert.assertEquals("a.docx",storage.writeFile(filestream,"a.docx"));
        System.out.println(storage.writeFile(filestream,"docx"));
    }

    @After
    public void tearDown() throws Exception {
//        File file=new File("a.txt");
//        if(file.exists()){
//            file.delete();
//        }
//
//        List<File> files=storage.dirs;
//        for(File f:files){
//            if(f.exists()){
//                f.delete();
//            }
//        }
    }
}

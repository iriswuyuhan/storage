package storageimpl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.DocType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class StorageBasicTest {
    private Storage storage;

    @Before
    public void setUp() throws Exception {
        storage=Storage.getInstance();
    }

    @Test
    public void writeFile1() throws Exception {
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

        byte a=49;

        long startTime=System.currentTimeMillis();
        Random r=new Random();
        for(int i=0;i<10000;i++){
            int length=r.nextInt(1024*1024*2);
//            int length=7687078;
//            System.out.println(length);
            byte[] file=new byte[length];
            Arrays.fill(file,a);
            storage.writeFile(file,DocType.txt);
        }
        long endTime=System.currentTimeMillis();
        System.out.println("写入10000个文件耗时："+(endTime-startTime)+"ms");
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
                throw new IOException("Could not completely read file " + file.getName());
            }

            fis.close();
            storage.writeFile(filestream, DocType.valueOf(type));
        }
    }

    @Test
    public void getFile1() throws Exception {
        String basicPath="E:\\DIR_";
        Random r=new Random();
        long total=0;
        int itr=0;
        for(int i=0;i<100;i++){
            itr=r.nextInt(20);
            File[] files=Storage.dirs.get(itr).listFiles();
            itr=r.nextInt(files.length);
            String path=files[itr].getAbsolutePath();
            long start=System.currentTimeMillis();
            storage.getFile(path);
            long end=System.currentTimeMillis();
            total+=end-start;
        }
        double average=total/100.0;
        System.out.println("spend "+average+"ms reading 100 files");
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

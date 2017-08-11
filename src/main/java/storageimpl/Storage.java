package storageimpl;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wuyuhan
 */
public class Storage {
    public static Storage storage;

    private final int BASIC_NUM_OF_DIRECTORIES=20;
    private final long FIRST_MAX_DIR_SPACE=new Long("4294967295");//2^32-1
    private final long FIRST_MAX_NUM_OF_FILE=new Long("65535");//2^16-1

    private int numOfDirectories;
    private List<Long> dirSpace;
    private List<Long> fileNum;

    //这里日后会删除
    public List<File> dirs=new ArrayList<File>();

    /**
     *
     * @throws IOException
     */
    //TODO 这里应该区分Exception的类别
    private Storage() throws IOException {
        numOfDirectories=BASIC_NUM_OF_DIRECTORIES;
        dirSpace=new ArrayList<Long>();
        fileNum=new ArrayList<Long>();

        for(int i=0;i<numOfDirectories;i++){
            File temp=new File("DIR_"+i);
            if(!temp.exists()){
                temp.mkdirs();
            }

            dirs.add(temp);
            dirSpace.add(FIRST_MAX_DIR_SPACE);
            fileNum.add(FIRST_MAX_NUM_OF_FILE);
        }
    }

    /**
     *
     * @return
     * @throws IOException
     */
    public static Storage getInstance() throws IOException {
        if(storage==null){
            storage=new Storage();
        }
        return storage;
    }

    /**
     *
     * @param path
     * @return
     * @throws IOException
     */

    //TODO 这里也应当考虑Exception
    public byte[] getFile(String path) throws IOException {
        File file=new File(path);
        long length=file.length();

        if(length<Integer.MAX_VALUE) {
            byte[] filestream = new byte[(int) length];

            FileInputStream fis=new FileInputStream(file);
            int offset=0;
            int numRead=0;

            while(offset<length
                    &&(numRead=fis.read(filestream,offset,filestream.length-offset))>=0){
                offset+=numRead;
            }

            if(offset!=filestream.length){
                throw new IOException("Could not completely read file "+file.getName());
            }
            fis.close();

            return filestream;
        }
        return null;
    }

    /**
     *
     * @param filestream
     * @param fileType
     * @return
     * @throws IOException
     */
    //让我们先假设是byte[]好了
    //TODO 处理Exception
    public String writeFile(byte[] filestream,String fileType) throws IOException {
        long length=filestream.length;
        int itr=findBest(length);
        File file=dirs.get(itr);

        String path=file.getAbsolutePath();
        System.out.println(path);
        OutputStream os=new FileOutputStream(path+"\\"+fileNum.get(itr)+"."+fileType);
        InputStream is=new ByteArrayInputStream(filestream);
        byte[] buffer=new byte[1024];
        int len=0;
        while ((len=is.read(buffer))!=-1){
            os.write(buffer,0,len);
        }
        return path+fileNum.get(itr)+"."+fileType;
    }

    //怎么判定什么是最佳方案呢？
    //TODO 这里应该有判断最佳文件夹的方法
    private int findBest(long volume){
        Map<Integer,Long> proSpace=new HashMap<Integer, Long>();

        for(Long s:dirSpace){

        }
        return 0;
    }
}

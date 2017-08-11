package storageimpl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wuyuhan
 */

//TODO：并发处理
public class Storage {
    private static Storage storage;

    private final int BASIC_NUM_OF_DIRECTORIES=3;
    private final long FIRST_MAX_DIR_SPACE=new Long("4294967296");//2^32-1
    private final long FIRST_MAX_NUM_OF_FILE=new Long("65536");//2^16-1

    public List<File> dirs=new ArrayList<File>();

    private int numOfDirectories;
    private List<Long> dirSpace;
    private List<Long> fileNum;

    private double a=0.5;
    private double b=0.5;
    /**
     *
     * @throws IOException
     */
    //TODO：这里应该区分Exception的类别
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
    static Storage getInstance() throws IOException {
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

    //TODO：这里也应当考虑Exception
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
    //TODO：处理Exception
    String writeFile(byte[] filestream, String fileType) throws IOException {
        long length=filestream.length;
        int itr=findBest(length);

        long space=dirSpace.get(itr)-length;
        dirSpace.set(itr,space);

        long num=fileNum.get(itr)-1;
        fileNum.set(itr,num);

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
    //TODO：这里应该有判断最佳文件夹的方法
    private int findBest(long volume){
        int winner=0;
        double mark=0.0;

        //这里可以改为下次匹配策略
        for(int i=0;i<dirSpace.size();i++){
            if(volume<=dirSpace.get(i)){
                double markTemp=dirSpace.get(i)/(double)FIRST_MAX_NUM_OF_FILE;
                markTemp=a*markTemp+b*fileNum.get(i);
                if(markTemp>mark) {
                    winner = i;
                    mark=markTemp;
                }
            }
        }
        return winner;
    }
}

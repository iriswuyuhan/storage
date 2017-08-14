package storageimpl;

import storageexception.MyException;
import storageservice.StorageService;
import util.MyErrorCode;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wuyuhan
 */

//TODO：（1）当新建的文件夹也满了的情况，文件夹空间上限和文件数目上限
//TODO：（2）a，b的彻底确定可能会涉及到机器学习？？？
public class Storage implements StorageService{
    private static Storage storage;

    private final static int BASIC_NUM_OF_DIRECTORIES=3;
    private final static long FIRST_MAX_DIR_SPACE=new Long("4294967295");//2^32-1=4294967295
    private final static long FIRST_MAX_NUM_OF_FILE=new Long("65535");//2^16-1=65535

    private static List<File> dirs=new ArrayList<File>();

    private int numOfDirectories;//在转型时可能会用到
    private static List<Long> dirSpace;
    private static List<Long> fileNum;
    private int index;

    private double a=0.5;
    private double b=0.5;

//    private long totalSpace=0;
//    private long totalNum=0;

    private long bigLimit;
    private long smallLimit;

    private Storage(){
        numOfDirectories=BASIC_NUM_OF_DIRECTORIES;
        dirSpace=new ArrayList<Long>();
        fileNum=new ArrayList<Long>();

        for(int i=0;i<numOfDirectories;i++){
            createDir(i);
        }

        bigLimit=102000;
        smallLimit=15300;

        index=0;
    }

    /**
     *
     * @return
     * @throws IOException
     */
    static Storage getInstance(){
        if(storage==null){
            storage=new Storage();
        }
        return storage;
    }

    public void setBigLimit(long bigLimit) {
        this.bigLimit = bigLimit;
    }

    public void setSmallLimit(long smallLimit) {
        this.smallLimit = smallLimit;
    }

    /**
     *
     * @param path
     * @return
     * @throws IOException
     */
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
                throw new MyException(MyErrorCode.READIOEXCEPTION);
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
    public String writeFile(byte[] filestream, String fileType) throws IOException {
        long length=filestream.length;
        int itr=findBest(length);

        long space=dirSpace.get(itr)-length;
        dirSpace.set(itr,space);
//        totalSpace+=space;

        long num=fileNum.get(itr)-1;
        fileNum.set(itr,num);
//        totalNum+=num;

        File file=dirs.get(itr);

        String path=file.getAbsolutePath();
        OutputStream os=new FileOutputStream(path+"\\"+fileNum.get(itr)+"."+fileType);
        InputStream is=new ByteArrayInputStream(filestream);
        byte[] buffer=new byte[1024];
        int len=0;
        while ((len=is.read(buffer))!=-1){
            os.write(buffer,0,len);
        }
        return path+fileNum.get(itr)+"."+fileType;
    }

    public String[] writeFile(byte[][] filesStream, String[] fileType) throws IOException {
        String[] paths=new String[fileType.length];
        for(int i=0;i<fileType.length;i++){
            paths[i]=writeFile(filesStream[i],fileType[i]);
        }
        return paths;
    }

    //怎么判定什么是最佳方案呢？
    private int findBest(long volume){
        boolean big=false;
        boolean small=false;

        if(volume>bigLimit){
            big=true;
        }
        if(volume<smallLimit){
            small=true;
        }

        int winner=getWinner(big,small,volume);
        if(winner<0){
            initial();
            winner=getWinner(big,small,volume);
        }
//        System.out.println(volume+"进入"+winner);
        return winner;
    }

    private synchronized int getWinner(boolean big,boolean small,long volume){
        int winner=-1;
        double mark=0.0;

        for(int i=index;i<index+BASIC_NUM_OF_DIRECTORIES;i++){
//            if(index==3){
//                System.out.println(mark);
//            }
            if(volume<=dirSpace.get(i)&&fileNum.get(i)>0){
                //剩余空间
                double markTemp=getSpaceMark(big,small,i)+getNumMark(big,small,i);
                if(markTemp>=mark) {
                    winner = i;
                    mark=markTemp;
                }
            }
        }
        return winner;
    }

    private double getSpaceMark(boolean big,boolean small,int i){
        if(big){
            return (a-0.3)*(FIRST_MAX_DIR_SPACE-dirSpace.get(i))/(double)FIRST_MAX_NUM_OF_FILE;
        }
        else if(small){
            return (a+0.3)*(dirSpace.get(i)/(double)FIRST_MAX_NUM_OF_FILE);
        }
        else{
            return 0;
        }
    }

    private double getNumMark(boolean big,boolean small,int i){
        if(big){
            return (b+0.3)*(FIRST_MAX_NUM_OF_FILE-fileNum.get(i));
        }
        else if(small){
            return (b-0.3)*fileNum.get(i);
        }
        else{
            return fileNum.get(i);
        }
    }
    //（2）考虑新增一部分也满了的情况
    private void initial(){
        for(int i=index+BASIC_NUM_OF_DIRECTORIES;i<index+2*BASIC_NUM_OF_DIRECTORIES;i++){
            createDir(i);
        }
        this.index=this.index+BASIC_NUM_OF_DIRECTORIES;
    }

    private static void createDir(int i){
        File temp=new File("DIR_"+i);
        if(!temp.exists()){
            temp.mkdirs();
        }

        dirs.add(temp);
        dirSpace.add(FIRST_MAX_DIR_SPACE);
        fileNum.add(FIRST_MAX_NUM_OF_FILE);
    }
}

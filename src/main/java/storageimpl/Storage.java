package storageimpl;

import storageexception.MyException;
import storageservice.StorageService;
import util.DocType;
import util.MyErrorCode;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 实现文件存储
 * @author wuyuhan
 */

public class Storage implements StorageService{
    private static Storage storage;

    private final static int BASIC_NUM_OF_DIRECTORIES=20;
    private final static long FIRST_MAX_DIR_SPACE=new Long("4294967295");//2^32-1=4294967295
    private final static long FIRST_MAX_NUM_OF_FILE=new Long("65535");//2^16-1=65535

    static List<File> dirs=new ArrayList<File>();

    private int numOfDirectories;//在转型时可能会用到
    private static List<Long> dirSpace;
    private static List<Long> fileNum;
    private int index;

    private double a=0.5;
    private double b=0.5;

    private long bigLimit;
    private long smallLimit;

    private Storage(){
        numOfDirectories=BASIC_NUM_OF_DIRECTORIES;
        dirSpace=new ArrayList<Long>();
        fileNum=new ArrayList<Long>();

        for(int i=0;i<numOfDirectories;i++){
            createDir(i);
        }

        bigLimit=1024*1024;
        smallLimit=340*1024;

        index=0;
    }

    /**
     * @return 单例模式获取一个对象
     */
    static Storage getInstance(){
        if(storage==null){
            storage=new Storage();
        }
        return storage;
    }

    /**
     * @param bigLimit 设置大值界限，超过这个值则认为是大文件
     */
    public void setBigLimit(long bigLimit) {
        this.bigLimit = bigLimit;
    }

    /**
     * @param smallLimit 设置小值界限，低于这个值则认为是小文件
     */
    public void setSmallLimit(long smallLimit) {
        this.smallLimit = smallLimit;
    }

    /**
     * @param path 文件路径
     * @return 字节数组，文件过大返回null
     * @throws IOException 文件找不到或无法打开、写入;MyException 文件没有完全读完
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
     * @param filestream 字节数组
     * @param fileType 文件种类（enum）
     * @return 文件路径
     * @throws IOException 文件找不到或无法打开、写入
     */
    //让我们先假设是byte[]好了
    public String writeFile(byte[] filestream, DocType fileType) throws IOException {
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
        OutputStream os=new FileOutputStream(path+"\\"+fileNum.get(itr)+"."+fileType.toString());
        InputStream is=new ByteArrayInputStream(filestream);
        byte[] buffer=new byte[1024];
        int len=0;
        while ((len=is.read(buffer))!=-1){
            os.write(buffer,0,len);
        }
        return path+fileNum.get(itr)+"."+fileType.toString();
    }

    /**
     * @param volume 文件长度
     * @return 最佳文件夹的index
     */
    private int findBest(long volume){
        boolean big=false;
        boolean small=false;

        if(volume>bigLimit){
            big=true;
        }
        else if(volume<smallLimit){
            small=true;
        }

        int winner=getWinner(big,small,volume);
        if(winner<0){
            initial();
            winner=getWinner(big,small,volume);
        }
        if(winner<0){
            throw new MyException(MyErrorCode.WRITEFILETOOBIG);
        }
//        System.out.println(volume+"进入"+winner);
        return winner;
    }

    /**
     * @param big 是否过大
     * @param small 是否过小
     * @param volume 文件长度
     * @return 最佳文件夹的index
     */
    private synchronized int getWinner(boolean big,boolean small,long volume){
        int winner=-1;
        double mark=0.0;

        //TODO：这里写得有点问题，还是应该用i=0开始
        for(int i=0;i<numOfDirectories;i++){
//            if(index==3){
//                System.out.println(mark);
//            }
            if(volume<=dirSpace.get(i)&&fileNum.get(i)>0){
                //剩余空间
                double markTemp=getSpaceMark(big,small,i)+getNumMark(big,small,i);
                if(markTemp>mark) {
                    winner = i;
                    mark=markTemp;
                }
            }
        }
        return winner;
    }

    /**
     * @param big 是否过大
     * @param small 是否过小
     * @param i 某文件夹的index
     * @return 某文件夹的空间评分
     */
    private double getSpaceMark(boolean big,boolean small,int i){
        if(big){
            return (a-0.3)*dirSpace.get(i)/(double)FIRST_MAX_NUM_OF_FILE;
        }
        else if(small){
            return (a+0.3)*((FIRST_MAX_DIR_SPACE-dirSpace.get(i))/(double)FIRST_MAX_NUM_OF_FILE);
        }
        else{
            return 0;
        }
    }

    /**
     * @param big 是否过大
     * @param small 是否过小
     * @param i 某文件夹的index
     * @return 某文件夹的文件数评分
     */
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

    /**
     * 当前文件夹全部填满时新建一定数量的文件夹
     */
    private void initial(){
        this.index+=BASIC_NUM_OF_DIRECTORIES;
        for(int i=index;i<index+BASIC_NUM_OF_DIRECTORIES;i++){
            createDir(i);
        }
        this.numOfDirectories+=BASIC_NUM_OF_DIRECTORIES;
    }

    /**
     * 创建文件夹
     * @param i 某文件夹的index
     */
    private static void createDir(int i){
        File temp=new File("E:\\DIR_"+i);
        if(!temp.exists()){
            temp.mkdirs();
        }

        dirs.add(temp);
        dirSpace.add(FIRST_MAX_DIR_SPACE);
        fileNum.add(FIRST_MAX_NUM_OF_FILE);
    }
}

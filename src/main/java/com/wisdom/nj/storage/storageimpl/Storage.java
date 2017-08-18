package com.wisdom.nj.storage.storageimpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wisdom.nj.storage.storageexception.MyException;
import com.wisdom.nj.storage.storageservice.StorageService;
import com.wisdom.nj.storage.util.DocType;
import com.wisdom.nj.storage.util.MyErrorCode;
import com.wisdom.nj.storage.util.PropertyReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 找到文件最佳路径
 * @author wuyuhan
 */
public class Storage implements StorageService{
    private static final Logger logger = LoggerFactory.getLogger(Storage.class);

    private static Storage storage;

    private static int BASIC_NUM_OF_DIRECTORIES;
    private static long FIRST_MAX_DIR_SPACE;//2^32-1=4294967295
    private static long FIRST_MAX_NUM_OF_FILE;//2^16-1=65535

    private static String rootPath;

    static List<File> dirs=new ArrayList<File>();

    private static List<Long> dirSpace;
    private static List<Long> fileNum;

    private int numOfDirectories;//在转型时可能会用到
    private int index;

    private double a;
    private double b;

    private long bigLimit;
    private long smallLimit;

    private Storage(){
        PropertyReader pr=PropertyReader.getInstance();

        rootPath=pr.getProperty("root_path");

        BASIC_NUM_OF_DIRECTORIES=Integer.parseInt(pr.getProperty("basic_num_of_dirs"));
        FIRST_MAX_DIR_SPACE=Long.parseLong(pr.getProperty("max_space"));
        FIRST_MAX_NUM_OF_FILE=Long.parseLong(pr.getProperty("max_num"));

        numOfDirectories=BASIC_NUM_OF_DIRECTORIES;
        dirSpace=new ArrayList<Long>();
        fileNum=new ArrayList<Long>();

        for(int i=0;i<numOfDirectories;i++){
            createDir(i);
        }

        bigLimit=Long.parseLong(pr.getProperty("big_limit"));
        smallLimit=Long.parseLong(pr.getProperty("small_limit"));

        a=Double.parseDouble(pr.getProperty("space_weight"));
        b=Double.parseDouble(pr.getProperty("num_weight"));

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

    public String createFile(long volume, DocType fileType){
        int itr = 0;
        try {
            itr = findBest(volume);
        } catch (MyException me) {
            logger.error(me.getMessage(), me);
            return null;
        }

        decreaseSpace(itr,volume);

        long name=decreaseFile(itr);

        File dir=dirs.get(itr);
        String path=dir.getAbsolutePath();
        String filePath=path + "\\" + name + "." + fileType.toString();

        File file=new File(filePath);

        try{
            if(!file.exists()){
                file.createNewFile();
            }
            else{
                throw new MyException(MyErrorCode.FILEEXISTS);
            }
        }catch (IOException ioe){
            logger.error(ioe.getMessage(), ioe);
            return null;
        }catch (MyException me){
            logger.error(me.getMessage(), me);
            return null;
        }

        return filePath;
    }

    //找到最佳文件夹
    private synchronized int findBest(long volume){
        boolean big=false;
        boolean small=false;

        if(volume>bigLimit){
            big=true;
        }
        else if(volume<smallLimit){
            small=true;
        }

        int winner=getWinner(big,small,volume);
        if (winner < 0) {//若不存在最佳文件夹，则需要新建文件夹
            initial();
            winner=getWinner(big,small,volume);
        }
        if (winner < 0) {//若第二次仍无法匹配则说明文件过大
            throw new MyException(MyErrorCode.WRITEFILETOOBIG);
        }

        return winner;
    }

    //获取得分最高的文件夹
    private int getWinner(boolean big,boolean small,long volume){
        int winner=-1;
        double mark=0.0;

        for(int i=0;i<numOfDirectories;i++){
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

    //获取文件夹的空间评分
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

    //获取文件夹的文件数评分
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

    //当文件夹不能满足现有文件的存储需要时，新建文件夹
    private void initial(){
        this.index+=BASIC_NUM_OF_DIRECTORIES;
        for(int i=index;i<index+BASIC_NUM_OF_DIRECTORIES;i++){
            createDir(i);
        }
        this.numOfDirectories+=BASIC_NUM_OF_DIRECTORIES;
    }

    //新建文件夹
    private static void createDir(int i){
        File temp=new File(rootPath+"DIR_"+i);
        if(!temp.exists()){
            temp.mkdirs();
        }

        dirs.add(temp);
        dirSpace.add(FIRST_MAX_DIR_SPACE);
        fileNum.add(FIRST_MAX_NUM_OF_FILE);
    }

    //减少文件夹空间
    private synchronized void decreaseSpace(int itr, long volume) {
        long space=dirSpace.get(itr)-volume;
        dirSpace.set(itr,space);
    }

    //减少文件夹剩余可存储的文件数
    private synchronized long decreaseFile(int itr){
        long num=fileNum.get(itr)-1;
        fileNum.set(itr,num);
        return num+1;
    }
}

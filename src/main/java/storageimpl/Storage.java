package storageimpl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wuyuhan
 */

//TODO：(1)并发处理(2)文件夹空间上限和文件数目上限
public class Storage {
    private static Storage storage;

    private final int BASIC_NUM_OF_DIRECTORIES=3;
    private final long FIRST_MAX_DIR_SPACE=new Long("4294967296");//2^32-1
    private final long FIRST_MAX_NUM_OF_FILE=new Long("65536");//2^16-1

    public List<File> dirs=new ArrayList<File>();

    private int numOfDirectories;
    private List<Long> dirSpace;
    private List<Long> fileNum;
    private int index;

    private double a=0.5;
    private double b=0.5;

    private long totalSpace=0;
    private long totalNum=0;

    private long bigLimit;
    private long smallLimit;
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

        //TODO 这里还要增加可修改性
        bigLimit=102000;
        smallLimit=15300;

        index=0;
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
        totalSpace+=space;

        long num=fileNum.get(itr)-1;
        fileNum.set(itr,num);
        totalNum+=num;

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

    //怎么判定什么是最佳方案呢？
    //TODO：这里需要优化
    private int findBest(long volume){

        boolean big=false;
        boolean small=false;

        boolean putin=false;

        if(volume>bigLimit){
            big=true;
        }
        if(volume<smallLimit){
            small=true;
        }

        int winner=getWinner(big,small,volume);
        if(winner<0){
            initial();
            getWinner(big,small,volume);
        }
//        System.out.println(volume+"进入"+winner);
        return winner;
    }

    private int getWinner(boolean big,boolean small,long volume){
        //这里可以改为下次匹配策略
        //TODO:这里需要做代码优化
        int winner=0;
        double mark=0.0;
        boolean putin=false;

        for(int i=index;i<dirSpace.size();i++){
            if(volume<=dirSpace.get(i)&&fileNum.get(i)!=0){
                //剩余空间
                double markTemp=getSpaceMark(big,small,i)+getNumMark(big,small,i);
                if(markTemp>mark) {
                    winner = i;
                    mark=markTemp;
                }
                putin=true;
            }
        }
        if(putin) {
            return winner;
        }
        return -1;
    }

    //TODO 参数可能有点问题？？？
    private double getSpaceMark(boolean big,boolean small,int i){
        if(big){
            return (a-0.3)*(FIRST_MAX_DIR_SPACE-dirSpace.get(i))/(double)FIRST_MAX_NUM_OF_FILE;
        }
        else if(small){
            return (a+0.3)*(dirSpace.get(i)/(double)FIRST_MAX_NUM_OF_FILE);
        }
        else{
            return a*(dirSpace.get(i)/(double)FIRST_MAX_NUM_OF_FILE);
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
            return b*fileNum.get(i);
        }
    }

    //TODO （1）要避免代码重复（2）考虑新增一部分也满了的情况
    private void initial(){
        for(int i=index+BASIC_NUM_OF_DIRECTORIES;i<index+2*BASIC_NUM_OF_DIRECTORIES;i++){
            File temp=new File("DIR_"+i);
            if(!temp.exists()){
                temp.mkdirs();
            }

            dirs.add(temp);
            dirSpace.add(FIRST_MAX_DIR_SPACE);
            fileNum.add(FIRST_MAX_NUM_OF_FILE);
        }
        index=BASIC_NUM_OF_DIRECTORIES;
    }
}

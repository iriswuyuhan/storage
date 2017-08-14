package util;

public enum MyErrorCode {
    WRITEFILENOTFOUND("WRITE01","找不到文件路径"),
    WRITEIOEXCEPTION("WRITE02","写文件IOEXCEPTION"),
    READFILENOTFOUND("READ01","找不到文件路径"),
    READIOEXCEPTION("READ02","无法读完文件");

    private String value;
    private String desc;

    private MyErrorCode(String value,String desc){
        this.setValue(value);
        this.setDesc(desc);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString(){
        return "["+this.value+this.desc;
    }
}

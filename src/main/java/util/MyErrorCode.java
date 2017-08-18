package util;

public enum MyErrorCode {
    WRITEFILETOOBIG("WRITE01","文件过大，无法写入"),
    FILEEXISTS("WRITE02","文件已存在");

    private String value;
    private String desc;

    MyErrorCode(String value, String desc){
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
        return "["+this.value+":"+this.desc+"]";
    }
}

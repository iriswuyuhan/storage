package com.wisdom.nj.storage.storageexception;

public class MyException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public MyException(Object Obj) {
        super(Obj.toString());
    }
}

package com.forezp.cut;

/**
 * Created by liangbing on 2018/5/16.
 * Desc:
 */
public class FileCutService {

    private String fileName;
    private int size;

    public FileCutService(String fileName, String size) {
        this.fileName = fileName;
        this.size = Integer.parseInt(size) * 1024;
    }
}

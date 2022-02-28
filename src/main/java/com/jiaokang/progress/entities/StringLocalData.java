package com.jiaokang.progress.entities;

/**
 * Created by jiaokang on 2022/2/25
 */
public class StringLocalData {
    /**
     * 当前数据所属语言区域
     */
    private final String mLocal;
    /**
     * 当前数据所属模块
     */
    private final String mModule;
    private final String key;
    private final String value;
    public StringLocalData(String module,String local,String key, String value){
        mModule = module;
        mLocal = local;
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "StringLocalData{" +
                "mLocal='" + mLocal + '\'' +
                ", mModule='" + mModule + '\'' +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public String getLocal(){
        return mLocal;
    }

    public String getModule(){
        return mModule;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}

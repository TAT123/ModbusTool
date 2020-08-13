package com.zfl.gsonMessage;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Result<T> {
    public String type;
    public boolean isList;
    public long ftmId;
    public T data;

    public Result(String type, boolean isList, long ftmId) {
        this.type = type;
        this.isList = isList;
        this.ftmId = ftmId;
    }

    public Result(String type, boolean isList, long ftmId, T data) {
        this.type = type;
        this.isList = isList;
        this.ftmId = ftmId;
        this.data = data;
    }

    public <T>  String GetGsonString(){
        Gson gson = new GsonBuilder().setDateFormat("yy-MM-dd HH:mm:ss").create();
        String jsonstr = gson.toJson(this);
        return jsonstr;
    }


}



package com.vinod.sqlitedatabaseapp;

public class BaseStudentDO {
    public int id;
    public String name ="";
    public Integer rollno  ;
    public Double salary;

    public    Integer  isEnable  ;

    public boolean  getIsEnable() {
        return isEnable ==0? false: true;
    }
    public void   setIsEnable(boolean enableOrNot  ) {
        isEnable = enableOrNot?1:0;
    }
}

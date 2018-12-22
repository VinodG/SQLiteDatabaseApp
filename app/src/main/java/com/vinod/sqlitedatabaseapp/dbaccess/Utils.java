package com.vinod.sqlitedatabaseapp.dbaccess;


public class Utils {
    public  static  String getUpdatedStringFromArrayForUpdateQueryString( String [] arr,boolean isForUpdateQuery)
    {
        String str =" ";
        for(int i = 0;i<arr.length;i++)
        {
            if(isForUpdateQuery)
                str = str + arr[i] + " = ?,";
            else
                str= str + arr[i] + ",";
        }
        return  removeLastChar(str);
    }
    public static String removeLastChar(String str) {
        str = str.substring(0,str.length()-1);
        return str;
    }
    public static  String getInsertQuestionMarksString(int size )
    {
        String str = " ";
        for(int i =0;i<size ;i++)
        {
            str =str +"?,";
        }
        return removeLastChar(str);
    }


}

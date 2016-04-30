package com.ritesh.imei;

import android.app.Application;

/**
 * Created by rites on 28-04-2016.
 */
public class Helper extends Application{
    private static Helper ourInstance = new Helper();
    public static Helper getInstance() {
        return ourInstance;
    }
    private Helper() {
    }
    public static class IMEIClass{
        private static final int NameIndex=0;
        private static final int IMEINoIndex=1;
        private static final int CustomerNameIndex=2;
        private static final int CustomerAddIndex=3;
        private static final int CompanyIndex=4;
        private static final int ModelIndex=5;
        private static final int DateIndex=6;
        private static final int DescIndex=7;
        private static final int StringVarCount=8;


        long id;
        String Name,IMEINo,Company,Model,CustomerName,CustomerAdd,EntryDate,Desc;
        String Strings[];
        public IMEIClass(){
            id=-1;
            Strings=new String[StringVarCount];
            for(int i=0;i<StringVarCount;i++)
            {
                Strings[i]="";
            }
            Name=Strings[NameIndex];
            IMEINo=Strings[IMEINoIndex];
            CustomerName=Strings[CustomerNameIndex];
            CustomerAdd=Strings[CustomerAddIndex];
            Company=Strings[CompanyIndex];
            Model=Strings[ModelIndex];
            EntryDate=Strings[DateIndex];
            Desc=Strings[DescIndex];
        }
        public IMEIClass(int id,String[] details){
            this.id=id;
            for(int i=0;i<StringVarCount;i++)
            {
                Strings[i]=details[i];
            }
        }

    }
}

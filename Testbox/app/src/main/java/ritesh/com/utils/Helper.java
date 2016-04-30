package ritesh.com.utils;

import android.content.Context;
import android.content.res.Resources;
import android.provider.ContactsContract;
import android.widget.Toast;

import ritesh.com.testbox.R;

/**
 * Created by rites on 20-03-2016.
 */
public class Helper{
private static Helper helper;
    //  Helper.get().showToast(getActivity().getApplicationContext(),"in the details fragment");
    public void showToast(Context context,String string)
    {
        Toast.makeText(context,string,Toast.LENGTH_LONG).show();
    }
    public static String getStringFromListIndex(Resources Res, int index)
    {
        String [] listValues;
        listValues=Res.getStringArray(R.array.listitems);
        return (listValues[index]);
    }

    public synchronized static Helper get() {
        if(helper==null)
            helper=new Helper();
        return helper;
    }
    public Helper()
    {

    }

    public static class DbHelper
    {

    }
}

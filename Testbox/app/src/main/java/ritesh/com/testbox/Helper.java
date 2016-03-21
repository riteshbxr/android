package ritesh.com.testbox;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

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

    public static Helper get() {
        if(helper==null)
            helper=new Helper();
        return helper;
    }
    public Helper()
    {

    }
}

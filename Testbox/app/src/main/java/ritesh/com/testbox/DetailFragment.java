package ritesh.com.testbox;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by rites on 19-03-2016.
 */

public class DetailFragment extends Fragment {
   private static TextView txtDetail;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       // return inflater.inflate(R.layout.fragment_detail,container,false);
       if(container==null)
            return  null ;
        txtDetail = new TextView(getActivity());
        txtDetail.setText(Helper.getStringFromListIndex(getResources(),gettheIndex()));
        return txtDetail;
    }

    public static DetailFragment NewDetailFragment (int i) {

        DetailFragment df=new DetailFragment();
        Bundle f= new Bundle();
        f.putInt("index", i);
        df.setArguments(f);
        return (df);

    }
    public int gettheIndex()
    {
        //Helper.get().showToast(getActivity().getApplicationContext(),"Index value "+getArguments().getInt("index", 0));
        return getArguments().getInt("index", 0);

    }
}

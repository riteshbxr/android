package ritesh.com.testbox;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by rites on 19-03-2016.
 */

public class ListFragment extends Fragment{
    public   ListView listView;
    public boolean dualPan=false;
    public int curpos=0;
    public  interface onItemSelectedListener{
        public void onItemSelect(int i);
    }
    private onItemSelectedListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return  inflater.inflate(R.layout.fragment_list,container,false);

       // return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView=(ListView) getActivity().findViewById(R.id.listView);
        ArrayAdapter<CharSequence> aa=  ArrayAdapter.createFromResource(this.getActivity(), R.array.listitems, android.R.layout.simple_list_item_1);
        listView.setAdapter(aa);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listener.onItemSelect(i);
            }
        });
    }


  @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (onItemSelectedListener) activity;
        } catch (ClassCastException exp) {
            throw new ClassCastException(activity.toString() + " must implement onItemSelecteded method");
        }
    }
}

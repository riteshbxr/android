package ritesh.com.testbox;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by rites on 19-03-2016.
 */

public class ListFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return  inflater.inflate(R.layout.fragment_list,container,false);

       // return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView listView=(ListView) getActivity().findViewById(R.id.listView);
        ArrayAdapter<CharSequence> aa=  ArrayAdapter.createFromResource(this.getActivity(), R.array.listitems, android.R.layout.simple_list_item_1);
        listView.setAdapter(aa);

    }
}

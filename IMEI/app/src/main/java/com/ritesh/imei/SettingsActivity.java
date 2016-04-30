package com.ritesh.imei;

import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Button;

import java.util.List;

import com.ritesh.imei.R;

public class SettingsActivity extends Activity {
    public static String WholeSaleRate="WSRate";
    public static String RetailSaleRate="RetailRate";
    public static String MasterCostCode="CostCode";
    public static String ComplexCostCode="ComplexCode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,new prefFragment()).commit();

    /*    if(hasHeaders()){
            Button button =new Button(this);
            button.setText("first entry");
            setListFooter(button);
        }*/
    }

  /*  @Override
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);
        loadHeadersFromResource(R.xml.pref_headers,target);
    }
*/
    public static  class prefFragment extends PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            PreferenceManager.setDefaultValues(getActivity(),R.xml.pref_general,true);
            addPreferencesFromResource(R.xml.pref_general);
        }
}
}

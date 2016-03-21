package ritesh.com.testbox;

//import android.support.v7.app.ActionBarActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;


public class DetailActivity extends Activity {// implements onItemSelectedlistener{

    private static int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE)
            finish();
       // this.getIntent().getIntExtra("index", 0);
        setContentView(R.layout.activity_detail);
        if(savedInstanceState==null) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            DetailFragment fragDetails = new DetailFragment();
            fragDetails.setArguments(getIntent().getExtras());
            ft.add(R.id.frame_detail, fragDetails);

            //    ft.addToBackStack(null);
            ft.commit();
        }
        else
        {
            index=savedInstanceState.getInt("index");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("index",index);
    }
}

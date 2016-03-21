package ritesh.com.testbox;

//import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends Activity  implements ListFragment.onItemSelectedListener{

    private boolean dualPan;
    private static int  curpos=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View detailFrame=findViewById(R.id.frame_detail);
        dualPan=detailFrame!=null && detailFrame.getVisibility()==View.VISIBLE;
        if(savedInstanceState!=null)
          showDetails(savedInstanceState.getInt("index", 0),false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id)
        {
            case R.id.action_alertbox:
                AlertDialog.Builder builder =new AlertDialog.Builder(this).setMessage(getString(R.string.AlertBoxMessage))
                        .setTitle(getString(R.string.action_alertbox));
                AlertDialog dialog= builder.create();
                dialog.show();
            return true;
            case R.id.action_toast:
                Toast.makeText(getApplicationContext(),getString(R.string.toastMsg),Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelect(int i) {

        showDetails(i);
    }

    private void showDetails(int i){
        showDetails(i,true);

    }   private void showDetails(int i,boolean detailspage) {
        curpos = i;
        if (dualPan) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            DetailFragment detailFragment = (DetailFragment) fm.findFragmentById(R.id.frame_detail);
            detailFragment = DetailFragment.NewDetailFragment(i);
            ft.replace(R.id.frame_detail, detailFragment);
            ft.addToBackStack(null);
            ft.commit();
        } else if(detailspage){
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("index", i);
            startActivity(intent);
            //  Helper.get().showToast(getApplicationContext(),"details pane not found");
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("index",curpos);
    }

}

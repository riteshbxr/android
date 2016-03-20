package ritesh.com.testbox;

//import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.AlertDialog;
<<<<<<< HEAD
import android.app.FragmentManager;
import android.app.FragmentTransaction;
=======
>>>>>>> refs/remotes/origin/master
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
<<<<<<< HEAD
        FragmentManager fm=getFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        DetailFragment fragDetails=new DetailFragment();
        ft.add(R.id.frame_detail,fragDetails);
        ft.addToBackStack(null);
        ft.commit();
=======
>>>>>>> refs/remotes/origin/master
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
}

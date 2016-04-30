package com.ritesh.imei;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements TextView.OnEditorActionListener {
    EditText txtDp, txtWholesale, txtRetail, txtCost,txtCostCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtDp = (EditText) findViewById(R.id.txtDp);
        txtRetail = (EditText) findViewById(R.id.txtRetail);
        txtCost = (EditText) findViewById(R.id.txtCost);
        txtWholesale = (EditText) findViewById(R.id.txtWholesale);
        txtCostCode = (EditText) findViewById(R.id.txtCostCode);
        txtCostCode.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        txtDp.setOnEditorActionListener(this);
        txtCost.setOnEditorActionListener(this);
        txtCostCode.setOnEditorActionListener(this);
        setTitle(R.string.price_calc);
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
        if (id == R.id.action_settings) {
            Intent intent=new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void updateOnCost(double cost,boolean flushDP){
        updateOnCost(cost,flushDP,!flushDP);
    }
    private void updateOnCost(double cost,boolean flushDP,boolean updateCost)
    {
        updateOnCost(cost, flushDP, updateCost, true);
    }
    private void updateOnCost(double cost,boolean flushDP,boolean updateCost,boolean updateCostCode)
    {
        if(flushDP)
            txtDp.setText("0");
        if(updateCost)
            txtCost.setText(String.valueOf(cost));
        if(updateCostCode)
            txtCostCode.setText(getCodeFromCost((int) Math.round(cost)));
        double nextnum=0;
        SharedPreferences p= PreferenceManager.getDefaultSharedPreferences(this);
        double rate=parseDouble(p.getString(SettingsActivity.RetailSaleRate,"0"));
        nextnum=cost+cost/100*rate;
        txtRetail.setText(String.valueOf(nextnum));
        rate=parseDouble(p.getString(SettingsActivity.WholeSaleRate, "0"));
        nextnum=cost+cost/100*rate;
        txtWholesale.setText(String.valueOf(nextnum));
    }

    private void updateOnCost()
    {
        double cost;
        try {
            cost=Double.parseDouble(txtCost.getText().toString());
        }catch (NumberFormatException ex)
        {
            cost = 0;
        }
        updateOnCost(cost, true);
    }
private double parseDouble(String value)
{
    double dp=0;
    try {
        dp=Double.parseDouble(value);
    }catch (NumberFormatException ex)
    {
        dp=0;
    }
    return dp;
}
    private void updateOnDP()
    {
            double dp=parseDouble(txtDp.getText().toString());
            double nextnum=0;
            nextnum=dp-(dp*5/100);
            nextnum=dp-(nextnum*2.5/100);
            updateOnCost(nextnum,false);
    }
    private void updateOnCostCode()
    {
        String code=txtCostCode.getText().toString();
        double cost=getCostFromCode(code);
        if (cost<0)
        {
            Toast.makeText(getApplicationContext(),"Wrong Code!!",Toast.LENGTH_SHORT).show();
            cost=0;
        }
        updateOnCost(cost,true,true,false);
    }
    private double getCostFromCode(String code)
    {
        SharedPreferences p=PreferenceManager.getDefaultSharedPreferences(this);
        String Master=p.getString(SettingsActivity.MasterCostCode, "1234567890");
        double value=0;
        int valcharacter;
        boolean found=false;
        for(int j=0, i=code.length()-1;i>=0;i--,j++)
        {
            valcharacter=0;
            if(code.charAt(i)=='+')
                found=true;
            else
                found=false;
            for(int k=0;k<Master.length() && !found;k++)
            {
                if(Master.charAt(k)==code.charAt(i))
                {
                    found=true;
                    valcharacter=k<9?k+1:0;
                }

            }
            if(!found) return -1;
            value+=valcharacter*Math.pow(10,j);
            //
        }
        return value;
    }

    private String getCodeFromCost(int cost)
    {
        SharedPreferences p=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String Master=p.getString(SettingsActivity.MasterCostCode, "1234567890");
        String value="";
        int valcharacter=0;
        boolean found=false;
        while(cost>0)
        {
            valcharacter=cost%10;
            Log.d("costcode",valcharacter+"");
            cost=cost/10;
            value=Master.charAt(valcharacter>0?valcharacter-1:9)+value;
        }
        return value;
    }
    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        switch (textView.getId())
        {
            case R.id.txtDp:
                updateOnDP();;
                break;
            case R.id.txtCost:
                updateOnCost();
                break;
            case R.id.txtCostCode:
                updateOnCostCode();
               //

        }
        return false;
    }
}


package com.projects.em;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;

public class CalcActivity extends Activity implements OnClickListener
{
    EditText editText;
    Button buttonPlus,buttonMinus,buttonMultiply,
                buttonDivide,buttonEqual,buttonPoint,buttonBack,buttonReset,buttonDone,buttonTotal;
    Button[] button;     
    String sum="",EditTextMsg;    
    Integer countOne=0;
    Float result=0f,result_mul=1f,result_div=1f,floatEditTextMsg;
	String remValue="",totValue="";
    Intent me;
    int c;
    char press;
    
    Vibrator vibrator;
       
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	Log.w("From Long Click","Creating view");
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);
        button=new Button[10];
        
        editText=(EditText)findViewById(R.id.editText1);

        button[0]=(Button)findViewById(R.id.button0);
        button[1]=(Button)findViewById(R.id.button1);
        button[2]=(Button)findViewById(R.id.button2);
        button[3]=(Button)findViewById(R.id.button3);
        button[4]=(Button)findViewById(R.id.button4);
        
        button[5]=(Button)findViewById(R.id.button5);
        button[6]=(Button)findViewById(R.id.button6);
        button[7]=(Button)findViewById(R.id.button7);
        button[8]=(Button)findViewById(R.id.button8);
        button[9]=(Button)findViewById(R.id.button9);
        for(int i=0;i<10;i++)
        	button[i].setOnClickListener(this);
        
        buttonPlus=(Button)findViewById(R.id.buttonplus);
        buttonPlus.setOnClickListener(this);
        buttonMinus=(Button)findViewById(R.id.buttonminus);
        buttonMinus.setOnClickListener(this);
        buttonMultiply=(Button)findViewById(R.id.buttonmultiply);
        buttonMultiply.setOnClickListener(this);
        buttonDivide=(Button)findViewById(R.id.buttondivide);
        buttonDivide.setOnClickListener(this);
        buttonPoint=(Button)findViewById(R.id.buttonpoint);
        buttonPoint.setOnClickListener(this);
        buttonReset=(Button)findViewById(R.id.buttonreset);
        buttonReset.setOnClickListener(this);
        buttonEqual=(Button)findViewById(R.id.buttonequal);
        buttonEqual.setOnClickListener(this);
        buttonTotal=(Button)findViewById(R.id.buttontotal);
        buttonTotal.setOnClickListener(this);
        Log.w("From Long Click","Here5");
        buttonBack=(Button)findViewById(R.id.buttonback);
        buttonBack.setOnClickListener(this);
        ((Button)findViewById(R.id.buttondone)).setOnClickListener(this);
        ((Button)findViewById(R.id.buttonrem)).setOnClickListener(this);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        buttonBack.setOnLongClickListener( new OnLongClickListener() {
			
    			@Override
    			public boolean onLongClick(View v) {
    				// TODO Auto-generated method stub
    				sum="";
    				editText.setText("");
    				vibrator.vibrate(100);
    				return false;
    			}
    		});        
        
    }
    

    @Override
    public void onResume() 
    {
    	super.onResume();
    	me=getIntent();
    	remValue=me.getStringExtra("rem");
    	totValue=me.getStringExtra("total");
    	sum=me.getStringExtra("Value");
    	editText.setText(sum);
    	press='=';
    }
    
     
	@Override
	public void onClick(View view) {
		
		switch(view.getId())
		{
			case R.id.button0:
		        if(press=='=')
		            onClick(buttonReset);
		        
		        	if(sum !="")
			        {
			            sum=sum+(String)button[0].getText();;
			            editText.setText(sum);
			        }
			        else
			        {
			            sum="0";
			            editText.setText("0");
			        }
		        break;
			case R.id.button1:
			case R.id.button2:
			case R.id.button3:
			case R.id.button4:
			case R.id.button5:
			case R.id.button6:
			case R.id.button7:
			case R.id.button8:
			case R.id.button9:				
		        if(press=='=')
		            onClick(buttonReset);
				Button btnclicked=(Button)view;
		        sum=sum+(String)btnclicked.getText();
		        editText.setText(sum);
				break;
			case R.id.buttonrem:
				if(press=='=')
		            onClick(buttonReset);
				sum=remValue;
		        editText.setText(sum);
				
				break;
			case R.id.buttontotal:
				if(press=='=')
		            onClick(buttonReset);
				sum=totValue;
		        editText.setText(sum);
				
				break;
			case R.id.buttonplus:
				if(press=='-' ||press=='*'||press=='/')
		        {
		            onClick(buttonEqual);
		        }
		        press='+';
		        if(sum != "")
		        {
		            result=result+Helper.getFloatIfParsable(editText.getText().toString());		            
		            editText.setText(result.toString());
		            result_mul=result;
		            result_div=result;
		            sum="";            
		        }
		        else
		        {
		            editText.setText(result.toString());
		            result_mul=result;	            
		            result_div=result;
		            sum="";
		        }

				break;
			case R.id.buttonminus:
				if(press=='*' ||press=='+'||press=='/')
		        {
		            onClick(buttonEqual);
		        }
		        press='-';
		        
		        EditTextMsg= editText.getText().toString(); 
		        floatEditTextMsg=Helper.getFloatIfParsable(EditTextMsg);
		        
		        if(sum==""  && result==0)
		        {
		            sum=sum+'-';
		            //Log.d("sum=","minus press");
		        }
		        else if(sum != "")
		        {
		            if(result==0)
		            {
		                result=Helper.getFloatIfParsable(sum)-result;
		                
		                editText.setText(result.toString());
		                
		                result_mul=result;
		                
		                result_div=result;
		                
		                sum="";
		            }
		            
		            else
		            {
		                result=result-Helper.getFloatIfParsable(sum);
		                
		                editText.setText(result.toString());
		                
		                result_mul=result;
		                
		                result_div=result;
		                
		                sum="";
		            }
		        }
		    
				break;
			case R.id.buttondivide:
				if(press=='-' ||press=='+'||press=='*')
		        {
		            onClick(buttonEqual);
		        }

		        press='/';
		        
		        EditTextMsg= editText.getText().toString(); 
		        floatEditTextMsg=Helper.getFloatIfParsable(EditTextMsg);
		        
		        if(sum !="" && result_div==1)
		        {
		            //int c=0;
		            
		            if(c==0)
		            {
		                result_div=floatEditTextMsg/result_div;  
		                Log.d("if if result_div=", result_div.toString());
		                c++;
		            }
		            else
		            {
		                result_div=result_div/floatEditTextMsg; 
		                Log.d("if else result_div=", result_div.toString());
		            }
		            
		            
		            result=result_div;
		            result_mul=result_div;
		            
		            editText.setText(result_div.toString());
		            
		            sum="";
		        }
		        else if(sum !="" && result_div !=1)
		        {
		            result_div=result_div/floatEditTextMsg; 
		            
		            Log.d("else if result_div=", result_div.toString());
		            
		            result=result_div;
		            
		            result_mul=result_div;
		            
		            editText.setText(result_div.toString());
		            
		            sum="";
		        }
		        else
		        {
		            editText.setText(EditTextMsg);
		            
		            sum="";
		        }
				break;
			case R.id.buttonmultiply:
				if(press=='-' ||press=='+'||press=='/')
		        {
		            onClick(buttonEqual);
		        }

		        
		        
		        press='*';
		        
		        EditTextMsg= editText.getText().toString(); 
		        floatEditTextMsg=Helper.getFloatIfParsable(EditTextMsg);
		        
		        if(sum !="")
		        {
		            result_mul=result_mul * floatEditTextMsg; 
		            
		            result=result_mul;
		            
		            result_div=result_mul;
		            
		            editText.setText(result_mul.toString());
		            
		            sum="";
		        }
		        else
		        {
		            editText.setText(EditTextMsg);
		            
		            //result_mul=result_mul * Helper.getFloatIfParsable(sum);
		            
		            //result=result_mul;
		            
		            sum="";
		        }
		        
				break;
			case R.id.buttonpoint:		        
			       int error=0;
			        if(sum !=null)
			        {
			            for(int i=0;i<sum.length();i++)
			            {
			                if(sum.charAt(i)=='.')
			                {
			                    error=1;
			                    break;
			                }
			            }

			        }
			        
			        if(error==0)
			        {
			            if(sum==null)
			            {
			                sum=sum+"0.";
			            }
			            else
			            {
			                sum=sum+".";
			            }
			        }
			       
			        editText.setText(sum);
			    break;
			case R.id.buttonequal:
				if(press=='+') 
		        {
		            onClick(buttonPlus);
		            //msg1= editText.getText().toString(); 
		            //floatMsg=Helper.getFloatIfParsable(msg1);
		        }
		        else if(press=='-') 
		        {
		            onClick(buttonMinus);
		        }
		        else if(press=='*') 
		        {
		            onClick(buttonMultiply);
		        }
		        else if(press=='/')
		        {
		            onClick(buttonDivide);
		        }
		        
		        press='=';
				break;
			case R.id.buttonreset:
				 vibrator.vibrate(30);
			        sum="";
			        countOne=0;//result=0;
			        result=0f;
			        result_mul=1f;
			        result_div=1f;
			        press=' ';
			        c=0;
			        
			        editText.setText(result.toString());
				break;
			case R.id.buttonback:
		         vibrator.vibrate(30);
		         if(sum !="")
		         {
		             StringBuilder stringBuilder=new StringBuilder(80);
		             stringBuilder.append(sum);
		             sum=stringBuilder.deleteCharAt(stringBuilder.length()-1).toString();		             
		             editText.setText(sum);
		         }

				break;
			case R.id.buttondone:
			vibrator.vibrate(30);
			Intent intent=new Intent();
			Float value=Helper.getFloatIfParsable(editText.getText().toString());
			String.format("%.1f",value);
			intent.putExtra("Value", String.format("%.1f",value));
			intent.putExtra("EditTextPosition", me.getIntExtra("EditTextPosition", -1));
			setResult(RESULT_OK, intent);
			finish();
	        break;
		}
		
	}
}
package com.azimov.laserteaser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.GridView;
import android.widget.LinearLayout;

public class MainActivity  extends Activity
{
	
	ImageAdaptor ad;
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		Display display = getWindowManager().getDefaultDisplay();
		

		LightGame.getInstance().setGameMode(LightGame.DEFAULT_MODE);
		GridView gv = new GridView(this);
		
		try {
		    Method setLayerTypeMethod = gv.getClass().getMethod("setLayerType", new Class[] {int.class, Paint.class});
		    
				setLayerTypeMethod.invoke(gv, new Object[] {View.LAYER_TYPE_SOFTWARE, null});
			
		} catch (NoSuchMethodException e)
		{
		    // Older OS, no HW acceleration anyway
		} catch (IllegalArgumentException e)
		{
		    e.printStackTrace();
		} catch (IllegalAccessException e)
		{
		    e.printStackTrace();
		} catch (InvocationTargetException e)
		{
		// TODO Auto-generated catch block
	    	e.printStackTrace();
	    }

		
		gv.setNumColumns(5);
		gv.setHorizontalSpacing(display.getWidth()/18);
		gv.setVerticalSpacing(display.getWidth()/18);
	//	gv.setColumnWidth(10);
		gv.setGravity(Gravity.BOTTOM);
		
	/*	GradientDrawable backgroundPaint = new GradientDrawable(
				GradientDrawable.Orientation.TL_BR, new int[] { Color.BLACK,
						Color.DKGRAY });
		
		backgroundPaint.setShape(GradientDrawable.RECTANGLE);
		
		
		int width = display.getWidth();
		int height = display.getHeight();
		backgroundPaint.setBounds(new Rect(0,0,width,height));
		gv.setBackground(backgroundPaint);*/
		
		
		
		LevelPanel.setPanelSize(display.getWidth()/7);
		
		ad = new ImageAdaptor(this);

			gv.setAdapter(ad);
			
		//	GridView.LayoutParams lp = new GridView.LayoutParams(85, 85);
			//lp.height =GridView.LayoutParams.WRAP_CONTENT;
			//lp.width =GridView.LayoutParams.WRAP_CONTENT;
			
			//gv.setLayoutParams(lp);
			//gv.setStretchMode(1);
			//gv.setColumnWidth(10);
			
		gv.setBackgroundColor(Color.BLACK);
		
		gv.setOnItemClickListener(new OnItemClickListener()
		{
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
            {
            	 if ( ((LevelPanel)v).isEnabled())
            	 {
            		 
            		 Intent intent = new Intent(MainActivity.this, LevelActivity.class);
            	    
            	 
            	    intent.putExtra("EXTRA_MESSAGE", position);
            	    startActivity(intent);
            	 }
            }

		});
		LightGame.getInstance().setContext(this);
		LightGame.getInstance().loadGameData();
		ad.refreshViews();
		setContentView(gv);
		
		
	}
	
	public void onResume()
	{
		super.onResume();
		ad.refreshViews();
	}


}

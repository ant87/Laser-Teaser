package com.azimov.laserteaser;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ImageAdaptor extends BaseAdapter
{
	
	Context mContext;
	LightGame game;
	
	
	public ImageAdaptor(Context c)
	{ 
        mContext = c;
        game = LightGame.getInstance();
        
        for(int i=0; i<lps.length;i++)
        {
        	if (game.isLevelEnabled(i))
    		
            	lps[i] = new LevelPanel(mContext,i,true);
        	else
        		lps[i] = new LevelPanel(mContext,i,false);
        	
        }
        
        
       
    }
	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return lps.length;
	}

	@Override
	public Object getItem(int arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0)
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void refreshViews()
	{
		for (LevelPanel lp: lps)
		{
		if (game.isLevelEnabled(lp.getLevel()) )
		{
			//System.out.println("highj "+LightGame.getInstance().getHighestLevel());
			lp.setEnabled(true);
		}
		else
		{
			lp.setEnabled(false);
		}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2)
	{
		// TODO Auto-generated method stub
		
		
		return lps[position];
		
		
		
	}
	
	
	
	LevelPanel[] lps = new LevelPanel[15];

}

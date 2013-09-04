package com.azim.laserteaser;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

public class LevelPanel extends View
{
	private static int size;
	RectF rectFF = new RectF();
	Paint enabledPaint = new Paint();
	Paint disabledPaint = new Paint();
	Paint enabledTextP = new Paint();
	Paint disabledTextP = new Paint();
	//Paint text = new Paint();
	
	EmbossMaskFilter embos;
	
	Rect textBounds = new Rect();
	
	
	
	int level;
	boolean enabled;
	//private Paint enabledColour = new Paint();
	
	public LevelPanel(Context ctx, int level, boolean enabled)
	{
		super(ctx);
		this.level = level;
		this.enabled = enabled;
		

embos = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.5f, 4.0f, 10.0f);
enabledPaint.setMaskFilter(embos); 
disabledPaint.setMaskFilter(embos); 
enabledTextP.setMaskFilter(embos);
disabledTextP.setMaskFilter(embos);

enabledPaint.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(new float[]{
		0,0,0,0,0
           ,0.5f,0,0.5f,0,1
           ,1,0,1,1,1,
           0.5f,0.5f,0.5f,0.5f,0.5f,})));

enabledPaint.setStyle(Style.FILL_AND_STROKE);

/*enabledTextP.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(new float[]{
		0.1f,0.5f,0.1f,0.1f,1
		,0,0,0,0,0,
		0,0,0,0,0,
        1,1,1,1,1,})));*/

enabledTextP.setColor(Color.YELLOW);

disabledPaint.setStyle(Style.STROKE);
disabledPaint.setColor(Color.DKGRAY);
disabledTextP.setColor(Color.DKGRAY);
		
		
		
		

		setMinimumWidth(size);
		setMinimumHeight(size);
			
	}
	
	public static void setPanelSize(int size)
	{
		LevelPanel.size =size;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	public void onDraw(final Canvas canvas)
	{
		super.onDraw(canvas);
		RectF rect = new RectF(canvas.getClipBounds());
		rectFF.bottom = rect.bottom-4;
		rectFF.top = rect.top+4;
		rectFF.left = rect.left+4;
		rectFF.right = rect.right-4;
		

	
	

	
		Paint p;
		Paint textP;
	
	
	if (enabled)
	{
	

p = enabledPaint;
textP = enabledTextP;

	}
	else
	{
		p = disabledPaint;
		textP = disabledTextP;
		
	}
		
	textP.setTextSize(rectFF.width()/3);

	
		
		//canvas.drawRoundRect(rectFF,7,7, solidP);
		
		String str =Integer.toString(level+1);
		textP.getTextBounds(str, 0, str.length(), textBounds);
		
		p.setStrokeWidth(7);
		p.setMaskFilter(embos);
		textP.setMaskFilter(embos); 
		
		canvas.drawRoundRect(rectFF,7,7, p);
		canvas.drawText(str, rect.centerX()-(textBounds.width()/2), rect.centerY()+(textBounds.height()/2), textP);
		
		
		
		//setBackground(background)
	}
}

package com.azim.laserteaser;

import java.util.ArrayList;

import com.azim.laserteaser.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnTouchListener;

class Panel extends View implements OnTouchListener
{
	private static final int[] colours = new int[] {Color.YELLOW,Color.GREEN,Color.BLUE, Color.CYAN,Color.MAGENTA,Color.WHITE};

	
	static int test1 = 0;
	
	private Activity parent;
	


	private static final int REFLECTOR_MOVE_COLOUR = 0xFF00B0FF;//Color.CYAN;
	

	private int reflectorColour = REFLECTOR_MOVE_COLOUR;


	int events = 0;

	int a = 0xFFFF0000;
	int b = 0xFF00FF00;
	int c = 0xFF0000FF;

	GradientDrawable backgroundPaint;

	Paint targetDefaultBlurPaint = new Paint();
	Paint targetSelectedPaint = new Paint();
	Paint targetHitFillPaint = new Paint();
	Paint targetDefaultFillPaint = new Paint();
	

	Paint targetDefaultPaint = new Paint();

	// GradientDrawable reflectorPaint;

	// final Paint paint = new Paint();

	Paint reflectorPaint = new Paint();
	Paint reflectorEdgePaint = new Paint();
	Paint axisPaint = new Paint();
	Paint rayBlurPaint = new Paint();
	Paint raySolidPaint  = new Paint();
	Paint textPaint = new Paint();
	
	Paint raySnapReflectPaint = new Paint();
	
	public static Point pRef;

	static int x;

	LightModel light;
	
	private boolean onSelectArea = false;
	
	Rect clipRect = new Rect();

	 Thread thread = null;
	    SurfaceHolder surfaceHolder;
	    volatile boolean running = false;
	    
	    Drawable arrowIcon;
	    Drawable menuIcon;
	    
	    ColorMatrix matrix=  new ColorMatrix(new float[]{
	    		   1,1,1,1,1
	              ,0,0,0,0,0
	              ,0,0,0,0,0,
	              0.5f,0.5f,0.5f,0.5f,0.5f,});// = new ColorMatrix();
	              
	              ColorMatrix matrix2=  new ColorMatrix(new float[]{
	            		  0.5f,0.5f,0.5f,0.5f,0.5f
	   	              ,0.5f,0.5f,0.5f,0.5f,0.5f
	   	           ,0.5f,0.5f,0.5f,0.5f,0.5f,
	   	              0.5f,0.5f,0.5f,0.5f,0.5f,});// = new ColorMatrix();
	   ColorMatrixColorFilter arrowColorFilter = new ColorMatrixColorFilter(new ColorMatrix(new float[]{
    		   0,0,1,0,0
	              ,0,0,0,0,0
	              ,0,0,0,0,0,
	              0.5f,0.5f,0f,0.5f,0.5f,}));// = new ColorMatrix(););
	    
	              Rect textBounds = new Rect();


				private boolean cycleSelected;
				
				Paint refSelectedPaint = new Paint();
			


	public Panel(Context context, LightModel light)
	{
		super(context);
		// parent = (Observer)context;
		setOnTouchListener(this);
		
		this.light = light;
		int axis = light.AXIS_LENGTH;
		this.parent = (Activity)context;
	//	surfaceHolder = getHolder();
	//	setBackgroundColor(Color.DKGRAY);

	

		backgroundPaint = new GradientDrawable(
				GradientDrawable.Orientation.TL_BR, new int[] { Color.BLACK,
						Color.DKGRAY });
		backgroundPaint.setShape(GradientDrawable.RECTANGLE);
		
		targetDefaultPaint.setColor(Color.BLUE);
		targetDefaultPaint.setAntiAlias(true);
		
		
		targetDefaultPaint.setStyle(Paint.Style.STROKE);
		targetDefaultPaint.setStrokeWidth(1);
		
		targetDefaultBlurPaint.setColor(Color.BLUE);
		targetDefaultBlurPaint.setStyle(Paint.Style.STROKE)	;
		targetDefaultBlurPaint.setStrokeWidth(axis/15);
		targetDefaultBlurPaint.setMaskFilter(new BlurMaskFilter(axis/20, BlurMaskFilter.Blur.NORMAL));

		//targetDefaultBlurPaint.setMaskFilter(new BlurMaskFilter(axis/20, BlurMaskFilter.Blur.NORMAL));
		
		
		targetSelectedPaint.setColor(Color.GREEN );
		targetSelectedPaint.setStyle(Paint.Style.STROKE)	;
		targetSelectedPaint.setStrokeWidth(axis/15);
		targetSelectedPaint.setMaskFilter(new BlurMaskFilter(axis/20, BlurMaskFilter.Blur.NORMAL));
	
		targetHitFillPaint.setColorFilter(new ColorMatrixColorFilter(matrix));
		targetDefaultFillPaint.setColorFilter(new ColorMatrixColorFilter(matrix2));
		
		raySnapReflectPaint.setColor(Color.WHITE);
		raySnapReflectPaint.setPathEffect(new DashPathEffect(new float[]{10,10},5) );

		
		textPaint.setColor(Color.RED);
		
		rayBlurPaint.setStrokeWidth(axis/12);
		
		rayBlurPaint.setMaskFilter(new BlurMaskFilter(axis/15, BlurMaskFilter.Blur.NORMAL));
		
		reflectorEdgePaint.setStrokeWidth(axis/15);
		reflectorEdgePaint.setColor(Color.WHITE);
		reflectorEdgePaint.setMaskFilter(new BlurMaskFilter(axis/20, BlurMaskFilter.Blur.NORMAL));
		
		refSelectedPaint.setColorFilter(new ColorMatrixColorFilter(matrix));
		refSelectedPaint.setStrokeWidth(7);

reflectorPaint.setStrokeWidth(axis/20);
		
		arrowIcon = getResources().getDrawable( R.drawable.ic_arrow );
		menuIcon = getResources().getDrawable( R.drawable.ic_faq_icon );
	
	
		
	}
	
	
	
	

	public void setModel(LightModel mod)
	{
		light = mod;
	}

	@Override
	
	public void onDraw(final Canvas canvas)
	{
		super.onDraw(canvas);

		////System.out.println("ondraw " + test1++);
		canvas.getClipBounds(clipRect);
		
		int height = clipRect.height();
		int width = clipRect.width();
		ArrayList<ReflectorManager.Reflector> refList = light
				.getReflectorList();
		ArrayList<RayManager.SourceRay> sourceRayList = light
				.getSourceRayList();

		Panel.x++;

		backgroundPaint.setBounds(clipRect);

		backgroundPaint.draw(canvas);

		if (light.showDebugText)
		{
			canvas.drawText("Dev mode", 10, 10, textPaint);
			canvas.drawText("Level: " + LightGame.getInstance().getCurrentLevel(),
					10, 30, textPaint);

			canvas.drawText("Reflectors: " + refList.size(), 10, 60, textPaint);
			
			canvas.drawText("Rays: "
					+ RayManager.getInstance().getSourceRayList().size(), 10,
					80, textPaint);
			
			canvas.drawText("Targets: "
					+ TargetManager.getTargetList().size(), 10,
					100, textPaint);
			
			if (ReflectorManager.getInstance().getSelectedReflector() !=null)
			{
			canvas.drawText("Ref points: "
					+ ReflectorManager.getInstance().getSelectedReflector().getReflectionPoints().size(), 10,
					150, textPaint);
			
			canvas.drawText("Ref points: "
					+ ReflectorManager.getInstance().getSelectedReflector().getAngle(), 100,
					200, textPaint);
			
			/*canvas.drawText("Ref snap angle: "
					+ ReflectorManager.getInstance().getSelectedReflector().getSnapAngle(), 200,
					300, textPaint);*/
			}
			
			if(RayManager.getInstance().getSelectedRay()!= null)
			{
				canvas.drawText("Ray angle: "
						+ RayManager.getInstance().getSelectedRay().getAngle(), 200,
						300, textPaint);
				
				
			}
				
			
		}
		
		
	
		
		/********************* draw axis******************/
		
		int x = 0;

		if (light.gridOn)
		{
			axisPaint.setColor(Color.DKGRAY);
			while (x < width)
			{
				x += light.AXIS_LENGTH;
				canvas.drawLine(x, 0, x, height, axisPaint);
				if (x < height)
					canvas.drawLine(0, x, width, x, axisPaint);

			}
			
		}
		
		/********************* draw axis******************/
		



		/******************************* draw rays ********************************/

		// canvas.setComposite(rayComposite);

		// canvas.setStroke(rayStroke);



		for (RayManager.SourceRay sourceRay : sourceRayList)
		{

			// SourceRay sourceRay = raySourceList.get(z);
			// LinkedList rayList = sourceRay.getRayList();
			

           
			int id = sourceRay.getId();
				

			for (RayManager.Ray r1 : sourceRay.getRayList())
			{
				
			
				
				
				
				if (sourceRay.isSelected())
				{
					rayBlurPaint.setColor(Color.RED);
			    	raySolidPaint.setColor(Color.RED);
				}
				else
				{
					if(id < colours.length)
					{
				    	rayBlurPaint.setColor(colours[id]);
				        raySolidPaint.setColor(colours[id]);
					}
					else
					{
						rayBlurPaint.setColor(Color.WHITE);
				    	raySolidPaint.setColor(Color.WHITE);
					}
				}
				Point p1 = r1.getP1();
				Point p2 = r1.getP2();
				
				
				
				canvas.drawLine(p1.x, p1.y, p2.x,
						p2.y, rayBlurPaint);
				
				
				canvas.drawLine(p1.x, p1.y, p2.x,
						p2.y, raySolidPaint);
				
				
			
			}

			canvas.drawCircle(sourceRay.getP1().x, sourceRay.getP1().y, light.AXIS_LENGTH/6,
					rayBlurPaint);
			
			canvas.drawCircle(sourceRay.getP1().x, sourceRay.getP1().y, light.AXIS_LENGTH/15,
					raySolidPaint);
			
			 id++;
			 
			 RayManager.Ray snapR = sourceRay.getSnapRay();
				if(snapR != null)
				{
				canvas.drawLine(snapR.getP1().x, snapR.getP1().y,snapR.getP2().x,
						snapR.getP2().y, raySnapReflectPaint);
				
				   if(light.showDebugText)
				   {
				
				      canvas.drawText("p2.x "+snapR.getP2().x, 500, 500, textPaint);
				      canvas.drawText("p2.y "+snapR.getP2().y, 500, 600, textPaint);
				   }
				}
			 
			
		}

		// canvas.setStroke(originalStroke);

		/*************************** draw reflectors **********************************************/

		// LinkedList rayList = Ray.getRayList();

		// g.drawString(Double.toString(rayList.size()),700,100);

		Point p1;
		Point p2;
		Point mid;
		

		for (ReflectorManager.Reflector r1 : refList)
		{

			p1 = r1.getPoint1();
			p2 = r1.getPoint2();
			mid = r1.getMidPoint();
			
			int refType = r1.getType();
			


				if(refType == ReflectorManager.TYPE_ROTATE)
				{
					reflectorColour = 0xFF009000;	
				}
				else if(refType == ReflectorManager.TYPE_MOVE)
				{
				   reflectorColour = REFLECTOR_MOVE_COLOUR;
				}
				else
				{
					reflectorColour = Color.WHITE;
				}
			

			
			LinearGradient lg = new LinearGradient(p1.x, p1.y, p2.x, p2.y,
					Color.GRAY, reflectorColour, Shader.TileMode.MIRROR);
			
			reflectorPaint.setShader(lg);
		
			canvas.drawLine(p1.x, p1.y, p2.x, p2.y, reflectorEdgePaint);

			if(r1.getType() == ReflectorManager.TYPE_ROTATE)
			       canvas.drawCircle(mid.x, mid.y, light.AXIS_LENGTH/8, reflectorEdgePaint);
			
			canvas.drawLine(p1.x, p1.y, p2.x, p2.y, reflectorPaint);
			
			if(r1.getType() == ReflectorManager.TYPE_ROTATE)	
		        	canvas.drawCircle(mid.x, mid.y, light.AXIS_LENGTH/14, reflectorPaint);
			
			if(r1.isSelected())
			{
			
				canvas.drawLine(p1.x,p1.y,p2.x,p2.y,refSelectedPaint);
			}
			
		
		}
		
		/********************draw targets *********************/
		
		/*Radius is set relative to the axis in TargetManager, so ok to use num of pixels */
		int radius = TargetManager.getInstance().getRadius();
		
		for (TargetManager.Target target : light.getTargetList())
		{
			
			int id = target.getId();
			
			if(id == -1)
			{
				targetDefaultBlurPaint.setColor(Color.WHITE);
				
			}
			else if(id < colours.length)
				
			{
				targetDefaultBlurPaint.setColor(colours[id]);
			
				
			}
			else
			{
				targetDefaultBlurPaint.setColor(Color.WHITE);
			}
			canvas.drawCircle(target.x,target.y,radius,targetDefaultPaint);
			
			canvas.drawCircle(target.x,target.y,radius,targetDefaultBlurPaint);

			if (target.isSelected())
			{
				

				canvas.drawCircle(target.x,target.y,radius+5,targetSelectedPaint);
				

				
			}

			if (target.isHit())
			{
				/*targetHitPaint.setShape(GradientDrawable.OVAL);
				targetHitPaint.setBounds(target.x - radius,
						target.y - radius, target.x
						+ radius, target.y
						+ radius);

				targetHitPaint.draw(canvas);*/
				
				//myIcon.setColorFilter(cmcf);
				canvas.drawCircle(target.x,target.y,radius,targetHitFillPaint);
			}
			else
			{
				canvas.drawCircle(target.x,target.y,radius,targetDefaultFillPaint);	
			}
			

		}
		
		/************ draw targets *************************/
		
		/**********************draw selection area *****/
		double wMargin = 0.1;
		double hMargin = 0.3;
		if(cycleSelected)
		{
		    arrowIcon.setColorFilter(arrowColorFilter);
		}
		else
		{
			arrowIcon.setColorFilter(null);
		}
		arrowIcon.setBounds(width-(int)(light.AXIS_LENGTH*(1-wMargin)), (int)(light.AXIS_LENGTH*hMargin), width-(int)(light.AXIS_LENGTH*wMargin), (int)(light.AXIS_LENGTH*(1-hMargin)));
		arrowIcon.draw(canvas);
		
		if (LightGame.getInstance().mode == LightGame.PLAY)
	    	menuIcon.setBounds((int)(light.AXIS_LENGTH*(wMargin)), (int)(light.AXIS_LENGTH*hMargin), (int)(light.AXIS_LENGTH*(1-wMargin)), (int)(light.AXIS_LENGTH*(1-hMargin)));
		else
			menuIcon.setBounds((int)(light.AXIS_LENGTH*(wMargin))+light.AXIS_LENGTH, (int)(light.AXIS_LENGTH*hMargin), (int)(light.AXIS_LENGTH*(1-wMargin))+light.AXIS_LENGTH, (int)(light.AXIS_LENGTH*(1-hMargin)));
			
		menuIcon.draw(canvas);
		
		/*************Show message ***********************/
		String msg = light.getMessage();
		
		textPaint.setTextSize(light.AXIS_LENGTH/4);
		
		if (msg != null)
		{
		String[] lines =msg.split("\n");
		
		
		
		int screenMid = clipRect.width()/2;
	    int yoff = light.AXIS_LENGTH/2 ;
	    for (String line : lines)
	    {
	       
	    	textPaint.getTextBounds(line, 0, line.length(), textBounds);
	        canvas.drawText(line, screenMid - (textBounds.width()/2), yoff, textPaint);
	        yoff += textBounds.height()+(textBounds.height()/4);
	    }
		}

         /************************************************/
		rayBlurPaint.setTextSize(light.AXIS_LENGTH/2);
		raySolidPaint.setTextSize(light.AXIS_LENGTH/2);
		raySolidPaint.setTypeface(Typeface.create("sans_serif",Typeface.ITALIC));
		
		
		if (LightGame.getInstance().isLevelComplete() == true)
		{
			rayBlurPaint.getTextBounds("Next", 0, "Next".length(),textBounds);
			int start =width -textBounds.width()-light.AXIS_LENGTH/2 ;
			canvas.drawText("Next",start, height -10,rayBlurPaint);
			canvas.drawText("Next",start, height -10,raySolidPaint);
		}


	}

	

	

	@Override
	public boolean onTouch(View arg0, MotionEvent evt)
	{
		//System.out.println("on touch");

		super.onTouchEvent(evt);
		// boolean captured = false;

		switch (evt.getAction())
		{
		case MotionEvent.ACTION_MOVE:
			events = 2;
			
			if(onSelectArea == true)
			{
				return true;
			}
			
			if(LightGame.getInstance().isLevelComplete() == false)
			{
			//System.out.println("action move");
			light.mouseDragged(evt);
			invalidate();
			
			}

			break;

		case MotionEvent.ACTION_DOWN:
			//System.out.println("action down");
			
			float x,y;
			x = evt.getX();
			y = evt.getY();
			events = 1;
			
			
			if((y > (getHeight()-50) )&&(LightGame.getInstance().isLevelComplete() == true))
			{
				/******AG should levelcomplete be called directly from game LightGame class**************************************/
				((LevelActivity) getContext()).levelComplete();
			
			
				invalidate();
				return true;
			}
			
			else
			{
		
				if(light.mousePressed(evt)== false)
				
				{
					/* Cycle select button pressed */
					if( (x>=(getWidth()-light.AXIS_LENGTH))&& (y<=light.AXIS_LENGTH))
					{
	
						light.cycleRefSelection();
						invalidate();
						onSelectArea = true;
						cycleSelected = true;
						return true;
			       
					}
					if(LightGame.getInstance().mode == LightGame.PLAY)
					{
					
						if( (x<light.AXIS_LENGTH)&& (y<=light.AXIS_LENGTH))
						{
		
							parent.openOptionsMenu();
							return true;
				       
						}
					}
					else
					{
						if( x<(light.AXIS_LENGTH*2)&& (x>light.AXIS_LENGTH) && (y<=light.AXIS_LENGTH))
						{
		
							parent.openOptionsMenu();
							return true;
				       
						}
						
					}
				}
			}
			
	

			invalidate();
			break;

		case MotionEvent.ACTION_UP:
			events = 3;
			//System.out.println("action up");
			
			if(onSelectArea == true)
			{
				onSelectArea = false;
				return true;
			}
			else
				cycleSelected = false;

			if (light.gridOn)
			{

				light.reflectorMouseReleased(evt, light.AXIS_LENGTH,
						22.5);
				//light.reflectorMouseReleased(evt);

				light.rayMouseReleased(evt, true,
						light.AXIS_LENGTH, 22.5);
				light.targetMouseReleased(evt, light.AXIS_LENGTH);
			} else
			{
				light.reflectorMouseReleased(evt);

				light.rayMouseReleased(evt, true);
				light.targetMouseReleased(evt);
			}

			
		    
		    LightGame.getInstance().checkLevelComplete();
			invalidate();
			break;

		}
invalidate();
		return true;

	}

	
}



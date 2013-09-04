package com.azim.laserteaser;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Point;

/**
 * 
 * @author User
 */
public class LightGame
{
	public static final int FREE = 0;
	public static final int SET_LEVEL = 1;
	public static final int PLAY = 2;
	public static final int DEFAULT_MODE = PLAY;

	//GameData gameData = new GameData();

	Calendar currentTime;



	public static boolean levelLoaded = false;

	LightModel light;
	// JPanel gui;


	private static String fileName;

	
	private static final String SIM_FILE_NAME = "sim";
	
	
	private static final String FILE_NAME = "level";

	private static final LightGame instance = new LightGame();

	Activity activity;

	Context context;

	Panel pv;
	
	int mode, level,gameHighestLevel;
	
	private boolean levelComplete = false;

	public void setContext(Context context)
	{
		this.context = context;
	}

	public void setPanel(Panel pv)
	{
		this.pv = pv;
	}

	public void levelComplete()
	{
		if (mode != PLAY)
			return;

		nextLevel();
		resetTimer();
		
		pv.invalidate();

	}
	
	private String getLevelMsg()
	{
		String msg;
		switch(level)
		{
		  case 0:
		  msg = "Drag the reflectors to guide the laser \nthrough the targets."; 
		  break;
		  
		  case 1:
			  
			  msg = "Green reflectors rotate to point to your finger.\n"+
			  "White reflectors do not move\n."+"\n"+""+"\n"+"\n"+""+"\n" +                             
			  "The yellow target is only set by the yellow beam"; 
			  break;
		  
		  default:
			  msg = "";
			  break;
		}
		
		return msg;
		
	}

	public void resetGame()
	{
		// AG improve this, we dont need to save eerything just gamed data
		gameHighestLevel = 0;
		level = 0;
		//save();
		loadLevel(false);
	}

	private LightGame()
	{
		// this.gui = gui;
		light = LightModel.getInstance();
		
		resetTimer();
		// setGameMode(mode);
	}
	
	public void loadGameData()
	{
		FileInputStream fis;
		
		try
		{
			fis = new FileInputStream(context.getFilesDir().getPath().toString() +"/"+ "game.txt");
			DataInputStream in = new DataInputStream(fis);

			gameHighestLevel =  in.readByte();
			in.close();
		} catch (IOException ex)
		{
			gameHighestLevel = 0;

			//setGameMode(PLAY);
			setGameMode(DEFAULT_MODE);

		}
	}
	
	public int getHighestLevel()
	{
		return gameHighestLevel;
	}
	
	public boolean isLevelEnabled(int lev)
	{
		if ((gameHighestLevel >= lev) ||( mode != PLAY))
		return true;
		else
			return false;
	}

	public void setActivity(Activity act)
	{
		activity = act;
	}

	public static LightGame getInstance()
	{
		return instance;
	}

	public long getElapsedTime()
	{
		return (Calendar.getInstance().getTimeInMillis() - currentTime.getTimeInMillis()) / 1000;
	}

	private void resetTimer()
	{
		currentTime = Calendar.getInstance();
	}

	public int getCurrentLevel()
	{
		
			return level;
	}
	
	
	public void saveLevel(boolean solution)
	{
		FileOutputStream fos = null;
		//ObjectOutputStream out = null;

		
		if (mode != PLAY)
		{

			ArrayList<RayManager.SourceRay> raySourceList = light.getSourceRayList();
			ArrayList<ReflectorManager.Reflector> reflectorList = light
					.getReflectorList();
			ArrayList<TargetManager.Target> targetList = TargetManager.getTargetList();

			for (RayManager.SourceRay sr : raySourceList)
			{
				sr.deleteReflections();
			}

			try
			{
				fos = new FileOutputStream(context.getFilesDir().getPath().toString() +"/"+ getFileName(solution));
				DataOutputStream dout = new DataOutputStream(fos);

				dout.writeByte(raySourceList.size());
				for (RayManager.SourceRay sr : raySourceList)
				{
					dout.writeByte(sr.source.x/(light.AXIS_LENGTH/2));
					dout.writeByte(sr.source.y/(light.AXIS_LENGTH/2));
					dout.writeDouble(sr.angle);
					
					/* spare */
					dout.writeByte(0xFF);
					dout.writeByte(0xFF);
					dout.writeByte(0xFF);
					dout.writeByte(0xFF);
					dout.writeByte(0xFF);
					dout.writeByte(0xFF);
					dout.writeByte(0xFF);
					dout.writeByte(0xFF);
				}
				
				dout.writeByte(reflectorList.size());
				for (ReflectorManager.Reflector ref : reflectorList)
				{
					dout.writeByte(ref.getMidPoint().x/(light.AXIS_LENGTH/2));
					dout.writeByte(ref.getMidPoint().y/(light.AXIS_LENGTH/2));
					//dout.writeInt(ref.getLength());
					dout.writeDouble(ref.getAngle());
					
					/* spare */
					dout.writeByte(ref.getType());
					dout.writeByte(0xFF);
					dout.writeByte(0xFF);
					dout.writeByte(0xFF);
					dout.writeByte(0xFF);
					dout.writeByte(0xFF);
					dout.writeByte(0xFF);
					dout.writeByte(0xFF);
				}
				
				dout.writeByte(targetList.size());
				for (TargetManager.Target t : targetList)
				{
					dout.writeByte(t.x/(light.AXIS_LENGTH/2));
					dout.writeByte(t.y/(light.AXIS_LENGTH/2));
					dout.writeByte(t.getId());
					
					/* spare */
					dout.writeByte(0xFF);
					dout.writeByte(0xFF);
					dout.writeByte(0xFF);
					dout.writeByte(0xFF);
					dout.writeByte(0xFF);
					dout.writeByte(0xFF);
					dout.writeByte(0xFF);
					dout.writeByte(0xFF);

				}
				
				dout.close();
				// out.writeObject(raySourceList);
				// out.close();
				//System.out.println("level  saved");
			} catch (IOException ex)
			{
				ex.printStackTrace();
				//System.out.println("level not saved");
			}

			

			
		} 

	}
	
	public void saveGameData()
	{
		if (mode == PLAY)
		{
			FileOutputStream fos;
			try
			{
				fos = new FileOutputStream(context.getFilesDir().getPath().toString() +"/"+ "game.txt");
				DataOutputStream dout = new DataOutputStream(fos);
				 dout.writeByte(gameHighestLevel);
				//out.writeObject(gameData);
				dout.close();

				//System.out.println("game saved");
			} catch (IOException ex)
			{
				ex.printStackTrace();
				//System.out.println("game NOT saved");
			}
		}

	
	}
	
	public void loadLevel(int level)
	{
		this.level = level;
		loadLevel(false);
	}
	
	public void resetLevel()
	{
		loadLevel(false);
	}
	
	public void loadLevelSolution()
	{
		loadLevel(true);
		
	}
	
	public void checkLevelComplete()
	{
		
	 levelComplete = (light.checkAllTargetsHit() && (mode == LightGame.PLAY));
	 
		
	}
	
	public boolean isLevelComplete()
	{
	 return levelComplete;
	 
		
	}

	private final void loadLevel(boolean solution)
	{
		light.deleteAll();
		light.setEdited(false);
		levelComplete = false;
	

		
	

		

		
		try
		{
			DataInputStream dis;
		
			
			if (LevelActivity.DEV)
			{
		
			dis = new DataInputStream(new FileInputStream(context.getFilesDir().getPath().toString() +"/"+ getFileName(solution)));
			}
			else
			{
				dis = new DataInputStream(context.getAssets().open(getFileName(solution)));
			}
	
			
			
			int numOfRays = dis.readByte();	

			for (int x = 0; x < numOfRays; x++)
			{
				int sourceX = dis.readByte()*(light.AXIS_LENGTH/2);
				int sourceY = dis.readByte()*(light.AXIS_LENGTH/2);
				double angle = dis.readDouble();
				
				/* spare */
				 dis.readByte();
				 dis.readByte();
				 dis.readByte();
				 dis.readByte();
				 dis.readByte();
				 dis.readByte();
				 dis.readByte();
				 dis.readByte();
				
				

				light.loadSourceRay(new Point(sourceX, sourceY), angle, true);
			}

			int numOfRefs = dis.readByte();

			for (int x = 0; x < numOfRefs; x++)
			{
				int midX = dis.readByte()*(light.AXIS_LENGTH/2);
				int midY = dis.readByte()*(light.AXIS_LENGTH/2);
				//int length = 100;// dis.readInt();
				double angle = dis.readDouble();
				
				/* spare */
				int type = dis.readByte();
				 dis.readByte();
				 dis.readByte();
				 dis.readByte();
				 dis.readByte();
				 dis.readByte();
				 dis.readByte();
				 dis.readByte();
				
	

				LightModel.loadReflector(new Point(midX, midY), angle, light.AXIS_LENGTH,type);
			}
			
			
			int numOfTargets = dis.readByte();

			for (int x = 0; x < numOfTargets; x++)
			{
				int tX = dis.readByte()*(light.AXIS_LENGTH/2);
				int tY = dis.readByte()*(light.AXIS_LENGTH/2);
				int id = dis.readByte();
				
				/* spare */
				 dis.readByte();
				 dis.readByte();
				 dis.readByte();
				 dis.readByte();
				 dis.readByte();
				 dis.readByte();
				 dis.readByte();
				 dis.readByte();

				light.createTarget(tX, tY, id);
				
			}
			
			
	    	light.setMessage(getLevelMsg());
				

			dis.close();
		}

		catch (EOFException ex)
		{
			ex.printStackTrace();
		} catch (IOException ex)
		{
			ex.printStackTrace();
		}
		

	}

	public int getMode()
	{
		return mode;
	}

	/*public void prevLevel()
	{
		// save();
		if (mode == PLAY)
		{
			if (level > 0)
				level--;
		} else
		{
			if (setLevel > 0)
				setLevel--;
		}

		resetTimer();

		load();

	}*/

	public void nextLevel()
	{

		if (mode == PLAY)
		{
			// if (level < LAST_LEVEL)
			resetTimer();
			

					level++;
					loadLevel(false);

					if (level > gameHighestLevel)
					{
						gameHighestLevel = level;
					}

					if (!light.lightScenarioSet())
					{
						AlertDialog.Builder builder = new AlertDialog.Builder(activity);
						builder.setMessage("Game Completed").setCancelable(true)
								.setPositiveButton("Ok", null);
						AlertDialog alert = builder.create();
						alert.show();

					}
					//save();

				
			
		}
		
	}

	
	public void setGameMode(int mode)
	{
		this.mode = mode;

		switch (mode)
		{
		case FREE:
			light.raysSelectable = true;
			light.rotationOverride = true;
			light.targetSelectable = true;
			light.staticRefsSelectable = true;
			light.gridOn = false;
			light.deleteEnabled = true;
			light.showDebugText = false;
			fileName = SIM_FILE_NAME;
			break;

		case SET_LEVEL:
			light.raysSelectable = true;
			light.rotationOverride = true;
			light.targetSelectable = true;
			light.staticRefsSelectable = true;
			light.gridOn = true;
			light.deleteEnabled = true;
			light.showDebugText = true;
			fileName = FILE_NAME;
			break;

		case PLAY:

			light.raysSelectable = false;
			light.rotationOverride = false;
			light.targetSelectable = false;
			light.staticRefsSelectable = false;
			light.gridOn = true;
			light.showDebugText = false;
			fileName = FILE_NAME;

			break;

		}
	
	}

	public void copyAssets()
	{
		AssetManager assetManager = context.getAssets();
		String[] files = null;
		try
		{
			files = assetManager.list("");

			
		} catch (IOException e)
		{
			//System.out.println("Failed to get asset file list.");
		}
		for (String filename : files)
		{
			InputStream in = null;
			OutputStream out = null;
			try
			{
				File file = new File(context.getFilesDir().getPath().toString() +"/"+ filename);
				if (file.exists())
					continue;

				in = assetManager.open(filename);
				out = new FileOutputStream(file);
				copyFile(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;

			} catch (IOException e)
			{
				//System.out.println("Failed to get asset file list" + filename);
			}

		}
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException
	{
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1)
		{
			out.write(buffer, 0, read);
		}
	}

	public void copyfilesToSdCard()
	{
		copyfileToSdCard(getFileName(false));
		copyfileToSdCard(getFileName(true));
		//copyfileToSdCard(getTargetFileName());
	}

	private void copyfileToSdCard(String fileName)
	{

		try
		{
			InputStream in = new FileInputStream(context.getFilesDir().getPath().toString()+"/"
					+ fileName);

			OutputStream out = new FileOutputStream("sdcard/" + fileName);

			copyFile(in, out);
			in.close();
			out.close();
			//System.out.println("File copied.");
		} catch (FileNotFoundException ex)
		{
			System.out.println(ex.getMessage() + " in the specified directory.");

		} catch (IOException e)
		{
			System.out.println(e.getMessage());
		}

	}

	
	
	private String getFileName(boolean solution)
	{
		String str;
		
		if(solution == true)
		{
			str =fileName + level + "_sol.txt";
		}
		else
		{
			str =fileName + level + ".txt";
		}
		
		return str;
		
	}

}

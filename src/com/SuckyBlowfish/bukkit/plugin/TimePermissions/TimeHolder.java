package com.SuckyBlowfish.bukkit.plugin.TimePermissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TimeHolder {
	private final JavaPlugin plugin;
	private ArrayList<String> mPlayerList = new ArrayList<String>();
	private ArrayList<Integer> mTimeList = new ArrayList<Integer>();
	private Map<String,Map<String,Integer>> TimeWorlds;
	//TimeWorlds
	//	|"world"
	//	|  |"SuckyBlowfish":13124
	//	|  |"bandless55":22313
	//	|"world_nether"
	//	|  |"SuckyBlowfish": 223
	//	|  |"bandless55": 5532
	private Timer updateTimer;
	private Timer saveTimer;
	private File dataSaveFile = null;
	
	public TimeHolder(JavaPlugin plugin){
		this.plugin = plugin;
		TimeWorlds = new HashMap<String,Map<String,Integer>>();
	}
	
	public void start(){
		updateTimer=new Timer("UpdateTimer");
		updateTimer.scheduleAtFixedRate(new updateTask(),1000,1000);
		saveTimer=new Timer("UpdateTimer");
		saveTimer.scheduleAtFixedRate(new saveTask(),600000,60000);
	}
	
	public void stop(){
		updateTimer.cancel();
		saveTimer.cancel();
	}
	
	public void load(File data){
		dataSaveFile = data;
		Scanner dataScanner = null;
		try {
			FileReader dataStream = new FileReader(dataSaveFile);
			dataScanner = new Scanner(dataStream);
	        while (dataScanner.hasNextLine()){
	            String[] split = dataScanner.nextLine().split(":");
	            addPlayer(split[0], Integer.parseInt(split[1]));
	        }
		} catch (FileNotFoundException e) {
			System.out.print("[TimePermissions] Data file not found, did *you* fuck this up?");
			e.printStackTrace();
		} finally{
			dataScanner.close();
		}
	}	
	
	public void save(){
		if (dataSaveFile!=null){
			String dataString = "";
			dataString+="@Version:v0.01";
			for (int i=0;i<mPlayerList.size();i++){
				dataString+="\n"+mPlayerList.get(i)+":"+mTimeList.get(i);
			}
			Writer out = null;
			try{
				out = new OutputStreamWriter(new FileOutputStream(dataSaveFile));
				out.write(dataString);
				out.close();
				System.out.print("[TimePermissions] All player times saved to disk!");
		    } catch (IOException e) {
				e.printStackTrace();
				System.out.print("[TimePermissions] Error with saving :@");
			} finally {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else{
			System.out.print("[TimePermissions] Tried to save before data was loaded!");
		}
	}
	
	private class updateTask extends TimerTask{
		public void run(){
			increaseTimes(plugin.getServer().getOnlinePlayers());
		}
	}
	
	private class saveTask extends TimerTask{
		public void run(){
			save();
		}
	}
	
	private void addPlayer(Player player){
		String name = player.getName();
		addPlayer(name);
	}
	
	private void addPlayer(String name){
		if (!mPlayerList.contains(name)){
			addPlayer(name,0);
		}
	}
	
	public void addPlayer(Player player, int time){
		String name = player.getName();
		addPlayer(name,time);
	}
	
	public void addPlayer(String name, int time){
		mPlayerList.add(name);
		mTimeList.add(time);
	}
	
	private void increaseTimes(Player[] players){
		for(Player player: players){
			String name = player.getName();
			int i = mPlayerList.indexOf(name);
			if (i==-1){
				addPlayer(name);
				i=mPlayerList.indexOf(name);
			}
			mTimeList.add(i,mTimeList.get(i)+1);
		}
	}
	
	public int getTime(Player player){
		String name = player.getName();
		return getTime(name);
	}
	
	public Integer getTime(String name){
		if (mPlayerList.contains(name)){
			return mTimeList.get(mPlayerList.indexOf(name));
		}
		else{
			return null;
		}
	}
	
	public String[] topTime(Integer top){
		if (top==null)top=5;
		ArrayList<Integer> rTimeList = mTimeList;
		Collections.sort(rTimeList);
		top=Math.min(top, rTimeList.size());
		String[] topPlayerArray = new String[top];
		for (int i=0;i<5;i++){
			topPlayerArray[i]=mPlayerList.get(mTimeList.indexOf(rTimeList.get(i)));
		}
		return topPlayerArray;
	}
}
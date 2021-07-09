package fw;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import customgo.listener.ReportListener;
import fw.group.joinprice.tili.User;
import fw.group.task.ValueData;
import net.milkbowl.vault.economy.Economy;


public class Data {
	public static boolean onDisable = false;
	public static boolean HighPlaceHolderAPIVersion = true;
	public static boolean HighMCVersion = true;
	
	public static String Version = "V8.6.6";
	
	public static boolean debug = false;
	
	public static Fwmain fmain = null;

	public static File lobbyDir;

	public static File itemDir;
	public static File functionDir;
	public static File optionFile;
	
	public static FileConfiguration optionFileConf;
	public static FileConfiguration LanguageFileConf;

	public static boolean LoadWhenJoin = false;
	
	public static ValueData data;
	
	public static boolean isEco = false;
	public static boolean isPoints = false;
	public static boolean isTili = false;
	
	public static Economy economy;
	public static PlayerPoints playerPoints;
	public static User tili;
	
	/**
	 * 向后台发送一个信息。
	 * @param info 需要发送的信息
	 */
	public static void ConsoleInfo(String info) {
		fmain.getLogger().info(info);
		for(ReportListener r : rep){
			r.ReportListen(info);
		}
	}

	
	public static void LoadOption() {
		try {
			debug = optionFileConf.getBoolean("Debug");
			
			if(optionFileConf.contains("HighVersionPlaceHolderApi")){
     	    	Data.HighPlaceHolderAPIVersion = optionFileConf.getBoolean("HighVersionPlaceHolderApi");
     	    }
			if(optionFileConf.contains("HighMCVersion")){
     	    	Data.HighMCVersion = optionFileConf.getBoolean("HighMCVersion");
     	    }
			LoadWhenJoin = false;
			//LoadWhenJoin = optionFileConf.getBoolean("LoadWhenJoin");
		} catch (NullPointerException x) {
			return;
		}
	}

	public static int Random(int a, int b) {
		int s;
		Random random = new Random();
		int length;
		if (a > b) {
			length = a - b;
			s = random.nextInt(length) + b;
		} else if (a < b) {
			length = b - a;
			s = random.nextInt(length) + a;
		} else {
			return a;
		}
		return s;
	}

	public static String ColorChange(String str) {
		return str.replace("&", "§");
	}

	public static List<String> ColorChange(List<String> str) {
		for (int a = 0; a < str.size(); a++) {
			str.set(a, ColorChange(str.get(a)));
		}
		return str;
	}

	public static void save() {
		try {
			optionFileConf.save(optionFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
/**
 * 让控制台执行一个指令(不带"/")。
 * @param command 需要执行的指令
 */
	public static void ConsoleCommand(String command) {
		Bukkit.dispatchCommand(Data.fmain.getServer().getConsoleSender(), command);
	}
/**
 * 向后台发送一个Debug信息。
 * 该信息会自动带有前缀，并且只在Debug开启时有效。
 * @param str 需要发送的信息
 */
	public static void Debug(String str){
		if(debug){
			ConsoleInfo("[Debug] "+str);
		}
		return;
	}
	private static Set<ReportListener> rep = new HashSet<>();
	public static void RegisterReport(ReportListener report){
		rep.add(report);
	}
}

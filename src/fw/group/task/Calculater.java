package fw.group.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import customgo.task.Value;
import fw.Data;
import fw.group.Group;
import fw.timer.CustomTimer;
import me.clip.placeholderapi.PlaceholderAPI;

public class Calculater {
	Group group;
	Player player;
	Player striker;
	Calculater(Group group, Player player, Player striker){
		this.group = group;
		this.player = player;
		this.striker = striker;
	}
	String ValueChange(String str) {
		if(str==null){
			return null;
		}
		str = str.replace("&", "§");
		str = valueExecuter(str);
		str = CalChange(str);
		str = PlaceholderAPI.setPlaceholders(player, str);
		str = str.replaceAll("~\\[", "<");
		str = str.replaceAll("\\]~", ">");
		str = str.replaceAll("\\·\\[", "{");
		str = str.replaceAll("\\]\\·", "}");
		return str;
	}
	
	private String BaseChange(String str){
		
		if(str.equals("<player>")){
			if(player != null){
				return player.getName();
			}else{
				return "控制台";
			}
		}
		
		if(str.equals("<striker>")){
			if(striker != null){
				return striker.getName();
			}else{
				Data.ConsoleInfo("注意：您在运行无触发者任务时使用了<striker>变量！");
				return "未知触发者";
			}
		}
		
		if(str.equals("<player.level>")){
			if(player != null){
				return player.getLevel()+"";
			}else{
				return "0";
			}
		}

		if(str.equals("<striker.level>")){
			if(striker != null){
				return striker.getLevel()+"";
			}else{
				Data.ConsoleInfo("注意：您在运行无触发者任务时使用了<striker.level>变量！");
				return "0";
			}
		}
		
		if(str.equals("<player.health>")){
			if(player != null){
				return player.getHealth()+"";
			}else{
				return "0";
			}
		}

		if(str.equals("<striker.health>")){
			if(striker != null){
				return striker.getHealth()+"";
			}else{
				Data.ConsoleInfo("注意：您在运行无触发者任务时使用了<striker.health>变量！");
				return "0";
			}
		}
		
		if(str.equals("<player.maxhealth>")){
			if(player != null){
				return player.getMaxHealth()+"";
			}else{
				return "0";
			}
		}

		if(str.equals("<striker.maxhealth>")){
			if(striker != null){
				return striker.getMaxHealth()+"";
			}else{
				Data.ConsoleInfo("注意：您在运行无触发者任务时使用了<striker.maxhealth>变量！");
				return "0";
			}
		}
		
		if(str.equals("<name>")){
			return group.GetName();
		}
		if(str.equals("<display>")){
			return group.GetDisplay();
		}
		if(str.equals("<playeramount>")){
			return group.GetPlayerAmount()+"";
		}
		
		if(str.equals("<globalplayeramount>")){
			return group.byLobby().getPlayerAmount()+"";
		}
		if(str.equals("<maxplayer>")){
			return group.getMaxPlayer()+"";
		}
		if(str.equals("<minplayer>")){
			return group.getMinPlayer()+"";
		}
		
		for(Group a : group.byLobby().getGroupList()){
			if(str.equals("<"+a.GetName()+".playeramount>")){
				return a.GetPlayerAmount()+"";
			}
		}
		
		for(String a : group.ValueBoard().ValueList()){
			if(str.equals("<value."+a+">")){
				return autoLeaveInteger(group.ValueBoard().getValue(a));
			}
		}
		
		for(String a : group.byLobby().ValueBoard().ValueList()){
			if(str.equals("<globalvalue."+a+">")){
				return autoLeaveInteger(group.byLobby().ValueBoard().getValue(a));
			}
		}
		for(CustomTimer t : group.getTimer().getCustomTimerList()){
			if(str.equals("<timer."+t.getName()+">")){
				return t.getTime()+"";
			}
		}
		if(str.equals("<timer.lobbytime>")){
			return group.getTimer().LobbyTimer().getTime()+"";
		}
		if(str.equals("<timer.gametime>")){
			return group.getTimer().ArenaTimer().getTime()+"";
		}
		
		Date d = new Date();
		
		if(str.equals("<systemtime>")){
			long p = (d.getTime() /1000);
			int x =  (int) ((p-57601)%86400);
			return x+"";
		}
		
		if(str.equals("<systemtime.date>")){
			return d.getDate()+"";
		}
		if(str.equals("<systemtime.month>")){
			return d.getMonth()+"";
		}
		if(str.equals("<systemtime.hour>")){
			return d.getHours()+"";
		}
		if(str.equals("<systemtime.minute>")){
			return d.getMinutes()+"";
		}
		if(str.equals("<systemtime.second>")){
			return d.getSeconds()+"";
		}
		if(str.equals("<systemtime.week>")){
			return d.getDay()+"";
		}
		
		if(player!=null){
			if(group.PlayerValueBoard().ValueList(player)!=null){
				for(String a : group.PlayerValueBoard().ValueList(player)){
					if(str.equals("<playervalue."+a+">")){
						return autoLeaveInteger(group.PlayerValueBoard().getValue(a,player));
					}
				}
			}
			if(group.byLobby().PlayerValueBoard().ValueList(player)!=null){
				for(String a : group.byLobby().PlayerValueBoard().ValueList(player)){
					if(str.equals("<globalplayervalue."+a+">")){
						return autoLeaveInteger(group.byLobby().PlayerValueBoard().getValue(a,player));
					}
				}
			}
		}
		if(striker!=null){
			if(group.PlayerValueBoard().ValueList(striker)!=null){
				for(String a : group.PlayerValueBoard().ValueList(striker)){
					if(str.equals("<strikervalue."+a+">")){
						return autoLeaveInteger(group.PlayerValueBoard().getValue(a,striker));
					}
				}
			}
			if(group.byLobby().PlayerValueBoard().ValueList(striker)!=null){
				for(String a : group.byLobby().PlayerValueBoard().ValueList(striker)){
					if(str.equals("<globalstrikervalue."+a+">")){
						return autoLeaveInteger(group.byLobby().PlayerValueBoard().getValue(a,striker));
					}
				}
			}
		}
		
		if(group.Tags().TagList()!=null){
			for(String a : group.Tags().TagList()){
				if(str.equals("<tag."+a+">")){
					return group.Tags().getTag(a);
				}
			}
		}
		
		
		for(Value v : vlist){
			if(str.startsWith("<"+v.setId())){
				str = str.substring(1, str.length()-1);
				return v.ValueFeedBack(str.split("\\."));
			}
		}
		
		
		if(str.startsWith("<data.")){
			return Data.data.getValue(str.substring(6,str.length()-1));
		}
		
		return "null";
	}
	
	String valueExecuter(String p){
		char[] cl = p.toCharArray();
		List<Integer> loc = new ArrayList<>();
		for(int a = 0 ; a < cl.length ; a++){
				if(cl[a]=='<'){
					loc.add(a);
				}
				if(cl[a]=='>' ){
					if(loc.size()>0){
						String s = p.substring(loc.get(loc.size()-1),a+1);
						if(s.equals(BaseChange(s))){
							p = p.replace(s, BaseChange(s));
						}else{
							p = p.replace(s, BaseChange(s));
						}
						
						return valueExecuter(p);
					}else{
						System.out.println("参数"+p+"出现语法结构错误：尖括号不匹配！");
					}
					
				}
			
		}
		return p;
	}
	
	static Set<Value> vlist = new HashSet<>();
	
	public static void RegValue(Value v){
		if(v.setId()!=null){
			for(Value value : vlist){
				if(v.setId().equals(value.setId())){
					Data.Debug("一项value占位符ID "+value.setId()+" 被注册了两次！第二次注册自动取消！");
					return;
				}
			}
		}else{
			Data.Debug("一项value企图注册空占位符！注册自动取消！");
			return;
		}
		vlist.add(v);
	}
	
	private String CalChange(String str){

		while(str.lastIndexOf("|[dcal=")!=-1 && str.indexOf("]|")!=-1){
			String replace = str.substring(str.indexOf("|[dcal="),str.indexOf("]|")+2);
			String cal = str.substring(str.indexOf("|[dcal=")+7,str.indexOf("]|"));
			str = str.replace(replace, ""+(int)calculate(cal));
		}

		while(str.lastIndexOf("|[cal=")!=-1 && str.indexOf("]|")!=-1){
			String replace = str.substring(str.indexOf("|[cal="),str.indexOf("]|")+2);
			String cal = str.substring(str.indexOf("|[cal=")+6,str.indexOf("]|"));
			str = str.replace(replace, ""+autoLeaveInteger(calculate(cal)));
			str = str.replace(replace, "-1");
		}
		
		
		return str;
	}

	/**
	 * 搜索一个字符串中是否包含指定字符串(支持正则表达式)，将所有指定字符串剪下来并整合为数组返回。
	 * @param 原字符串
	 * @param 指定字符串[支持正则表达式]
	 * @return 剪完的所有字符串组成的数组。
	 */
	private List<String> Cut(String str, String regx) { 
	    //1.将正在表达式封装成对象Patten 类来实现
		List<String> s = new ArrayList<>();
	    Pattern pattern = Pattern.compile(regx); 
	    //2.将字符串和正则表达式相关联 
	    Matcher matcher = pattern.matcher(str); 
	    //3.String 对象中的matches 方法就是通过这个Matcher和pattern来实现的。 
	    //查找符合规则的子串 
	    while(matcher.find()){ 
	      //获取 字符串 
	      s.add(matcher.group());
	    }
	    return s;
	  }

	/**
	 * 对一个小数四舍五入。
	 * @param num 将要四舍五入的小数。
	 * @param length 保留的小数位数。
	 * @return
	 */
	private double roundOff(double num,int length){
		double r = ( (double)((long)(num*1000000000)) / 1000000000 );
		if( num - r > 0.0000000005){
			r = r+0.000000001;
		}
		return r;
	}
	
	
	public double calculate(String C){
		try{
			String com = "";
			while(true){
				com = C;
				List<String> c;
				C = Trim(C);
				c=Cut(C,"[.0-9]+(\\-)[.0-9]+");
				if(c.size()>0){
					for(String v : c){
						C = C.replace(v, JJ(v));
					}
					C = Trim(C);
				}
				//减法转换
				c=Cut(C,"(ran)\\[(\\-|)[.0-9]+,(\\-|)[.0-9]+\\]");
				if(c.size()>0){
					for(String v : c){
						C = C.replace(v, random(v));
					}
					C = Trim(C);
				}
				//随机数计算
				c=Cut(C,"(a|)(tan|sin|cos|cot)\\[(\\-|)[.0-9]+\\]");
				if(c.size()>0){
					for(String v : c){
						C = C.replace(v, Tr(v));
					}
					C = Trim(C);
				}
				//三角函数计算
				c=Cut(C,"(ln)\\[(\\-|)[.0-9]+\\]");
				if(c.size()>0){
					for(String v : c){
						C = C.replace(v, Log(v));
					}
					C = Trim(C);
				}
				//对数运算
				c=Cut(C,"[.\\-0-9]+\\^(\\-|)[.0-9]+");
				if(c.size()>0){
					for(String v : c){
						C = C.replace(v, Pow(v));
					}
					C = Trim(C);
				}
				//次方计算
				c=Cut(C,"(\\-|)[.0-9]+%(\\-|)[.0-9]+");
				if(c.size()>0){
					for(String v : c){
						C = C.replace(v, QY(v));
					}
				}
				//求余数计算
				c=Cut(C,"(\\-|)[.0-9]+(/|\\*)(\\-|)[.0-9]+");
				if(c.size()>0){
					for(String v : c){
						C = C.replace(v, Mul(v));
					}
					C = Trim(C);
				}
				//乘除计算
				
				c=Cut(C,"(\\-|)[.0-9]+\\+(\\-|)[.0-9]+");
				if(c.size()>0){
					for(String v : c){
						C = C.replace(v, Add(v));
					}
					C = Trim(C);
				}
				//加法计算
				
				c=Cut(C,"\\|(\\-|)[.0-9]+\\|");
				if(c.size()>0){
					for(String v : c){
						C = C.replace(v, Abs(v));
					}
					C = Trim(C);
				}
				//绝对值计算

				c=Cut(C,"\\((\\-|)[.0-9]+\\)");
				if(c.size()>0){
					for(String v : c){
						C = C.replace(v, delK(v));
					}
				}
				//去除括号计算
				
				if(com.equals(C)){
					break;
				}

			}
			return roundOff(Double.parseDouble(C),5);
		}catch(NumberFormatException e){
			return -1;
		}
	}

	
	private String random(String v) {
		String n = v.split("\\[")[1].replace("]", "");
		double a = Double.parseDouble(n.split(",")[0]);
		double b = Double.parseDouble(n.split(",")[1]);
		if(b>a){
			return String.valueOf((int)(a+Math.random()*(b-a+1)));
		}else{
			return String.valueOf((int)(b+Math.random()*(a-b+1)));
		}
	}
	
	private String Log(String v) {
		double a = Double.parseDouble(v.split("\\[")[1].replace("]", ""));
		return String.valueOf(Math.log(a));
	}
	
	private String QY(String v) {
		double a = Double.parseDouble(v.split("%")[0]);
		double b = Double.parseDouble(v.split("%")[1]);
		return String.valueOf((a%b));
	}
	
	private CharSequence JJ(String v) {
		v = v.replace("-", "+-");
		return v;
	}
	
	private String delK(String v) {
		v = v.replace("(", "");
		v = v.replace(")", "");
		return v;
	}
	
	private String Tr(String s){
		double x;
		if(s.split("\\[").length==1){
			return "";
		}
		String f = s.split("\\[")[0];
		String num = s.split("\\[")[1].replace("]", "");
		switch(f){
		case "atan":
			x = Math.atan(Double.parseDouble(num))*(180/Math.PI);
			break;
		case "asin":
			x = Math.asin(Double.parseDouble(num))*(180/Math.PI);
			break;
		case "acos":
			x = Math.acos(Double.parseDouble(num))*(180/Math.PI);
			break;
		case "arccot":
			x = 90-Math.atan(Double.parseDouble(num))*(180/Math.PI);
			break;
		case "tan":
			x = Math.tan(Double.parseDouble(num)*Math.PI/180);
			break;
		case "sin":
			x = Math.sin(Double.parseDouble(num)*Math.PI/180);
			break;
		case "cos":
			x = Math.cos(Double.parseDouble(num)*Math.PI/180);
			break;
		case "cot":
			x = 1/Math.tan(Double.parseDouble(num)*Math.PI/180);
			break;
		default:
			return "";
		}
		return String.valueOf(roundOff(x,10));
	}
	
	private String Pow(String s){
		if(s.split("\\^").length==1){
			return "";
		}
		String[] l = s.split("\\^");
		return String.valueOf(Math.pow(Double.parseDouble(l[0]), Double.parseDouble(l[1])));
	}
	
	private String Mul(String s){
		if(s.indexOf("*")!=-1){
			return String.valueOf(Double.parseDouble(s.split("\\*")[0])*Double.parseDouble(s.split("\\*")[1]));
		}else if(s.indexOf("/")!=-1){
			return String.valueOf(roundOff( Double.parseDouble(s.split("/")[0]) / Double.parseDouble(s.split("/")[1]) , 10));
		}else{
			return "";
		}
	}
	private String Add(String s){
		if(s.split("\\+").length==1){
			return "";
		}
		return String.valueOf(Double.parseDouble(s.split("\\+")[0])+Double.parseDouble(s.split("\\+")[1]));
	}
	private String Abs(String s){
		s = s.replace("|", "");
		s = s.replace("-", "");
		return s;
	}
	private String Trim(String s){
		s=s.replace("()", "");
		s=s.replace("--", "+");
		return s;
	}
	
	String math = "+-*/-.1234567890";
	public boolean Check(String str){
		for(int a = 0;a<str.length()-1;a++){
			if(math.indexOf(str.substring(a,a+1))==-1){
				return false;
			}
		}
		return true;
	}
	
	public String autoLeaveInteger(double d){
		if(d%1==0){
			return (int)d+"";
		}else{
			return d+"";
		}
	}
	
}

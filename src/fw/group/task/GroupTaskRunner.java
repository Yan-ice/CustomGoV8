package fw.group.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import customgo.task.Task;
import fw.Data;
import fw.group.Group;
import fw.group.Lobby;
import fw.group.joincheck.JoinPrice;
import fw.group.trigger.TriggerBase;
import fw.location.FArena;
import fw.location.FLocation;
import fw.location.Teleporter;

public class GroupTaskRunner extends BukkitRunnable {
	
	List<String> tasklist = new ArrayList<>();
	List<Player> playerlist = new ArrayList<>();
	
	Map<String,Integer> marklist = new HashMap<>();
	
	Group group;
	Player striker;
	int RunningPoint = 0;
    boolean EndWhenClear = false;
	
    String taskName = "";
    
	public GroupTaskRunner(List<String> TaskList, Group byGroup, Player striker ,List<Player> playerlist) {
		if(Data.onDisable) {
			return;
		}
		this.group = byGroup;
		this.striker = striker;
		boolean functionchecked = false;
		do{
			if(functionchecked){
				TaskList.clear();
				TaskList.addAll(tasklist);
				tasklist.clear();
				functionchecked = false;
			}
			for (int a = 0;a<TaskList.size();a++) {
				String ts = TaskList.get(a);

				if(ts.startsWith("function")){
					if (ts.indexOf("{") != -1) {
						String value = ts.split("\\{")[1].split("\\}")[0];
						
						String[] args = new String[1];
						if(value.contains(",")){
							args = value.split(",");
						}else{
							args[0] = value;
						}
						TaskFunction func = TaskFunction.getFunction(args[0]);
						if(func!=null){
							functionchecked = true;
							tasklist.add("funcmark{"+value+"}");
							tasklist.addAll(func.getTask());
						}else{
							Data.ConsoleInfo("====[一项任务因出错被取消！]====");
							Data.ConsoleInfo("错误类型：函数不存在");
							Data.ConsoleInfo("内容: " + ts);
							Data.ConsoleInfo("位于: 游戏文件夹 【"+ group.byLobby().getName() +"】 的队列文件 【" + group.GetName() +"】");
						}
						
					}
				}else{
					tasklist.add(ts);
				}
			}
		}while(functionchecked);
		
		
		this.playerlist = playerlist;
		
		for (int a = tasklist.size()-1;a>-1;a--) {
			String ts = tasklist.get(a);
			if(ts.startsWith("mark")){
				if (ts.indexOf("{") != -1) {
					String value = ts.split("\\{")[1].split("\\}")[0];
					marklist.put(value, a);
				}
			}
		}
		
		this.runTaskTimer(Data.fmain, 1,1);
		H.add(this);
		
	}

	
	int BreakTime = 0;
	@Override
	public void run() {
		if(RunningPoint>=tasklist.size()){
			ShutDown();
			return;
		}
		if(EndWhenClear && group.GetPlayerAmount()==0){
			ShutDown();
			return;
		}
		if(BreakTime>0){
			BreakTime--;
			return;
		}
		for (;RunningPoint<tasklist.size();RunningPoint++) {
			if(BreakTime>0){
				return;
			}
			try{
				if (!Execute(tasklist.get(RunningPoint))) {
					Data.ConsoleInfo("====[一项任务因出错被取消！]==== ");
					Data.ConsoleInfo("错误类型：缺少必需数据/缺少目标");
					Data.ConsoleInfo("内容: " + tasklist.get(RunningPoint));
					Data.ConsoleInfo("位于: 游戏 【"+ group.byLobby().getName() +"】 的队列 【" + group.GetName() +"】");
				}
			}catch(NullPointerException e){
				e.printStackTrace();
				Data.ConsoleInfo("====[一项任务因出错被取消！]====");
				Data.ConsoleInfo("错误类型：缺少必需数据");
				Data.ConsoleInfo("内容: " + tasklist.get(RunningPoint));
				Data.ConsoleInfo("位于: 游戏 【"+ group.byLobby().getName() +"】 的队列 【" + group.GetName() +"】");
			
			}catch(NumberFormatException e){
				Data.ConsoleInfo("====[一项任务因出错被取消！]==== ");
				Data.ConsoleInfo("错误类型：转换数字错误(勿在需要数字处输入非数字)");
				Data.ConsoleInfo("内容: " + tasklist.get(RunningPoint));
				Data.ConsoleInfo("位于: 游戏 【"+ group.byLobby().getName() +"】 的队列 【" + group.GetName() +"】");
			
			}catch(IndexOutOfBoundsException e){
				Data.ConsoleInfo("====[一项任务因出错被取消！]==== ");
				Data.ConsoleInfo("错误类型：缺少必需数据或格式错误");
				Data.ConsoleInfo("内容: " + tasklist.get(RunningPoint));
				Data.ConsoleInfo("位于: 游戏 【"+ group.byLobby().getName() +"】 的队列 【" + group.GetName() +"】");
			
			}
			
		}
	}

	public boolean Execute(String args) throws NullPointerException,NumberFormatException,IndexOutOfBoundsException{
		
		String label = null;
		String value = null;
		String target = "";
		args = args.replaceAll("\\\\\\{", "·[");
		args = args.replaceAll("\\\\\\}", "]·");
		args = args.replaceAll("\\\\\\<", "~[");
		args = args.replaceAll("\\\\\\>", "]~");
		if (args.indexOf("{") != -1) {
			label = args.split("\\{")[0];
		} else {
			return false;
		}
		
		if (args.indexOf("}") != -1) {
			value = args.split("\\{")[1];
			String[] sp = value.split("\\}");
			if(sp.length>1){
				value = sp[sp.length-2];
				target = sp[sp.length-1];
			}else if(sp.length>0){
				value = sp[0];
			}else{
				value="";
			}
			if (value == "" || value == " ") {
				value = null;
			}
		} else {
			return false;
		}
		
		if(playerlist!=null && playerlist.size()>0){
			List<Player> list;
			if (target!=null &&target.indexOf("@") != -1) {
				PlayerChooser chooser = new PlayerChooser(striker,group,playerlist,playerlist.get(0).getWorld());
				list = chooser.Choose(target);
			}else{
				list = null;
			}

			if(list!=null){
				for (Player p : list) {
					Calculater cal = new Calculater(group,p,striker);
					String valuen = cal.ValueChange(value);
					if (!RunTask(label, valuen, p)) {
						return false;
					}
				}
			}else{
				Calculater cal = new Calculater(group,null,striker);
				value = cal.ValueChange(value);
				if (!RunTask(label, value, null)) {
					return false;
				}
			}
		}else{
			Calculater cal = new Calculater(group,null,striker);
			value = cal.ValueChange(value);
			if (!RunTask(label, value, null)) {
				return false;
			}
		}
		

		return true;
	}

	private boolean If(String If,Player pl) throws NullPointerException,NumberFormatException,IndexOutOfBoundsException{
		Calculater c = new Calculater(group,pl,striker);
		If = c.ValueChange(If);
		
		if(If.indexOf("&")!=-1){
			return (If(If.split("&")[0],pl) & If(If.split("&")[1],pl));
		}
		if(If.indexOf("|")!=-1){
			return (If(If.split("|")[0],pl) | If(If.split("|")[1],pl));
		}
		if(If.indexOf("BIG")!=-1){
			if(Double.parseDouble(If.split("BIG")[0])>Double.parseDouble(If.split("BIG")[1])){
				return true;
			}else{
				return false;
			}
		}
		if(If.indexOf("SMALL")!=-1){
			if(Double.parseDouble(If.split("SMALL")[0])<Double.parseDouble(If.split("SMALL")[1])){
				return true;
			}else{
				return false;
			}
		}
		if(If.indexOf("!=")!=-1){
			String value = If.split("!=")[1];
			switch(If.split("!=")[0]){
			case "Permission":
				if(striker.hasPermission(value)){
					return false;
				}else{
					return true;
				}
			case "InGroup":
				if(group.hasPlayer(striker)){
					return false;
				}else{
					return true;
				}
			default:
				if(If.split("!=")[0].equals(If.split("!=")[1])){
					return false;
				}else{
					return true;
				}
			}
			
		}else
		if(If.indexOf("=")!=-1){
			String value = If.split("=")[1];
			switch(If.split("=")[0]){
			case "minLevel":
				if(striker.getLevel()>=Double.parseDouble(value)){
					return true;
				}else{
					return false;
				}
			case "maxLevel":
				if(striker.getLevel()<=Double.parseDouble(value)){
					return true;
				}else{
					return false;
				}
			case "minLife":
				if(striker.getHealth()>=Double.parseDouble(value)){
					return true;
				}else{
					return false;
				}
			case "maxLife":
				if(striker.getHealth()<=Double.parseDouble(value)){
					return true;
				}else{
					return false;
				}
			case "Permission":
				if(striker.hasPermission(value)){
					return true;
				}else{
					return false;
				}
			case "InGroup":
				if(group.hasPlayer(striker)){
					return true;
				}else{
					return false;
				}
			default:
				if(!If.split("=")[0].equals(If.split("=")[1])){
					return false;
				}else{
					return true;
				}
			}
			
		}
		return true;
	}

	
	private boolean RunTask(String label, String value, Player Target)  throws NullPointerException,NumberFormatException,IndexOutOfBoundsException{

		if (label == null) {
			return false;
		}
		World autoworld = Bukkit.getWorld("world");
		if(playerlist!=null && playerlist.size()>0) {
			autoworld = playerlist.get(0).getWorld();
		}
		
		switch (label) {
		case "leave":
			if (Target == null) {
				return false;
			}
			Lobby.AutoLeave(Target,false);
			break;
		case "command":
			if (value == null) {
				return false;
			}
			value = value.replace("|[", "{");
			value = value.replace("]|", "}");
			CommandRunner(value, Target);
			break;
		case "consolecommand":
			if (value == null) {
				return false;
			}
			value = value.replace("|[", "{");
			value = value.replace("]|", "}");
			Data.ConsoleCommand(value);
			break;
		case "say":
			if (value == null || Target==null) {
				return false;
			}
			Target.chat(value);
			break;
		case "tell":
			if (value == null || Target == null) {
				return false;
			}
			Target.sendMessage(value);
			break;
		case "title":
			if (value == null || Target == null) {
				return false;
			}
			String[] p = value.split(",");
			if(p.length>1){
				Target.sendTitle(p[0],p[1]);
			}else{
				Target.sendTitle(value,"");
			}
			
			break;
		case "delay":
			if (value == null) {
				return false;
			}
			try {
				Delay(Integer.valueOf(value));
			} catch (NumberFormatException a) {
				return false;
			}
			break;
		case "nametask":
			if (value == null) {
				return false;
			}
			try {
				this.taskName = value;
			} catch (NumberFormatException a) {
				return false;
			}
			break;
		case "heal":
			if (Target == null) {
				return false;
			}
			if ((value != null) && (value.trim() != "")) {
				try {
					Heal(Target, Double.parseDouble(value));
				} catch (NumberFormatException a) {
					return false;
				}
			} else {
				Heal(Target, null);
			}
			break;
		case "maxhp":
			if (Target == null) {
				return false;
			}
			if ((value != null) && (value.trim() != "")) {
				try {
					Target.setMaxHealth(Double.parseDouble(value));
				} catch (NumberFormatException a) {
					return false;
				}
			} else {
				Target.setMaxHealth(20);
			}
			break;
		case "food":
			if (Target == null) {
				return false;
			}
			if (value != null) {
				try {
					Food(Target, Integer.valueOf(value));
				} catch (NumberFormatException a) {
					return false;
				}
			} else {
				Food(Target, null);
			}
			break;
			
		case "damage":
			try {
				Damage(Target, Double.parseDouble(value));
			} catch (NumberFormatException a) {
				return false;
			}
			break;
		case "sethp":
			Target.setHealth(Double.parseDouble(value));
			break;
		case "potion":
			Potion(Target, value);
			break;	
		case "taskitem":
			if(value.contains(",")){
				GiveTaskItem(Target,value.split(",")[0],Integer.valueOf(value.split(",")[1]));
			}else{
				GiveTaskItem(Target, value,-1);
			}
			
			break;
		case "teleport":
			Teleport(Target, value,autoworld);
			break;
		case "setblock":
			FLocation loc = new FLocation(value.split(",")[1],autoworld);
			loc.getWorld().getBlockAt(loc.ToMC(null)).setType(Material.getMaterial(value.split(",")[0]));
			break;
		case "notice":
			group.sendNotice(value);
			break;
		case "globalnotice":
			Lobby l = group.byLobby();
			for(Group g : l.getGroupList()){
				g.sendNotice(value);
			}
			break;
		case "value":
			String x1 = value.split(",")[1];
			if(x1.startsWith("+")){
				group.ValueBoard().ValueAdd(value.split(",")[0], Double.parseDouble(x1.substring(1)));
			}else{
				group.ValueBoard().Value(value.split(",")[0], Double.parseDouble(x1));
			}
			break;
		case "globalvalue":
			String x2 = value.split(",")[1];
			if(x2.startsWith("+")){
				group.byLobby().ValueBoard().ValueAdd(value.split(",")[0], Double.parseDouble(x2.substring(1)));
			}else{
				group.byLobby().ValueBoard().Value(value.split(",")[0], Double.parseDouble(x2));
			}
			break;
		case "playervalue":
			String x3 = value.split(",")[1];
			if(x3.startsWith("+")){
				group.PlayerValueBoard().ValueAdd(value.split(",")[0], Double.parseDouble(x3.substring(1)),Target);
			}else{
				group.PlayerValueBoard().Value(value.split(",")[0], Double.parseDouble(x3),Target);
			}
			
			break;
		case "globalplayervalue":
			String x4 = value.split(",")[1];
			if(x4.startsWith("+")){
				group.byLobby().PlayerValueBoard().ValueAdd(value.split(",")[0], Double.parseDouble(x4.substring(1)),Target);
			}else{
				group.byLobby().PlayerValueBoard().Value(value.split(",")[0], Double.parseDouble(x4),Target);
			}
			break;
		case "respond":
			respond(value,Target,false);
			break;
		case "globalrespond":
			respond(value,Target,true);
			break;
		case "goto":
			if(value.split(",").length>1){
				String check = value.split(",")[1];
				if(If(check,Target)){
					String m = value.split(",")[0];
					if(marklist.containsKey(m)){
						RunningPoint = marklist.get(m);
					}
				}
			}else{
				if(marklist.containsKey(value)){
					RunningPoint = marklist.get(value);
				}
			}
			
			break;

		case "end":
			if (value != null) {
				if(If(value,Target)){
					RunningPoint = tasklist.size()+1;
				}
			}else{
				RunningPoint = tasklist.size()+1;
			}
			
			break;
		case "join":

			group.byLobby().ChangeGroup(Target, value);
			return true;
		case "divide":
			String[] sl = value.split(",");
			List<Player> plis = new ArrayList<>();
			plis.addAll(group.getPlayerList());
			int a = 0;
			for(Player pl : plis){
				Group g = group.byLobby().getGroup(sl[a]);
				
				boolean passed = false;
				int badTime = 0;
				while(!passed){
					if(g!=null && g.getMaxPlayer()>g.GetPlayerAmount()){
						
						group.byLobby().ChangeGroup(pl, sl[a]);
						passed = true;
					}else{
						badTime++;
					}
					a++;
					if(a>=sl.length){
						a=0;
					}
					if(badTime>group.byLobby().getGroupList().size()+2){
						
						pl.sendMessage(ChatColor.RED+"已没有可跳转的队伍了！");
						passed = true;
					}
				}
			}
			return true;
		case "enabletrigger":
			if(value.equals("all")){
				for(TriggerBase e : group.getTrigger().getTriggerList()){
					HandlerList.unregisterAll(e);
					Data.fmain.getServer().getPluginManager().registerEvents(e,Data.fmain);
				}
				return true;
			}
			if(group.getTrigger().getTrigger(value)!=null){
				HandlerList.unregisterAll(group.getTrigger().getTrigger(value));
				Data.fmain.getServer().getPluginManager().registerEvents
				(group.getTrigger().getTrigger(value),Data.fmain);
				return true;
			}
			return false;
		case "disabletrigger":
			if(value.equals("all")){
				for(TriggerBase e : group.getTrigger().getTriggerList()){
					HandlerList.unregisterAll(e);
				}
				return true;
			}
			if(group.getTrigger().getTrigger(value)!=null){
				HandlerList.unregisterAll(group.getTrigger().getTrigger(value));
				return true;
			}
			return false;
		case "stopgame":
			ShutDown(group);
			group.byLobby().Clear();
			break;
		case "stoptask":
			for(GroupTaskRunner g : H){
				if(g.group==group && g.taskName.equals(value)) {
					g.ShutDown();
					return true;
				}
			}
			
			break;
		case "skip":
			if(value.split(",").length>1){
				String check = value.split(",")[1];
				if(If(check,Target)){
					RunningPoint = RunningPoint+Integer.valueOf(value.split(",")[0]);
				}
				
			}else{
				RunningPoint = RunningPoint+Integer.valueOf(value);
			}
			break;
		case "data":
			Data.data.Value(value.split(",")[0], value.split(",")[1]);
			break;
		case "endwhenclear":
			EndWhenClear=true;
			break;
		case "timer":
			group.getTimer().addCustomTimer(value.split(",")[0], Integer.valueOf(value.split(",")[1]));
			break;
		case "midjoin":
			if(value=="true"){
				group.byLobby().MidJoinEnable(true);
			}else{
				group.byLobby().MidJoinEnable(false);
			}
			
			break;
		case "placeholder":
			if(group.byLobby().PlaceHolder() !=null){
				group.byLobby().PlaceHolder().CustomPlaceHolder(value.split(",")[0], value.split(",")[1]);
			}else{
				Data.ConsoleInfo("在请求游戏"+group.byLobby().getName()+"的PlaceHolderAPI连接时出错！");
			}
			
			break;
		case "addhologram":
			String[] args = value.split(",");
			FLocation lo = new FLocation(args[0],autoworld);
			group.hd.AddHologram(lo.ToMC(null), args[1], args[2]);
			break;
		case "delhologram":
			group.hd.DelHologram(value);
			break;
		case "edithologram":
			group.hd.EditHologram(value.split(",")[0], value.split(",")[1]);
			break;
		case "clearhologram":
			group.hd.ClearHologram();
			break;
		case "spawnmob":
			String[] vl = value.split(",");
			String[] v = vl[2].split(" ");
			if(v.length==3 && playerlist.size()>0){
				Data.ConsoleCommand("mm m spawn "+vl[0]+" "+vl[1]+" "+autoworld.getName()+","+v[0]+","+v[1]+","+v[2]);
			}else if(v.length>=4){
				Data.ConsoleCommand("mm m spawn "+vl[0]+" "+vl[1]+" "+v[3]+","+v[0]+","+v[1]+","+v[2]);
			}else{
				return false;
			}

			break;
		case "checkprice":
			if (value=="true") {
				if(!group.checkPrice(Target, true)){
					group.LeaveGroup(Target, false);
				}
			}else{
				if(!group.checkPrice(Target, false)){
					group.LeaveGroup(Target, false);
				}
			}
			break;
		case "checkitem":
			if(!JoinPrice.itemCheck(Target, value)){
				RunningPoint = tasklist.size()+1;
			}
			break;
		case "consumeitem":
			if(!JoinPrice.itemConsume(Target, value)){
				RunningPoint = tasklist.size()+1;
			}
			break;
		case "removemobs":

			FArena arena = new FArena(value,autoworld);
			if(arena.isComplete()){
				List<LivingEntity> e;
				if(arena.getWorld()!=null){
					e = arena.getWorld().getLivingEntities();
					
				}else{
					e = this.group.getPlayerList().get(0).getWorld().getLivingEntities();
					
				}
				for(LivingEntity en : e){
					if(!(en instanceof Player) && !(en instanceof HumanEntity)
							&& !(en instanceof Villager)){
						if(arena.inArea(new FLocation(en.getLocation()))){
							en.remove();
						}
					}
				}
			}
			break;
		case "tag":
			if (value==null || value.split(",").length<2) {
				return false;
			}
			String[] argst = value.split(",");
			group.Tags().addTag(argst[0], argst[1]);
			break;
		case "mark":
			break;
		case "funcmark":
			if(value==null){
				return false;
			}
			String[] ag = new String[1];
			if(value.contains(",")){
				ag = value.split(",");
			}else{
				ag[0] = value;
			}
			
			TaskFunction func = TaskFunction.getFunction(ag[0]);
			
			List<String> para = func.getParemeter();
			if(para.size()==ag.length-1){
				for(int px= 0;px<func.getParemeter().size();px++){
					this.group.Tags().addTag(para.get(px), ag[px+1]);
				}
			}else{
				Data.ConsoleInfo("====[一项任务因出错被取消！]====");
				Data.ConsoleInfo("错误类型：使用函数时不符合参数要求");
				Data.ConsoleInfo("内容: " + this.tasklist.get(RunningPoint));
				Data.ConsoleInfo("该函数需要参数个数： " + para.size());
				Data.ConsoleInfo("位于: 游戏文件夹 【"+ group.byLobby().getName() +"】 的队列文件 【" + group.GetName() +"】");
			
			}
			break;
		default:
			for(Task e : evtlist){
				if(e.identity().equals(label)){
					e.TaskListen(group, value, Target);
					return true;
				}
			}
			Data.ConsoleInfo("出现无法识别的Task指令"+label+"？");
			return false;
		}
		return true;
	}

	private void Potion(Player target, String value)  throws NullPointerException,NumberFormatException,IndexOutOfBoundsException{
		String[] args = value.split(",");
		if(args.length>2){
			PotionEffectType eff = PotionEffectType.getByName(args[0]);
			target.addPotionEffect(new PotionEffect(eff,Integer.valueOf(args[1]),Integer.valueOf(args[2])));
		}
		return;
	}

	private void CommandRunner(String command, Player Target) {
		if (Target != null) {
			group.PassNextCommand();
			if (Target.isOp()) {
				Target.chat("/" + command);
			} else {
				Target.setOp(true);
				Target.chat("/" + command);
				Target.setOp(false);
			}
		} else {
			Data.ConsoleCommand(command);
		}
	}
	
	private void Delay(int delay) {
		BreakTime = delay;
	}

	private void Heal(Player player, Double d) {
		if (d != null) {
			if (((Damageable)player).getHealth() + d <= ((Damageable)player).getMaxHealth()) {
				player.setHealth(((Damageable)player).getHealth() + d);
			} else {
				player.setHealth(((Damageable)player).getMaxHealth());
			}
		} else {
			player.setHealth(((Damageable)player).getMaxHealth());
		}
	}

	private void Food(Player player, Integer amount) {
		if (amount != null) {
			if (player.getFoodLevel() + amount <= 20) {
				player.setFoodLevel(player.getFoodLevel() + amount);
			} else {
				player.setFoodLevel(20);
			}
		} else {
			player.setFoodLevel(20);
		}
	}

	private void GiveTaskItem(Player player, String Name ,int Slot) {
		if (ItemTask.getItemTask(Name) != null) {
			ItemTask.getItemTask(Name).Give(player,Slot);
		} else {
			Data.ConsoleInfo("尝试获取任务执行器"+Name+"时发生错误：物品不存在。");
		}
	}

	private void Damage(Player player, double d) {
		if (((Damageable)player).getHealth() > d) {
			player.setHealth(player.getHealth() - d);
		} else {
			player.setHealth(0);
		}
	}

	private void Teleport(Player player, String Location,World autoworld) {
		Teleporter tel = new Teleporter(player);
		FLocation Loc = new FLocation(Location,autoworld);
		tel.Teleport(Loc, true);
	}
	
	private void respond(String value,Player Target,boolean global){
		if(value.split(",").length>1){
			String check = value.split(",")[1];
			if(If(check,Target)){
				String sp = value.split(",")[0];
				if(global){
					for(Group g : group.byLobby().getGroupList()){
						g.TriggerRespond(sp,Target);
					}
				}else{
					group.TriggerRespond(sp,Target);
				}
				
			}
		}else{
			if(global){
				for(Group g : group.byLobby().getGroupList()){
					g.TriggerRespond(value,Target);
				}
			}else{
				group.TriggerRespond(value,Target);
			}
			
		}
	}
	private void ShutDown() {
		H.remove(this);
		cancel();
	}

	private static Set<Task> evtlist = new HashSet<>();
	
	public static void RegisterListener(Task listener){
		evtlist.add(listener);
	}
	
	public static void ShutDown(Group gro){
		Set<GroupTaskRunner> sd = new HashSet<>();
		for(GroupTaskRunner g : H){
			if(g.group==gro){
				sd.add(g);
			}
		}
		for(GroupTaskRunner g : sd){
			Data.Debug("-----------------------------------");
			Data.Debug("一项TaskRunner被强制取消！");
			Data.Debug("内容: " + g.tasklist.get(g.RunningPoint));
			Data.Debug("位于: 游戏文件夹 【"+ g.group.byLobby().getName() +"】 的队列 【" + g.group.GetName() +"】");
			Data.Debug("-----------------------------------");
			g.ShutDown();
		}
	}

	private static Set<GroupTaskRunner> H = new HashSet<>();
	
}

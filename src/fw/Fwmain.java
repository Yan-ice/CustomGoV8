package fw;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import fw.frame.TaskDemoAPI;
import fw.group.Group;
import fw.group.Lobby;
import fw.group.joinprice.tili.User;
import fw.group.task.ItemTask;
import fw.group.task.TaskFunction;
import fw.group.task.ValueData;
import fw.location.FLocation;
import fw.player.OfflineListen;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;

public class Fwmain extends JavaPlugin implements Listener {
	protected boolean setupEconomy() {
		RegisteredServiceProvider<Economy> eco = getServer().getServicesManager().getRegistration(Economy.class);
		if (eco != null) {
			Data.economy = (eco.getProvider());
		}
		return Data.economy != null;
	}
	private boolean setupPoints()
    {
        RegisteredServiceProvider<PlayerPoints> points = getServer().getServicesManager().getRegistration(org.black_ixx.playerpoints.PlayerPoints.class);
        if (points != null) {
                Data.playerPoints = points.getProvider();
        }
        return (Data.playerPoints != null);
    }
	
	private boolean setupTili()
    {
        RegisteredServiceProvider<User> tili = getServer().getServicesManager().getRegistration(fw.group.joinprice.tili.User.class);
        if (tili != null) {
              Data.tili = tili.getProvider();
        }
        boolean b = (Data.tili != null);
        Data.ConsoleInfo(b+"");
        return (Data.tili != null);
    }
	
		
	public FileConfiguration load(File file) {
		if (!file.exists()) {
			saveResource(file.getName(), true);
		}
		return YamlConfiguration.loadConfiguration(file);

	}
	Set<Player> plist = new HashSet<>();
	boolean uuid = false;
	private void Fwcommands(CommandSender sender, String args[]) {
		if(args[0].equals("skip")){
			if(args.length<2){
				return;
			}
			if(args.length<3){
				if(sender instanceof Player){
					Group g = Group.SearchPlayerInGroup((Player)sender);
					if(g!=null){
						Lobby l = g.byLobby();
						if (sender.hasPermission("fw.skip." + args[1]) || CheckPerm(sender, "fw.skip")) {
							l.ChangeGroup((Player)sender, args[1]);
						}
					}
					
				}else{
					sender.sendMessage("控制台不能这么做！");
				}
			}else{
				if(sender.isOp()){
					for(Player p : Bukkit.getOnlinePlayers()){
						if(p.getName().equals(args[2])){
							Group g = Group.SearchPlayerInGroup(p);
							if(g!=null){
								Lobby l = g.byLobby();
								l.ChangeGroup(p, args[1]);
							}
							return;
						}
					}
					sender.sendMessage("无效的玩家名！");
				}
			}
		}else if(args[0].equals("item")){
			if(args.length<2){
				Help.MainHelp(sender);
				return;
			}
			
			if(args.length<3){
				if(sender instanceof Player && sender.isOp()){
					if (ItemTask.getItemTask(args[1]) != null) {
						ItemTask.getItemTask(args[1]).Give((Player)sender,-5);
						sender.sendMessage("您已获得该物品！");
					} else {
						sender.sendMessage("任务执行物品不存在。");
					}
					
				}else{
					sender.sendMessage("你不能这么做！");
				}
			}else{
				if(sender.isOp()){
					for(Player p : Bukkit.getOnlinePlayers()){
						if(p.getName().equals(args[2])){
							if (ItemTask.getItemTask(args[1]) != null) {
								ItemTask.getItemTask(args[1]).Give(p,-5);
								sender.sendMessage("您已获得该物品！");
							} else {
								sender.sendMessage("任务执行物品不存在。");
							}
						}
					}
					sender.sendMessage("无效的玩家名！");
				}
			}
			return;
		}
		if(args.length < 2){
			switch(args[0]){
			case "list":
				if (CheckPerm(sender, "fw.list")) {
					showList(sender);
				}
				break;
			case "start":
				if (CheckPerm(sender, "fw.start")) {
					showList(sender);
				}
				break;
			case "uuid":
				if (CheckPerm(sender, "fw.uuid")) {
					plist.add((Player)sender);
				}
				break;
			case "debug":
				if (CheckPerm(sender, "fw.admin")) {
					if(Data.debug){
						Data.debug = false;
						sender.sendMessage("测试模式已关闭。");
					}else{
						Data.debug = true;
						sender.sendMessage("测试模式已开启。");
					}

				}
				break;
			case "reload":
				if (CheckPerm(sender, "fw.reload")) {
					Reload(sender);
				}
				break;

			case "leave":
					if(sender instanceof Player){
						if (CheckPerm(sender, "fw.leave")) {
							Lobby.AutoLeave((Player)sender,false);
						}
					}
				break;
			case "tellraw":
				if(sender instanceof Player){
					if (CheckPerm(sender, "fw.tellraw")) {
						if(args.length>3){
							if(Lobby.getLobby(args[1])!=null){
								if(Lobby.getLobby(args[1]).getGroup(args[2])!=null){
									sender.sendMessage("消息发送成功。");
									Lobby.getLobby(args[0]).getGroup(args[2]).sendNotice(args[3].replace("&", "§"));
								}
							}
						}
					}
				}
			break;
			case "help":
				if (CheckPerm(sender, "fw.help")) {
					Help.MainHelp(sender);
					Help.LobbyHelp(sender);
					if(Bukkit.getWorld("CustomGoTec")==null){
						sender.sendMessage("§a*************************************");
						sender.sendMessage("§a检测到可以进行教程模板加载/更新！");
						sender.sendMessage("§a如果你是第一次使用插件，教程会对你有很大帮助哦~！");
						sender.sendMessage("§a");
						sender.sendMessage("想要加载/更新教程，请输入 §e/fw teach");
						sender.sendMessage("§a");
						sender.sendMessage("§7(附加提示：更新教程会重载插件~)");
						sender.sendMessage("§a*************************************");
					}
					
				}
				break;
			case "teach":
				sender.sendMessage("§d正在进行教程模板加载/更新！请耐心等待...");
				this.LoadTec(true);
				Data.ConsoleCommand("mm reload");
				this.Reload(sender);
				sender.sendMessage("§d更新完成！输入 §e/fw 1-1 join §d进入教程吧！");
				break;
			case "stop":
				if (CheckPerm(sender, "fw.stop")) {
					sender.sendMessage(ChatColor.BLUE+"安全关闭所有游戏！");
					for(Lobby l : Lobby.getLobbyList()){
						l.Clear();
					}
					Lobby.getLobbyList().clear();
				}

				break;
			case "reconnect":
				if(sender instanceof Player){
					if (CheckPerm(sender, "fw.reconnect")) {
						OfflineListen.ReConnect((Player) sender);
					}
				}
				
				break;
			default:
				String Name = args[0];
				if(Lobby.getLobby(Name)!=null){
					Help.LobbyHelp(sender);
				}else{
					Help.MainHelp(sender);
				}
				
				
			}
		}else{
			if(args[0].equals("leave")){
				if(sender.isOp()){
					for(Player p : Bukkit.getOnlinePlayers()){
						if(p.getName().equals(args[1])){
							Lobby.AutoLeave(p,false);
							return;
						}
					}
					sender.sendMessage("无效的玩家名！");
					return;
				}else{
					return;
				}
			}
			
			String Name = args[0];
			Lobby lobby = Lobby.getLobby(Name);
			if (args.length < 2) {
				Help.LobbyHelp(sender);
			} else {
				if (lobby != null) {
					switch (args[1]) {
					case "load":
					if (CheckPerm(sender, "fw.load")) {
						sender.sendMessage("重载游戏 "+args[0]+" ！");
						lobby.load();
					}
					break;
					case "unload":
						if (CheckPerm(sender, "fw.unload")) {
							sender.sendMessage("卸载游戏 "+args[0]+" ！");
							lobby.unLoad();
						}
						break;
					case "join":
						if(args.length<3){
							if(sender instanceof Player){
								if (sender.hasPermission("fw.join." + args[0]) || CheckPerm(sender, "fw.join")) {
									lobby.Join((Player)sender);
								}
							}else{
								sender.sendMessage("控制台不能这么做！");
							}
						}else{
							if(sender.isOp()){
								for(Player p : Bukkit.getOnlinePlayers()){
									if(p.getName().equals(args[2])){
										lobby.Join(p);
										return;
									}
								}
								sender.sendMessage("无效的玩家名！");
							}
							
						}
						
						break;
					case "statu":
						if (CheckPerm(sender, "fw.statu")) {
							sender.sendMessage(ChatColor.BLUE+lobby.getName()+" :");
							sender.sendMessage(ChatColor.GREEN+"默认队列："+lobby.getDefaultGroup().GetDisplay());
							for(Group gro : lobby.getGroupList()){
								gro.state(sender);
							}
						}

						break;
					default:
						Help.LobbyHelp(sender);
					}
						
				} else {
					if (args[1].equals("load") && CheckPerm(sender, "fw.load")) {
						
						for(File f : Data.lobbyDir.listFiles()){
							if(f.getName().equals(args[0])){
								Lobby l = new Lobby(f);
								l.addToList();
								sender.sendMessage("已加载游戏 "+args[0]+" !");
								return;
							}
						}
						sender.sendMessage("未找到可加载的文件夹 "+args[0]+" !");
					}
					sender.sendMessage("队列不存在！");
				}
			}
		}
	}

/**
 * 发送大厅列表。
 * @param player 要发送的玩家
 */
	public void showList(CommandSender player) {
		String Glist = "";
		Set<Lobby> Lobbylist = Lobby.getLobbyList();
		for (Lobby l : Lobbylist) {
			String Name;
			if (l.isComplete()) {
				Name = ChatColor.GREEN + l.getName() + ChatColor.AQUA;
			} else {
				Name = ChatColor.RED + l.getName() + ChatColor.AQUA;
			}
			if (Glist != "") {
				Glist = Glist + "、" + Name;
			} else {
				Glist = Name;
			}
		}
		player.sendMessage(ChatColor.AQUA + "当前游戏列表:");
		player.sendMessage(Glist);
	}
/**
 * 检查一个玩家的一项权限。
 * fw.admin可以直接通过检查而无需验证是否有需要权限。
 * @param sender 被检查的玩家
 * @param Permission 需要检查的权限
 * @return 是否通过检查
 */
	public static boolean CheckPerm(CommandSender sender, String Permission) {
		if (sender.hasPermission("fw.admin") || sender.hasPermission(Permission)) {
			return true;
		} else {
			sender.sendMessage(ChatColor.RED+"您没有权限这样做！缺少权限："+Permission);
			return false;
		}
	}

	public void onEnable() {
		Data.fmain = this;
		getServer().getPluginManager().registerEvents(this, this);
		if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
			Data.isEco = setupEconomy();
		} else {
			getLogger().info("未检测到Vault前置插件！无法使用金钱功能！");
		}
		
		if (Bukkit.getPluginManager().getPlugin("PlayerPoints") != null) {
			Data.isPoints = setupPoints();
		} else {
			getLogger().info("未检测到PlayerPoints前置插件！无法使用点券功能！");
		}
		
		if (Bukkit.getPluginManager().getPlugin("CustomGo-Tili") != null) {
			//Data.isTili = setupTili();
			Data.isTili = true;
			
		} else {
			getLogger().info("未检测到CustomGo-Tili前置插件！无法使用体力功能！");
		}
		
		try{
			
			LoadFile();
			SendToData();
			
			getLogger().info("插件启动成功！ [CustomGo " + Data.Version + " ]");
			
			TaskFunction.LoadAll(Data.functionDir);
			
			new BukkitRunnable(){
				
				@Override
				public void run() {
					try{
						getLogger().info("正在准备读取队列...");
						Lobby.LoadAll(lobby);
						ItemTask.LoadAll(itemd);
						JoinSign.DisposeAllSign();
					}catch(NoClassDefFoundError e){
						getLogger().info("=====[出现类文件缺失错误！]=====");
						getLogger().info("请检查是否有以下任何情况发生：");
						getLogger().info("【1】PAPI实际版本与Option.yml填写不符");
						getLogger().info("【2】更换插件/扩展插件jar未重启(重启解决)");
						getLogger().info("【3】jar损坏(重新下载插件/扩展插件)");
						getLogger().info("===========================");
					}catch(IllegalPluginAccessException e){
						getLogger().info("=====[出现插件注册错误！]=====");
						getLogger().info("请检查是否有以下任何情况发生：");
						getLogger().info("【1】用了plugman/YUM重载插件(重启解决)");
						getLogger().info("【2】在插件初始化时有错误日志(暂时移除配置)");
						getLogger().info("【3】jar损坏(重新安装插件/扩展插件)");
						getLogger().info("===========================");
					
					}
					if(Bukkit.getWorld("CustomGoTec")==null){
						getLogger().info("*************************************");
						getLogger().info("检测到可以进行教程模板加载/更新！");
						getLogger().info("作为第一次使用插件的话，教程能够给予你很大帮助哦");
						getLogger().info("");
						getLogger().info(ChatColor.YELLOW+"如果想要安装教程和模板，请输入"+ChatColor.LIGHT_PURPLE+" /fw teach");
						getLogger().info("");
						getLogger().info("(附加提示：更新教程会重载插件哦~)");
						getLogger().info("*************************************");
					}
				}

				
			}.runTaskLater(this, 80);
		}catch(LinkageError e){
			getLogger().info("=====[出现链接错误！]=====");
			getLogger().info("请检查是否有以下任何情况发生：");
			getLogger().info("【1】用了plugman/YUM重载插件(重启解决)");
			getLogger().info("===========================");
			
		}
		
	}

	public void onDisable() {
		Data.onDisable=true;
		JoinSign.SaveAllSign();
		getLogger().info("正在无触发器退出所有玩家...");
		Lobby.unloadTeam();
		Lobby.UnLoadAll();
		ItemTask.UnLoadAll();
		TaskFunction.UnLoadAll();
		Data.data.Save();
		HandlerList.unregisterAll((Plugin)this);
		PlaceholderAPI.unregisterPlaceholderHook(this);
		getLogger().info("插件关闭成功！");
	}
/**
 * 重载插件。
 * @param sender 发送重载信息的对象
 */
	public void Reload(CommandSender sender) {
		JoinSign.SaveAllSign();
		ItemTask.UnLoadAll();
		Lobby.UnLoadAll();
		TaskFunction.UnLoadAll();
		HandlerList.unregisterAll((Plugin)this);
		Data.data.Save();
		
		PlaceholderAPI.unregisterPlaceholderHook(this);
		
		LoadFile();
		SendToData();
		
		Lobby.LoadAll(lobby);
		ItemTask.LoadAll(itemd);
		TaskFunction.LoadAll(func);
		getServer().getPluginManager().registerEvents(this, this);
		
		JoinSign.DisposeAllSign();
		if(sender != null){
			sender.sendMessage("插件重载成功！ [CustomGo " + Data.Version + " ]");
		}
		
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			if(Data.debug){
				if(CheckPerm(sender,"fw.debug")){
					switch(label){
					case "fw":
						Fwcommands(sender, args);
						break;
					case "fwtask":
						if(sender instanceof Player) {
							Player pl = (Player)sender;
							TaskDemoAPI.FWTask(pl);
							
						}
						break;
					}

				}else{
					sender.sendMessage("管理员开启了测试模式！暂时无法使用指令...");
				}
			}else{
				switch(label){
				case "fw":
					Fwcommands(sender, args);
					break;
				case "fwtask":
					if(sender instanceof Player) {
						Player pl = (Player)sender;
						TaskDemoAPI.FWTask(pl);
					}
					break;
				}
			}
		return false;
	}

	public static Economy economy;

	protected File lobby = new File(getDataFolder(), "lobby");
	protected File itemd = new File(getDataFolder(), "itemtask");
	protected File func = new File(getDataFolder(), "function");
	public File data;
	protected File option;
	protected static FileConfiguration optionfile;
	protected static ValueData d;
	
	private void LoadTec(boolean cat){
		
		this.saveResource("CustomGo.zip", true);
		File zip = new File(getDataFolder(), "CustomGo.zip");
		FileMng.unZip(zip,"plugins");
		zip.delete();
		
		this.saveResource("CustomGoTec.zip", true);
		zip = new File(getDataFolder(), "CustomGoTec.zip");
		
		File csgt;
		csgt = new File("world/CustomGoTec");
		csgt.mkdir();
		FileMng.unZip(zip,csgt.getAbsolutePath());
		
		csgt = new File("CustomGoTec");
		csgt.mkdir();
		FileMng.unZip(zip,csgt.getAbsolutePath());
		
		zip.delete();
		
		getServer().createWorld(WorldCreator.name("CustomGoTec"));
	}
	private void LoadFile() {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		if (!lobby.exists()) {
			lobby.mkdir();
		}

		if (!itemd.exists()) {
			itemd.mkdir();
		}
		if (!func.exists()) {
			func.mkdir();
		}
		data = new File(getDataFolder(), "Data.yml");
		option = new File(getDataFolder(), "Option.yml");
		optionfile = load(option);
		d = new ValueData(load(data));
	}

	private void SendToData() {
		Data.fmain = this;

		Data.optionFile = option;
		Data.lobbyDir = lobby;
		Data.itemDir = itemd;
		Data.functionDir = func;
		Data.optionFileConf = optionfile;

		Data.data = d;
		Data.LoadOption();
	}
	
	@EventHandler(priority = EventPriority.LOW)
	private static void CreateSign(SignChangeEvent evt){
		if(evt.getLine(0).startsWith("[") && evt.getLine(0).endsWith("]") && evt.getLine(0).split("-").length>1){
			String lobby = evt.getLine(0).split("-")[1].replace("]", "");
			String name = evt.getLine(0).split("-")[0].replace("[", "");
			for(Lobby l:Lobby.getLobbyList()){
				if(l.getName().equals(lobby)){
					List<String> info = new ArrayList<>();
					info.add(evt.getLine(1));
					info.add(evt.getLine(2));
					info.add(evt.getLine(3));
					new JoinSign(name,new FLocation(evt.getBlock().getLocation()),l,info);
					
					evt.getPlayer().sendMessage("加入牌子创建成功！");
					return;
				}
			}
			evt.getPlayer().sendMessage("加入牌子创建失败！");
		}
	}
	
	@EventHandler
	private void PlayerJoinTip(PlayerJoinEvent evt){
		final Player sender = evt.getPlayer();
		if(evt.getPlayer().isOp()) {
			if(Bukkit.getWorld("CustomGoTec")==null){
				
				new BukkitRunnable() {

					@Override
					public void run() {
						
						if(sender.isOp()) {
							sender.sendMessage("§a*************************************");
							sender.sendMessage("§a检测到可以进行教程模板加载/更新！");
							sender.sendMessage("§a如果你是第一次使用插件，教程会对你有很大帮助哦~！");
							sender.sendMessage("§a");
							sender.sendMessage("想要加载/更新教程，请输入 §d/fw teach");
							sender.sendMessage("§a");
							sender.sendMessage("§7(附加提示：更新教程会重载插件~)");
							sender.sendMessage("§a*************************************");
						}
						// TODO Auto-generated method stub
						
					}
					
				}.runTaskLater(this, 100);
				
			}
		}
		if(OfflineListen.getOfflineListener(evt.getPlayer())!=null){
			OfflineListen l = OfflineListen.getOfflineListener(evt.getPlayer());
			evt.getPlayer().sendMessage(ChatColor.RED+"您上次断开连接时 "+l.getGroup().GetDisplay()+" 尚未结束,且在可重新连接时间内(剩余"+l.getTime()+"秒)。");
			evt.getPlayer().sendMessage(ChatColor.RED+"您可以输入/fw reconnect重新连接到"+l.getGroup().GetDisplay()+"中。");
		}
	}
	
	@EventHandler
	private void Playeruuid(EntityDamageByEntityEvent evt){
		if(evt.getDamager() instanceof Player && evt.getEntity() instanceof LivingEntity){
			Player pl = (Player)evt.getDamager();
			if(plist.contains(pl)){
				plist.remove(pl);
				pl.sendMessage("UUID: "+evt.getEntity().getUniqueId().toString());
			}
		}
	}

}

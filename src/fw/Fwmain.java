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
					sender.sendMessage("����̨������ô����");
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
					sender.sendMessage("��Ч���������");
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
						sender.sendMessage("���ѻ�ø���Ʒ��");
					} else {
						sender.sendMessage("����ִ����Ʒ�����ڡ�");
					}
					
				}else{
					sender.sendMessage("�㲻����ô����");
				}
			}else{
				if(sender.isOp()){
					for(Player p : Bukkit.getOnlinePlayers()){
						if(p.getName().equals(args[2])){
							if (ItemTask.getItemTask(args[1]) != null) {
								ItemTask.getItemTask(args[1]).Give(p,-5);
								sender.sendMessage("���ѻ�ø���Ʒ��");
							} else {
								sender.sendMessage("����ִ����Ʒ�����ڡ�");
							}
						}
					}
					sender.sendMessage("��Ч���������");
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
						sender.sendMessage("����ģʽ�ѹرա�");
					}else{
						Data.debug = true;
						sender.sendMessage("����ģʽ�ѿ�����");
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
									sender.sendMessage("��Ϣ���ͳɹ���");
									Lobby.getLobby(args[0]).getGroup(args[2]).sendNotice(args[3].replace("&", "��"));
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
						sender.sendMessage("��a*************************************");
						sender.sendMessage("��a��⵽���Խ��н̳�ģ�����/���£�");
						sender.sendMessage("��a������ǵ�һ��ʹ�ò�����̳̻�����кܴ����Ŷ~��");
						sender.sendMessage("��a");
						sender.sendMessage("��Ҫ����/���½̳̣������� ��e/fw teach");
						sender.sendMessage("��a");
						sender.sendMessage("��7(������ʾ�����½̳̻����ز��~)");
						sender.sendMessage("��a*************************************");
					}
					
				}
				break;
			case "teach":
				sender.sendMessage("��d���ڽ��н̳�ģ�����/���£������ĵȴ�...");
				this.LoadTec(true);
				Data.ConsoleCommand("mm reload");
				this.Reload(sender);
				sender.sendMessage("��d������ɣ����� ��e/fw 1-1 join ��d����̳̰ɣ�");
				break;
			case "stop":
				if (CheckPerm(sender, "fw.stop")) {
					sender.sendMessage(ChatColor.BLUE+"��ȫ�ر�������Ϸ��");
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
					sender.sendMessage("��Ч���������");
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
						sender.sendMessage("������Ϸ "+args[0]+" ��");
						lobby.load();
					}
					break;
					case "unload":
						if (CheckPerm(sender, "fw.unload")) {
							sender.sendMessage("ж����Ϸ "+args[0]+" ��");
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
								sender.sendMessage("����̨������ô����");
							}
						}else{
							if(sender.isOp()){
								for(Player p : Bukkit.getOnlinePlayers()){
									if(p.getName().equals(args[2])){
										lobby.Join(p);
										return;
									}
								}
								sender.sendMessage("��Ч���������");
							}
							
						}
						
						break;
					case "statu":
						if (CheckPerm(sender, "fw.statu")) {
							sender.sendMessage(ChatColor.BLUE+lobby.getName()+" :");
							sender.sendMessage(ChatColor.GREEN+"Ĭ�϶��У�"+lobby.getDefaultGroup().GetDisplay());
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
								sender.sendMessage("�Ѽ�����Ϸ "+args[0]+" !");
								return;
							}
						}
						sender.sendMessage("δ�ҵ��ɼ��ص��ļ��� "+args[0]+" !");
					}
					sender.sendMessage("���в����ڣ�");
				}
			}
		}
	}

/**
 * ���ʹ����б�
 * @param player Ҫ���͵����
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
				Glist = Glist + "��" + Name;
			} else {
				Glist = Name;
			}
		}
		player.sendMessage(ChatColor.AQUA + "��ǰ��Ϸ�б�:");
		player.sendMessage(Glist);
	}
/**
 * ���һ����ҵ�һ��Ȩ�ޡ�
 * fw.admin����ֱ��ͨ������������֤�Ƿ�����ҪȨ�ޡ�
 * @param sender ���������
 * @param Permission ��Ҫ����Ȩ��
 * @return �Ƿ�ͨ�����
 */
	public static boolean CheckPerm(CommandSender sender, String Permission) {
		if (sender.hasPermission("fw.admin") || sender.hasPermission(Permission)) {
			return true;
		} else {
			sender.sendMessage(ChatColor.RED+"��û��Ȩ����������ȱ��Ȩ�ޣ�"+Permission);
			return false;
		}
	}

	public void onEnable() {
		Data.fmain = this;
		getServer().getPluginManager().registerEvents(this, this);
		if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
			Data.isEco = setupEconomy();
		} else {
			getLogger().info("δ��⵽Vaultǰ�ò�����޷�ʹ�ý�Ǯ���ܣ�");
		}
		
		if (Bukkit.getPluginManager().getPlugin("PlayerPoints") != null) {
			Data.isPoints = setupPoints();
		} else {
			getLogger().info("δ��⵽PlayerPointsǰ�ò�����޷�ʹ�õ�ȯ���ܣ�");
		}
		
		if (Bukkit.getPluginManager().getPlugin("CustomGo-Tili") != null) {
			//Data.isTili = setupTili();
			Data.isTili = true;
			
		} else {
			getLogger().info("δ��⵽CustomGo-Tiliǰ�ò�����޷�ʹ���������ܣ�");
		}
		
		try{
			
			LoadFile();
			SendToData();
			
			getLogger().info("��������ɹ��� [CustomGo " + Data.Version + " ]");
			
			TaskFunction.LoadAll(Data.functionDir);
			
			new BukkitRunnable(){
				
				@Override
				public void run() {
					try{
						getLogger().info("����׼����ȡ����...");
						Lobby.LoadAll(lobby);
						ItemTask.LoadAll(itemd);
						JoinSign.DisposeAllSign();
					}catch(NoClassDefFoundError e){
						getLogger().info("=====[�������ļ�ȱʧ����]=====");
						getLogger().info("�����Ƿ��������κ����������");
						getLogger().info("��1��PAPIʵ�ʰ汾��Option.yml��д����");
						getLogger().info("��2���������/��չ���jarδ����(�������)");
						getLogger().info("��3��jar��(�������ز��/��չ���)");
						getLogger().info("===========================");
					}catch(IllegalPluginAccessException e){
						getLogger().info("=====[���ֲ��ע�����]=====");
						getLogger().info("�����Ƿ��������κ����������");
						getLogger().info("��1������plugman/YUM���ز��(�������)");
						getLogger().info("��2���ڲ����ʼ��ʱ�д�����־(��ʱ�Ƴ�����)");
						getLogger().info("��3��jar��(���°�װ���/��չ���)");
						getLogger().info("===========================");
					
					}
					if(Bukkit.getWorld("CustomGoTec")==null){
						getLogger().info("*************************************");
						getLogger().info("��⵽���Խ��н̳�ģ�����/���£�");
						getLogger().info("��Ϊ��һ��ʹ�ò���Ļ����̳��ܹ�������ܴ����Ŷ");
						getLogger().info("");
						getLogger().info(ChatColor.YELLOW+"�����Ҫ��װ�̳̺�ģ�壬������"+ChatColor.LIGHT_PURPLE+" /fw teach");
						getLogger().info("");
						getLogger().info("(������ʾ�����½̳̻����ز��Ŷ~)");
						getLogger().info("*************************************");
					}
				}

				
			}.runTaskLater(this, 80);
		}catch(LinkageError e){
			getLogger().info("=====[�������Ӵ���]=====");
			getLogger().info("�����Ƿ��������κ����������");
			getLogger().info("��1������plugman/YUM���ز��(�������)");
			getLogger().info("===========================");
			
		}
		
	}

	public void onDisable() {
		Data.onDisable=true;
		JoinSign.SaveAllSign();
		getLogger().info("�����޴������˳��������...");
		Lobby.unloadTeam();
		Lobby.UnLoadAll();
		ItemTask.UnLoadAll();
		TaskFunction.UnLoadAll();
		Data.data.Save();
		HandlerList.unregisterAll((Plugin)this);
		PlaceholderAPI.unregisterPlaceholderHook(this);
		getLogger().info("����رճɹ���");
	}
/**
 * ���ز����
 * @param sender ����������Ϣ�Ķ���
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
			sender.sendMessage("������سɹ��� [CustomGo " + Data.Version + " ]");
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
					sender.sendMessage("����Ա�����˲���ģʽ����ʱ�޷�ʹ��ָ��...");
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
					
					evt.getPlayer().sendMessage("�������Ӵ����ɹ���");
					return;
				}
			}
			evt.getPlayer().sendMessage("�������Ӵ���ʧ�ܣ�");
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
							sender.sendMessage("��a*************************************");
							sender.sendMessage("��a��⵽���Խ��н̳�ģ�����/���£�");
							sender.sendMessage("��a������ǵ�һ��ʹ�ò�����̳̻�����кܴ����Ŷ~��");
							sender.sendMessage("��a");
							sender.sendMessage("��Ҫ����/���½̳̣������� ��d/fw teach");
							sender.sendMessage("��a");
							sender.sendMessage("��7(������ʾ�����½̳̻����ز��~)");
							sender.sendMessage("��a*************************************");
						}
						// TODO Auto-generated method stub
						
					}
					
				}.runTaskLater(this, 100);
				
			}
		}
		if(OfflineListen.getOfflineListener(evt.getPlayer())!=null){
			OfflineListen l = OfflineListen.getOfflineListener(evt.getPlayer());
			evt.getPlayer().sendMessage(ChatColor.RED+"���ϴζϿ�����ʱ "+l.getGroup().GetDisplay()+" ��δ����,���ڿ���������ʱ����(ʣ��"+l.getTime()+"��)��");
			evt.getPlayer().sendMessage(ChatColor.RED+"����������/fw reconnect�������ӵ�"+l.getGroup().GetDisplay()+"�С�");
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

package fw.group;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import customgo.event.group.EventGroup;
import customgo.event.group.EventOnGroupLoaded;
import customgo.event.group.EventOnGroupStart;
import customgo.event.group.EventOnGroupUnloaded;
import customgo.event.group.EventOnPlayerAmountChange;
import customgo.event.group.EventOnPlayerJoinGroup;
import customgo.event.group.EventOnPlayerLeaveGroup;
import customgo.listener.GroupListener;
import customgo.listener.ListenerTag;
import fw.Data;
import fw.JoinSign;
import fw.group.hologram.FwHologram;
import fw.group.joincheck.JoinPrice;
import fw.group.task.GroupTaskRunner;
import fw.group.task.PlayerValueBoard;
import fw.group.task.Tag;
import fw.group.task.ValueBoard;
import fw.group.trigger.Join;
import fw.group.trigger.Leave;
import fw.group.trigger.TriggerBase;
import fw.location.FLocation;
import fw.location.Teleporter;
import fw.player.OfflineListen;

/**
 * �����ÿһ�������Ӧһ����Ϸ�ڵĶ��С�
 * @author Yan_ice
 *
 */
public class Group{
	
	static Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
	
	
	/**
	 * ����һ��ָ����Ҵ��ڵĶ��С�
	 * �����Ҳ������κζ��У�����null��
	 * @param pl ָ������ҡ�
	 * @return �����������
	 */
	public static Group SearchPlayerInGroup(Player pl){
		for(Lobby l : Lobby.getLobbyList()){
			for(Group g : l.getGroupList()){
				if(g.hasPlayer(pl)){
					return g;
				}
			}
		}
		return null;
	}
	
	
	private FileConfiguration file;
	private GroupTimerControl timer;
	private PlayerRule rule = new PlayerRule();
	private boolean FreeJoin = false;
	private boolean canJoin = true;
	private Trigger trigger = new Trigger(this);
	private GListener listener;
	private JoinPrice j;
	private String Name;
	private String Display;
	public FwHologram hd = new FwHologram();
	
	protected int MinPlayer;
	protected int MaxPlayer;
	protected boolean Playing = false;
	protected boolean isComplete = false;
	protected List<Player> playerList = new ArrayList<>();
	protected int Reconnect = 0;
	public List<FLocation> LobbyLoc = new ArrayList<>();
	public List<FLocation> LeaveLoc = new ArrayList<>();
	public List<FLocation> RespawnLoc = new ArrayList<>();
	public List<FLocation> GroupLoc = new ArrayList<>();
	
	Team t;
	
	public Group(String Name, FileConfiguration f) {
		this.Name = Name;
		this.file = f;
		Loaded = false;
	}
	
	public Group(Lobby byLobby,FileConfiguration file,String name) {
		Name = name;
		this.file = file;
		Loaded = false;
		setLobby(byLobby);
		Load();
	}

	/**
	 * ���ö���������Ϸ��
	 */
	public void setLobby(Lobby byLobby){
		this.byLobby = byLobby;
	}
	
	public GroupTimerControl getTimer(){
		return timer;
	}
	
	private boolean Loaded = false;
	
	private Tag tags = new Tag();
	private ValueBoard Board = new ValueBoard();
	private PlayerValueBoard PlayerBoard = new PlayerValueBoard();
	
	protected Lobby byLobby;
	
	/**
	 * �ж϶����Ƿ�������������Ϊfalse���ö����Ǽ���ʧ�ܵġ�
	 * @return �ж϶����Ƿ����������
	 * 
	 */
	public boolean isComplete() {
		return isComplete;
	}

	/**
	 * ��ȡ���е�Display(��ʾ��)���á�
	 * @return ��ȡ���е�Display(��ʾ��)���á�
	 * 
	 */
	public String GetDisplay() {
		return Display;
	}
	
	/**
	 * ��ȡ���е�Name(������)���á�
	 * @return ��ȡ���е�Name(������)���á�
	 * 
	 */
	public String GetName() {
		return Name;
	}
	
	/**
	 * ��ȡ���е�MinPlayer(��С�����)���á�
	 * @return ��ȡ���е�MinPlayer(��С�����)���á�
	 * 
	 */
	
	public int getMinPlayer() {
		return MinPlayer;
	}
	/**
	 * ��ȡ���е�MaxPlayer(��������)���á�
	 * @return ��ȡ���е�MaxPlayer(��������)���á�
	 * 
	 */
	public int getMaxPlayer() {
		return MaxPlayer;
	}
	
	/**
	 * ��ȡ���еĵ�ǰ�������
	 * @return ��ȡ���еĵ�ǰ�������
	 * 
	 */
	public int GetPlayerAmount() {
		return playerList.size();
	}
	

	/**
	 * ����Respond����������Ӧ���롣��Ӧ����Ĵ���������������
	 * @param Message ��Ӧ����
	 * @param pl ������(����Ϊnull)
	 */
	public void TriggerRespond(String Message,Player pl){
		if(Message.contains(" ")){
			String[] s = Message.split(" ");
			String rs = s[Data.Random(0, s.length)];
			trigger.Respond(rs,pl);
		}else{
			trigger.Respond(Message,pl);
		}
		
	}
	
	/**
	 * ��ȡ���е�ȫ�ֱ����Ʒְ塣
	 * @return ��ȡ���е�ȫ�ֱ����Ʒְ塣
	 * 
	 */
	public ValueBoard ValueBoard(){
		return Board;
	}
	
	/**
	 * ��ȡ���е���ұ����Ʒְ塣
	 * @return ��ȡ���е���ұ����Ʒְ塣
	 * 
	 */
	public Tag Tags(){
		return tags;
	}
	
	/**
	 * ��ȡ���б�ǩ��
	 * @return ��ȡ���еı�ǩ��
	 * 
	 */
	public PlayerValueBoard PlayerValueBoard(){
		return PlayerBoard;
	}
	
	/**
	 * ��ȡ�����ڵ�ǰ������ҡ�
	 * @return ��ȡ�����ڵ�ǰ������ҡ�
	 * 
	 */
	public List<Player> getPlayerList(){
		return playerList;
	}

	/**
	 * ��ȡ���е������ļ���
	 * @return ��ȡ���е������ļ���
	 * 
	 */
	public FileConfiguration getFileConf() {
		if (file != null) {
			return file;
		} else {
			return null;
		}
	}
	
	/**
	 * 
	 * @return ��ȡ���е�FreeJoin������Ϣ��
	 * 
	 */
	public boolean GetFreeJoin() {
		return FreeJoin;
	}
	

	/**
	 * �Ƿ���Լ��롣��������Ѿ���ʼ,canJoin�ͻ�Ϊfalse.
	 * 
	 * @return �������
	 */
	public boolean canJoin() {
		return canJoin;
	}

	protected void SetcanJoin(boolean canJoin) {
		if(FreeJoin){
			canJoin = true;
		}else{
			this.canJoin = canJoin;
		}
	}

	public Trigger getTrigger() {
		return trigger;
	}

	/**
	 * ж�ر����С�
	 */
	public void UnLoad() {
		
		if (isComplete()) {
			while(playerList.size()>0){
				this.LeaveGroup(playerList.get(0), false);
			}
		}
		ListenerRespond(new EventOnGroupUnloaded(this));
		HandlerList.unregisterAll(listener);
		for(TriggerBase b : trigger.TriggerList) {
			b.unLoad();
		}
		isComplete = false;
	}
	/**
	 * ��ȡ/���ر�����,��debug����ʱ�ᷢ����ϸ������Ϣ��
	 * @return ��ȡ/�����Ƿ�ɹ���
	 */
	public boolean Load() {
		if(isComplete){
			Data.ConsoleInfo("�ö����Ѿ���ȡ���ˣ�");
			return false;
		}
		if(getFileConf().contains("DefaultGroup")){
			if(getFileConf().getBoolean("DefaultGroup")){
				byLobby().setDefaultGroup(this);
			}
		}
		if(getFileConf().contains("MidJoinGroup")){
			if(getFileConf().getBoolean("MidJoinGroup")){
				byLobby().setMidJoinGroup(this);
			}
		}
		
		if(Data.LoadWhenJoin){
			Data.ConsoleInfo("���ڼ�ʱ��ȡģʽ������������ʱ���ж�ȡ��");
			return false;
		}
		
		return MainLoad();
	}

	private String teamname= "FWdef";
	private boolean MainLoad(){
		try{
			if(this.byLobby.Name.length()>3){
				if(Name.length()>4){
					teamname = "F"+this.byLobby.Name.substring(0, 4)+Name.substring(0, 4);
				}else{
					teamname = "F"+this.byLobby.Name.substring(0, 4)+Name;
				}
				
			}else{
				if(Name.length()>4){
					teamname = "F"+this.byLobby.Name+Name.substring(0, 4);
				}else{
					teamname = "F"+this.byLobby.Name+Name;
				}
			}
		
		isComplete = true;
		
		if(getFileConf().contains("Display")){
			Display = getFileConf().getString("Display");
		}
		if(getFileConf().contains("FreeJoin")){
			FreeJoin = getFileConf().getBoolean("FreeJoin");
		}
		if(FreeJoin){
			MinPlayer = 0;
		}else{
			if(getFileConf().contains("MinPlayer")){
				MinPlayer = getFileConf().getInt("MinPlayer");
			}else{
				isComplete = false;
			}
		}
		
		if(getFileConf().contains("MaxPlayer")){
			MaxPlayer = getFileConf().getInt("MaxPlayer");
		}else{
			isComplete = false;
		}
		int LobbyTimer = 60;
		int Full_LobbyTimer = 10;
		int ArenaTimer = 300;
		if(getFileConf().contains("Timer.LobbyTime")){
			LobbyTimer = getFileConf().getInt("Timer.LobbyTime");
		}
		if(getFileConf().contains("Timer.Full_LobbyTime")){
			Full_LobbyTimer = getFileConf().getInt("Timer.Full_LobbyTime");
		}
		if(getFileConf().contains("Timer.ArenaTime")){
			ArenaTimer = getFileConf().getInt("Timer.ArenaTime");
		}
		timer = new GroupTimerControl(this, LobbyTimer, Full_LobbyTimer, ArenaTimer);
		
		if(getFileConf().contains("Locations.Waiting")){
			if(getFileConf().get("Locations.Waiting") instanceof String){
				LobbyLoc.add(new FLocation(getFileConf().getString("Locations.Waiting"),null));
			}else{
				List<String> Loclist = getFileConf().getStringList("Locations.Waiting");
				for (int a = 0; a < Loclist.size(); a++) {
					FLocation l = new FLocation(Loclist.get(a),null);
					if(l.isComplete()){
						LobbyLoc.add(l);
					}
				}
			}
			
		}
		if(getFileConf().contains("Locations.Leaves")){
			if(getFileConf().get("Locations.Leaves") instanceof String){
				LeaveLoc.add(new FLocation(getFileConf().getString("Locations.Leaves"),null));
			}else{
				List<String> Loclist = getFileConf().getStringList("Locations.Leaves");
				for (int a = 0; a < Loclist.size(); a++) {
					FLocation l = new FLocation(Loclist.get(a),null);
					if(l.isComplete()){
						LeaveLoc.add(l);
					}
				}
			}
			
		}

		if(getFileConf().contains("Locations.Respawn")){
			List<String> Loclist = getFileConf().getStringList("Locations.Respawn");
			
			for (int a = 0; a < Loclist.size(); a++) {
				FLocation l = new FLocation(Loclist.get(a),null);
				if(l.isComplete()){
					RespawnLoc.add(l);
				}
			}
		}
		if(getFileConf().contains("Locations.Arena")){
			List<String> Loclist = getFileConf().getStringList("Locations.Arena");
			
			for (String s : Loclist) {
				FLocation l = new FLocation(s,null);
				if(l.isComplete()){
					GroupLoc.add(l);
				}
			}
		}
		
		if (!rule.Load(this)) {
			isComplete = false;
		}

		getTrigger().load(this);
		
		if(isComplete){

			teamload();	
			j = new JoinPrice(this);
			refreshListener();
			Data.ConsoleInfo("���� "+Name+" ��ȡ�ɹ���");
			ListenerRespond(new EventOnGroupLoaded(this,true));
			onGroupLoaded();
		}else{
			Data.ConsoleInfo("���� "+Name+" ��ȡʧ�ܣ�");
			ListenerRespond(new EventOnGroupLoaded(this,false));
		}
		return isComplete;
		
		}catch(NullPointerException e){
			e.printStackTrace();
			Data.ConsoleInfo(ChatColor.RED+"�ڶ�ȡ "+Name+" ʱ������ȱʧ�����ã�");
			Data.ConsoleInfo(ChatColor.RED+"�ظ����ڶ�ȡ "+Name+" ʱ������ȱʧ�����ã�");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
			}
			isComplete=false;
			return false;
		}catch(NumberFormatException e){
			e.printStackTrace();
			Data.ConsoleInfo(ChatColor.RED+"�ڶ�ȡ "+Name+" ʱ�����˴�������ã�");
			Data.ConsoleInfo(ChatColor.RED+"�ظ����ڶ�ȡ "+Name+" ʱ�����˴�������ã�");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
			}
			isComplete=false;
			return false;
		}
		
	}
	/**
	 * �ö�����������Ҵ���Diss������
	 */
	public void Dissolve() {
		List<TriggerBase> list = getTrigger().getTriggerList();
		for (int a = 0; a < list.size(); a++) {
			if (list.get(a) instanceof fw.group.trigger.Diss) {
				list.get(a).Strike(null);
			}
		}
	}
	
	private void teamload(){
		if(!rule.useTeam){
			return;
		}
		if(board.getTeam(teamname)!=null){
			t = board.getTeam(teamname);
		}else{
			t = board.registerNewTeam(teamname);
		}
		if(Data.HighMCVersion){
			switch(rule.NameInv()){
			case 0:
				t.setOption(Team.Option.NAME_TAG_VISIBILITY, org.bukkit.scoreboard.Team.OptionStatus.NEVER);
				break;
			case 1:
				t.setOption(Team.Option.NAME_TAG_VISIBILITY, org.bukkit.scoreboard.Team.OptionStatus.FOR_OWN_TEAM);
				break;
			case 2:
				t.setOption(Team.Option.NAME_TAG_VISIBILITY, org.bukkit.scoreboard.Team.OptionStatus.ALWAYS);
				break;
			}
		}
		
		if(rule.Prefix()!=null){
			t.setPrefix(rule.Prefix().replace("&", "��"));
		}
	}
	
	protected void refreshListener(){
		if(listener!=null){
			HandlerList.unregisterAll(listener);
		}
		listener = new GListener(this);
		Data.fmain.getServer().getPluginManager().registerEvents(listener, Data.fmain);
		
	}
	/**
	 * ��鱾�����Ƿ��������ҡ�
	 * 
	 * @param player
	 *            ��������ҡ�
	 * @return �Ƿ��鵽
	 */
	public boolean hasPlayer(Player player) {
		return playerList.contains(player);
	}

	/**
	 * ��ȡ�������ڵ���Ϸ��
	 * @return ��Ϸ
	 */
	public Lobby byLobby() {
		return byLobby;
	}
	/**
	 * ��ȡ��ҹ���
	 * @return ��ҹ���
	 */
	public PlayerRule getRule(){
		return rule;
	}
	
	/**
	 * ��һ����Ҽ���ö��С�
	 * @param player ��Ҫ�������ҡ�
	 * @param byPass �Ƿ�ԽȨ����(�������������ж������������봥������Ҳ��������չ�����������)
	 */
	public boolean JoinGroup(Player player,boolean byPass) {
		if(Data.LoadWhenJoin){
			if(!Loaded){
				this.MainLoad();
				Loaded=true;
			}
		}
		if (JoinCheck(player,byPass)) {
			playerList.add(player);
			if(board.getTeam(teamname)==null||t==null){
				teamload();
			}
			if(t!=null && !t.hasPlayer(player)){
				t.addPlayer(player);
			}
			
			JoinSign.RefreshSign(byLobby());
			if (FreeJoin) {
				onPlayerJoin(player);
				
				Teleporter tel = new Teleporter(player);
				tel.TeleportRandom(GroupLoc);
				
				if (playerList.size() == 1) {// �����ﵽ�������
					timer.start(3);
				}
			} else if(byPass){
				Teleporter tel = new Teleporter(player);
				if(!canJoin()){
					onMidJoin(player);
					tel.TeleportRandom(GroupLoc);
				}else{
					onPlayerJoin(player);
					tel.TeleportRandom(LobbyLoc);
					
					if (playerList.size() == MinPlayer) {// �����ﵽ�������
						timer.start(1);
						onPlayerEnough();
					}
					if (playerList.size() == MaxPlayer) {
						timer.LobbyTimer().FastStart();
						onPlayerFull();
					}
				}
			}else{
				onPlayerJoin(player);

				Teleporter tel = new Teleporter(player);
				tel.TeleportRandom(LobbyLoc);
				if (playerList.size() == MinPlayer) {// �����ﵽ�������
					timer.start(1);
					onPlayerEnough();
				}
				if (playerList.size() == MaxPlayer) {
					timer.LobbyTimer().FastStart();
					onPlayerFull();
				}
				ListenerRespond(new EventOnPlayerAmountChange(this));
			}
			return true;
		}else{
			return false;
		}
	}
	
	
	
	private boolean JoinCheck(Player player,boolean byPass){
		
		if(!isComplete){
			player.sendMessage(ChatColor.RED + "��Ϸδ��������ȡ��");
			return false;
		}
		
		if(Group.SearchPlayerInGroup(player)!=null){
			player.sendMessage(ChatColor.RED+"����ʧ�ܣ����Ѿ���һ����Ϸ���ˣ�");
			return false;
		}
		if (GetPlayerAmount() == MaxPlayer) {
			player.sendMessage(ChatColor.RED+"����ʧ�ܣ���������������");
			return false;
		}
		if(!byPass){
			if(!FreeJoin && !canJoin()){
				player.sendMessage(ChatColor.RED+"����ʧ�ܣ���Ϸ���ڽ����У�");
				return false;
			}
			
			EventOnPlayerJoinGroup evt = new EventOnPlayerJoinGroup(this,player);
			this.ListenerRespond(evt);
			if(evt.isPrevented()){
				return false;
			}
		}
		
		if(j!=null && j.autoCheck){
			
			if(FreeJoin){
				if(!checkPrice(player,true)){
					return false;
				}
			}else{
				if(!checkPrice(player,false)){
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * ���һλ����Ƿ������������
	 * @param player ��������ҡ�
	 * @param consume �����ͨ���Ƿ�������ġ�
	 * @return �Ƿ���������
	 */
	public boolean checkPrice(Player player,boolean consume){
		if(j!=null){
			if(j.PlayerCheck(player)){
				if(consume){
					j.PlayerConsume(player);
				}
			}else{
				return false;
			}
		}
		return true;
	}
	
/**
 * ��ʼһ����FreeJoin�Ķ��С��������ⲿ���á�
 */
	public void Start() {
		if (!FreeJoin && canJoin()) {
			if (GetPlayerAmount() > 0) {
					List<FLocation> llist = new ArrayList<>();
					llist.addAll(GroupLoc);
					for (int a = playerList.size()-1;a>-1;a--) {
						Player p = playerList.get(a);
						if(this.j.autoCheck){
							if(this.checkPrice(p, true)){
								if(GroupLoc.size()>0){
									Teleporter tel = new Teleporter(p);
									if(llist.size()==0){
										llist.addAll(GroupLoc);
									}
									FLocation l = FLocation.RandomLoc(llist);
									tel.Teleport(l, false);
									llist.remove(l);
								}
								
							}else{
								p.sendMessage("����������Ϸ������������");
								this.LeaveGroup(p, false);
							}
						}else{
							if(GroupLoc.size()>0){
								Teleporter tel = new Teleporter(p);
								if(llist.size()==0){
									llist.addAll(GroupLoc);
								}
								FLocation l = FLocation.RandomLoc(llist);
								tel.Teleport(l, false);
								llist.remove(l);
							}
						}
						
						
				}
				
				onGroupStart();
				if(timer.LobbyTimer().isRunning()){
					timer.LobbyTimer().cancel();
				}
				timer.start(2);
				SetcanJoin(false);
				if(byLobby.getDefaultGroup()==this){
					
					byLobby.start();
				}
				Playing = true;
			}
		}
	}

	/**
	 * ��һ������뿪���С���Ҫ������ڶ����У�����ִ���κ����ݡ�
	 * @param player ��Ҫ�뿪����ҡ�
	 * @param noTel �Ƿ�ȡ���뿪����ʱ�Ĵ��͡�
	 */
	public void LeaveGroup(Player player,boolean noTel) {
		for (Player pl : playerList) {
			if (player.getName().equals(pl.getName())) {
				if(t!=null && board.getTeam(teamname)!=null &&  t.hasPlayer(pl)){
					t.removePlayer(pl);
				}
				
				if(!Data.onDisable){
					onPlayerLeave(player);
				}
				
				playerList.remove(pl);

				if(!noTel){
					onPlayerRest();
					ListenerRespond(new EventOnPlayerAmountChange(this));
					Teleporter tel = new Teleporter(player);
					tel.TeleportRandom(LeaveLoc);
				}
				
				if(GetPlayerAmount()==0){
					SetcanJoin(true);	
				}
				if(!Data.onDisable){
					byLobby.checkCanJoin();
				}
				JoinSign.RefreshSign(byLobby());
				return;
			}
		}
	}
	
	/**
	 * ��һ������Զ��뿪�������Ķ��С��������Ҳ����κζ��У���ִ�����ݡ�
	 * @param player ��Ҫ�뿪����ҡ�
	 * @param noTel �Ƿ�ȡ���뿪����ʱ�Ĵ��͡�
	 */
	public static void AutoLeaveGroup(Player player,boolean noTel){
		if(Group.SearchPlayerInGroup(player)!=null){
			Group.SearchPlayerInGroup(player).LeaveGroup(player,noTel);
		}
	}
	/**
	 * �������������ҷ���һ����Ϣ��
	 * @param str ��Ҫ���͵���Ϣ
	 */
	public void sendNotice(String str){
		for(Player pl : playerList){
			pl.sendMessage(str);
		}
	}
	
	/**
	 * ����ҷ��Ͷ���״̬��
	 * 
	 * @param sender
	 *            �����͵����
	 */
	public void state(CommandSender sender) {
		sender.sendMessage(ChatColor.AQUA + Display + ChatColor.WHITE + "("+Name+")����״̬��");
		if (isComplete) {
			String pl = "";
			String statu = "";
			if (playerList.size() > 0) {
				for (int a = 0; a < playerList.size(); a++) {
					pl = pl + playerList.get(a).getName() + " ";
				}
			}
			if (pl == "") {
				pl = "�����";
			}
			if(FreeJoin){
				statu = ChatColor.GREEN + "���ɼ���";
			}else{
				if(canJoin()){
					if(timer.LobbyTimer().getTime()!=timer.LobbyTimer().getMaxtime()){
						statu = ChatColor.GOLD+"������ʼ��"+timer.LobbyTimer().getTime()+"��";
					}else{
						statu = ChatColor.GREEN+"�ȴ���";
					}
				}else{
					statu = ChatColor.RED+"��Ϸ��";
				}
			}
			sender.sendMessage(ChatColor.YELLOW + "����б�:");
			sender.sendMessage(pl+ " (" + playerList.size() + "/" + MaxPlayer + ")");
			sender.sendMessage(ChatColor.YELLOW + "����״̬: " + statu);
		} else {
			sender.sendMessage(ChatColor.RED + "û�б�������ȡ");
		}
	}

	/**
	 * �������Ƿ�Ϊ�ա�
	 * @return �Ƿ�Ϊ��
	 */
	public boolean isClear() {
		return this.GetPlayerAmount() == 0;
	}

	/**
	 * ʹ����ִ��ָ����������ĸ�ʽ������һ����
	 * @param taskList �����б�
	 * @param striker ������
	 */
	public void runTask(List<String> taskList,Player striker){
		new GroupTaskRunner(taskList, this,
				null,byLobby().getPlayerList());
	}
	
	private void onGroupLoaded() {
		if (getFileConf().contains("ControlTask.onGroupLoaded")) {
			for(TriggerBase e : getTrigger().getTriggerList()){
				Data.fmain.getServer().getPluginManager().registerEvents(e,Data.fmain);
			}
			
			new GroupTaskRunner(getFileConf().getStringList("ControlTask.onGroupLoaded"), this,
					null,null);
			
		}
	}
	
	private void onPlayerJoin(Player player) {
		for(TriggerBase t : trigger.getTriggerList()){
			if(t instanceof Join){
				t.Strike(player);
			}
		}
		if (getFileConf().contains("ControlTask.onPlayerJoin")) {
			new GroupTaskRunner(getFileConf().getStringList("ControlTask.onPlayerJoin"), this,
					player,byLobby().getPlayerList());
		}
	}

	private void onMidJoin(Player player) {

		if (getFileConf().contains("ControlTask.onPlayerMidJoin")) {
			new GroupTaskRunner(getFileConf().getStringList("ControlTask.onPlayerMidJoin"), this,
					player,byLobby().getPlayerList());
		}
	}
	private void onPlayerLeave(Player player) {
		for(TriggerBase t : trigger.getTriggerList()){
			if(t instanceof Leave){
				ValueBoard().Value("InWaiting", 1);
				t.Strike(player);
			}
		}
		if(this.FreeJoin){
			if (getFileConf().contains("ControlTask.onPlayerLeave")) {
				new GroupTaskRunner(getFileConf().getStringList("ControlTask.onPlayerLeave"), this,
				player,byLobby().getPlayerList());
			}
			ListenerRespond(new EventOnPlayerLeaveGroup(this,player,false));
		}else{
			if(this.canJoin()){
				if (getFileConf().contains("ControlTask.onPlayerLeaveInWaiting")) {
					new GroupTaskRunner(getFileConf().getStringList("ControlTask.onPlayerLeaveInWaiting"), this,
							player,byLobby().getPlayerList());
				}
				ListenerRespond(new EventOnPlayerLeaveGroup(this,player,true));
			}else{
				if (getFileConf().contains("ControlTask.onPlayerLeave")) {
					new GroupTaskRunner(getFileConf().getStringList("ControlTask.onPlayerLeave"), this,
							player,byLobby().getPlayerList());
				}
				ListenerRespond(new EventOnPlayerLeaveGroup(this,player,false));
			}
		}
		
		player.setCustomName(player.getName());
		player.setCustomNameVisible(true);
	}

	private void onPlayerRest() {
		if(FreeJoin || (!FreeJoin && !this.canJoin())){
			if (getFileConf().contains("ControlTask.onPlayerRest(" + playerList.size() + ")")) {
				new GroupTaskRunner(
						getFileConf().getStringList("ControlTask.onPlayerRest(" + playerList.size() + ")"), this, null,byLobby().getPlayerList());
			}
		}
		
	}
	private void onPlayerEnough() {
		if (getFileConf().contains("ControlTask.onPlayerEnough")) {
			new GroupTaskRunner(getFileConf().getStringList("ControlTask.onPlayerEnough"), this,
					null,byLobby().getPlayerList());

		}
	}
	private void onPlayerFull() {
		if (getFileConf().contains("ControlTask.onPlayerFull")) {
			new GroupTaskRunner(getFileConf().getStringList("ControlTask.onPlayerFull"), this,
					null,byLobby().getPlayerList());

		}
	}
	private void onGroupStart() {
		if (getFileConf().contains("ControlTask.onGroupStart")) {
			new GroupTaskRunner(getFileConf().getStringList("ControlTask.onGroupStart"), this,
					null,byLobby().getPlayerList());

		}
		ListenerRespond(new EventOnGroupStart(this));
		
	}
	
	
	static List<GroupListener> llist = new ArrayList<>();

	
	public static void RegisterListener(GroupListener l){
		llist.add(l);
	}
	public static void UnRegisterListener(GroupListener l){
		llist.remove(l);
	}
	
	protected void ListenerRespond(EventGroup evt){
		Map<Method,GroupListener> runninglist = new HashMap<>();
		for(GroupListener listener : llist){
			for(Method meth : listener.getClass().getMethods()){
				if(meth.isAnnotationPresent(ListenerTag.class)){
					runninglist.put(meth,listener);
				}
			}
		}
		for(int dtime = -5;dtime <= 5;dtime++){
			for(Method run : runninglist.keySet()){
				if(run.getAnnotation(ListenerTag.class).runDelay() == dtime){
					try {
						if(run.getParameterTypes().length==1 && run.getParameterTypes()[0].equals(evt.getClass())){
							run.invoke(runninglist.get(run),evt);
						}
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	
	public void PassNextCommand(){
		if(listener!=null){
			listener.PassNextCommand();
		}
	}
	
}

class GListener implements Listener {
	Group g;
	PlayerRule rule;
	public GListener(Group g){
		this.g = g;
		rule = g.getRule();
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	private void PVPListen(EntityDamageByEntityEvent evt) {
		if (evt.getEntity() instanceof Player) {
			Player damaged = (Player) evt.getEntity();
			Player damager;
			if (evt.getDamager() instanceof Player) {
				damager = (Player) evt.getDamager();
				if (g.hasPlayer(damaged) && g.hasPlayer(damager)) {
					if (!rule.PvP()) {
						evt.setCancelled(true);
						if(rule.PvPMessage()!="none"){
							damager.sendMessage(rule.PvPMessage());
						}
						
					}else{
						if(rule.HighPriority()){
							evt.setCancelled(false);
						}
					}
				}
			}else
			if(evt.getDamager() instanceof Projectile){
				if(((Projectile)evt.getDamager()).getShooter() != null){
					if((((Projectile)evt.getDamager()).getShooter()) instanceof Player){
						damager = (Player)(((Projectile)evt.getDamager()).getShooter());
						if(g.hasPlayer(damaged) && g.hasPlayer(damager)){
							if (!rule.Projectile()) {
								evt.setCancelled(true);
								if(rule.ProjectileMessage()!="none"){
									damager.sendMessage(rule.ProjectileMessage());
								}
								
							}else{
								if(rule.HighPriority()){
									evt.setCancelled(false);
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	private void PotionListen(PotionSplashEvent evt2) {
		ThrownPotion pot = evt2.getPotion();
		if (pot.getShooter() instanceof Player) {
			Player shooter = (Player) pot.getShooter();
			if(g.hasPlayer(shooter)){
				if(!rule.Potionhit()){
					evt2.setCancelled(true);
					List<Entity> damageds = pot.getNearbyEntities(3.0, 3.0, 3.0);
					for (Entity d : damageds) {
						if (d instanceof Player) {
							Player damaged = (Player)d;
								if ((shooter != damaged) && g.hasPlayer(damaged) ) {
									if(rule.PotionhitMessage()!="none"){
										shooter.sendMessage(rule.PotionhitMessage());
									}
								} else {
									damaged.addPotionEffects(pot.getEffects());
								}
						} else if (d instanceof Creature) {
							((Creature)d).addPotionEffects(pot.getEffects());
						}
					}
				}else{
					if(rule.HighPriority()){
						evt2.setCancelled(false);
					}
				}
			}
		}
	}

	boolean passnext = false;
	void PassNextCommand(){
		passnext = true;
	}
	@EventHandler(priority=EventPriority.LOW)
	private void CommandListen(PlayerCommandPreprocessEvent evt) {
		if(evt.isCancelled()){
			return;
		}
		if (g.hasPlayer(evt.getPlayer())) {
			if(passnext){
				passnext = false;
				return;
			}
			String Command = evt.getMessage().split(" ")[0];
			if (Command.equalsIgnoreCase("/fw")) {
				return;
			}
			for (int a = 0; a < rule.WhiteListCommand().size(); a++) {
				if (Command.equalsIgnoreCase("/" + rule.WhiteListCommand().get(a))) {
					return;
				}
			}
			evt.setCancelled(true);
			evt.getPlayer().sendMessage(ChatColor.RED + "����Ϸ�н�ֹʹ�ñ�ָ�");
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	private void ChatListen(PlayerChatEvent evt) {
		if(evt.isCancelled() || evt.getMessage().startsWith("/")){
			return;
		}
		if(rule.chatInGroup() && g.hasPlayer(evt.getPlayer())){
			evt.setCancelled(true);
			String message = rule.ChatFormat();
			message = message.replace("&", "��");
			message = message.replace("%player%", evt.getPlayer().getName());
			
			message = message.replace("%group%", g.GetDisplay());
			if(evt.getMessage().startsWith("!")){
				message = message.replace("%type%", "������");
				message = message.replace("%message%", evt.getMessage().substring(1));
				for(Group g : g.byLobby.getGroupList()){
					g.sendNotice(message);
				}
			}else{
				message = message.replace("%message%", evt.getMessage());
				message = message.replace("%type%", "������");
				g.sendNotice(message);
			}
			
		}
		
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	private void Listen(PlayerRespawnEvent evt2) {
		Player pl = evt2.getPlayer();
		if (g.hasPlayer(pl)) {
			if(g.RespawnLoc!=null && g.RespawnLoc.size()>0){
				FLocation f = g.RespawnLoc.get(Data.Random(0, g.RespawnLoc.size()));
				if(f.isComplete()){
					evt2.setRespawnLocation(f.ToMC(pl));
				}
				return;
			}else if(g.GroupLoc.size()>0){
				FLocation f = g.GroupLoc.get(Data.Random(0, g.GroupLoc.size()));
				if(f.isComplete()){
					evt2.setRespawnLocation(f.ToMC(pl));
					return;
				}
			}

			return;
		}
	}
	
	@EventHandler
	private void LListen(PlayerQuitEvent evt){
		if(g.hasPlayer(evt.getPlayer())){
			new OfflineListen(evt.getPlayer(),g);
			g.LeaveGroup(evt.getPlayer(),false);
		}
	}
	
	@EventHandler
	private void LListen(EntityDamageEvent evt){
		if(g.hd.Holograms().containsValue(evt.getEntity())){
			evt.setCancelled(true);
		}
	}
}

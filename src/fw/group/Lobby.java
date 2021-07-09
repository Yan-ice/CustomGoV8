package fw.group;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import customgo.event.lobby.EventLobby;
import customgo.event.lobby.EventOnLobbyClear;
import customgo.event.lobby.EventOnLobbyLoaded;
import customgo.event.lobby.EventOnLobbyStart;
import customgo.event.lobby.EventOnLobbyUnloaded;
import customgo.event.lobby.EventOnPlayerJoinLobby;
import customgo.event.lobby.EventOnPlayerJoinLobbyFailed;
import customgo.event.lobby.EventOnPlayerLeaveLobby;
import customgo.listener.ListenerTag;
import customgo.listener.LobbyListener;
import fw.Data;
import fw.JoinSign;
import fw.group.task.PlayerValueBoard;
import fw.group.task.ValueBoard;
import fw.papi.HighValuePlaceholder;
import fw.papi.ValuePlaceholder;
import me.clip.placeholderapi.PlaceholderAPI;
/**
 * 该类的每一个对象对应一个游戏。
 * @author Yan_ice
 *
 */
public class Lobby {
	private boolean CanJoin = false;
	
	private static Set<Lobby> LobbyList = new HashSet<>();
	
	private List<JoinSign> sign = new ArrayList<>();
	
	/**
	 * 获取游戏列表。
	 * @return 列表
	 */
	public static Set<Lobby> getLobbyList(){
		return LobbyList;
	}
	
	public static void unloadTeam(){
		for(Lobby l : LobbyList){
			for(Group g : l.grouplist){
				if(g.t!=null && Group.board.getTeam(g.t.getName())!=null){
					g.t.unregister();
				}
			}
		}
	}
	
	private Set<Group> grouplist = new HashSet<>();
	
	Group Default;
	Group MidJoin;
	File Folder;
	String Name;
	
	private ValueBoard Board = new ValueBoard();
	private PlayerValueBoard PlayerBoard = new PlayerValueBoard();
	
	/**
	 * 获取全游戏的全局计分板(在Task中对应globalvalue{})
	 * @return 计分板
	 */
	public ValueBoard ValueBoard(){
		return Board;
	}
	/**
	 * 获取全游戏的玩家计分板(在Task中对应globalplayervalue{})
	 * @return 计分板
	 */
	public PlayerValueBoard PlayerValueBoard(){
		return PlayerBoard;
	}
	/**
	 * 获取游戏的玩家列表。
	 * @return 玩家列表
	 */
	public List<Player> getPlayerList(){
		List<Player> pl = new ArrayList<>();
		for(Group g : getGroupList()){
			for(Player p : g.getPlayerList()){
				pl.add(p);
			}
			
		}
		return pl;
	}
	
	
	protected boolean midjoin = true;
	
	/**
	 * 是否允许中途加入。
	 * @param b 开关
	 */
	public void MidJoinEnable(boolean b){
		midjoin = b;
	}
	
	/**
	 * 重命名队列。
	 * @param name 新的名字
	 */
	public void rename(String name){
		this.Name = name;
	}
	
	
	public Lobby(File folder){
		Folder = folder;
		this.Name = folder.getName();
		load();
	}
	
	public Lobby clone(){
		return new Lobby(Folder);
	}
	
	
	public void addToList(){
		LobbyList.add(this);
	}
	
	ValuePlaceholder h = null;
	HighValuePlaceholder hh = null;
	
	public ValuePlaceholder PlaceHolder(){
		return h;
	}
	
	/**
	 * 读取/重载该游戏。
	 */
	public void load(){
		Clear();
		for(Group g : grouplist){
			g.UnLoad();
		}
		grouplist.clear();
		for(File f : Folder.listFiles()){
			if(f.getName().contains("yml")){
				Group gro = new Group(this, Data.fmain.load(f),f.getName().split("\\.")[0]);
				grouplist.add(gro);
			}
			
		}
		if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
			boolean success = false;
			if (Data.HighPlaceHolderAPIVersion)
     	    {
				hh = new HighValuePlaceholder(this);
     	        success = hh.register();
     	    } else {
     	    	h = new ValuePlaceholder(Data.fmain,this);
     	        success = h.hook();
     	    }
     	    if (success) {
     	         System.out.println("已通过PlaceHolderAPI连接外部变量！");
     	    } else {
     	         System.out.println("与PlaceHolderAPI未能成功连接！");
     	    }
		}
		
		if(this.getDefaultGroup()==null){
			System.out.println("错误：该游戏没有设置初始队列！");
		}
		if(isComplete()){
			Data.ConsoleInfo("=====[游戏"+Name+"读取成功！]=====");
			ListenerRespond(new EventOnLobbyLoaded(this,true));
		}else{
			Data.ConsoleInfo("=====[游戏"+Name+"读取失败！]=====");
			ListenerRespond(new EventOnLobbyLoaded(this,false));
		}
		CanJoin=true;
	}
	
	/**
	 * 卸载该游戏。
	 */
	public void unLoad() {
		Clear();
		for(Group g : grouplist){
			g.UnLoad();
		}
		ListenerRespond(new EventOnLobbyUnloaded(this,false));
		if(LobbyList.contains(this)){
			LobbyList.remove(this);
		}
		
		if(hh!=null){
			PlaceholderAPI.unregisterExpansion(hh);
		}
	}
	/**
	 * 检查该大厅是否完整。
	 * 如果返回false，意味着大厅没有读取成功。
	 * @return 是否完整
	 */
	public boolean isComplete(){
		for(Group g : grouplist){
			if(!g.isComplete){
				return false;
			}
		}
		if(Default==null){
			return false;
		}
		return true;
	}
	/**
	 * 获取游戏名字
	 * @return 名字
	 */
	public String getName(){
		return Name;
	}
	
	
	/**
	 * 设置默认队列。
	 * 如果该队列本不在游戏中，自动加入游戏中。
	 * @param gro 需要被设置的队列
	 */
	public void setDefaultGroup(Group gro){
		if(setted){
			Data.ConsoleInfo("该游戏被设置了超过一个默认队列！已随机选取一个！");
		}
		setted = true;
		this.Default = gro;
		if(!grouplist.contains(gro)){
			grouplist.add(gro);
		}
		
	}
	
	private boolean setted = false;
	
	/**
	 * 获得中途加入队列。
	 * @return 中途加入队列
	 */
	public Group getMidJoinGroup(){
		return MidJoin;
	}
	
	/**
	 * 设置中途加入队列。
	 * 如果该队列本不在游戏中，自动加入游戏中。
	 * @param gro 需要被设置的队列
	 */
	public void setMidJoinGroup(Group gro){
		this.MidJoin = gro;
		if(!grouplist.contains(gro)){
			grouplist.add(gro);
		}
	}
	
	/**
	 * 获得默认队列。
	 * @return 默认队列
	 */
	public Group getDefaultGroup(){
		return Default;
	}
	
	/**
	 * 获得队列列表。
	 * @return 中途加入队列
	 */
	public Set<Group> getGroupList(){
		return grouplist;
	}
	
	/**
	 * 获得全游戏玩家数。
	 * @return 玩家数
	 */
	public int getPlayerAmount(){
		int a = 0;
		for(Group g : grouplist){
			a = a+g.GetPlayerAmount();
		}
		return a;
	}
	
	public void UnLoadSign(){
		for(int a = 0;a<sign.size();a++){
			HandlerList.unregisterAll(sign.get(a));
		}
		sign.clear();
	}
	
	void setCanJoin(boolean s){
		CanJoin = s;
	}
	public boolean canJoin(){
		return CanJoin;
	}
	
	
	public void checkCanJoin(){
		for(Group g : this.grouplist){
			if(g.GetPlayerAmount()>0){
				setCanJoin(false);
				return;
			}
		}
		for(Group g : this.grouplist){
			g.hd.ClearHologram();
			g.SetcanJoin(true);
		}
		
		ListenerRespond(new EventOnLobbyClear(this));
		MidJoinEnable(true);
		setCanJoin(true);
	}
	
	
	/**
	 * 获得游戏中的一个队列。若不存在该队列，返回null。
	 * @param Name 指定的游戏名字。
	 * @return 具有指定名字的游戏。
	 */
	public Group getGroup(String Name){
		for(Group l : grouplist){
			if(l.GetName().equals(Name)){
				return l;
			}
		}
		return null;
	}
	
	/**
	 * 清空游戏内所有玩家。
	 * @param Name 指定的游戏名字。
	 * @return 具有指定名字的游戏。
	 */
	public void Clear(){
		for(Group l : grouplist){
			l.hd.ClearHologram();
		}
		for(Player p : this.getPlayerList()){
			this.Leave(p);
		}
		this.setCanJoin(true);
	}
	
	/**
	 * 获得具有指定名字的游戏。
	 * @param Name 指定的游戏名字。
	 * @return 具有指定名字的游戏。
	 */
	public static Lobby getLobby(String Name){
		for(Lobby l : LobbyList){
			if(l.Name.equalsIgnoreCase(Name)){
				return l;
			}
		}
		return null;
	}
	
	/**
	 * 令一名玩家加入这个游戏。
	 * @param player 需要加入的玩家
	 */
	public void Join(Player player){
		if(Default==null){
			player.sendMessage(ChatColor.RED+"该游戏缺失初始队列！");
			return;
		}
		if(CanJoin){
			EventOnPlayerJoinLobby jl = new EventOnPlayerJoinLobby(this,player);
			ListenerRespond(jl);
			if(!jl.isPrevented()){
				if(!Default.JoinGroup(player, false)){
					EventOnPlayerJoinLobbyFailed jlf = new EventOnPlayerJoinLobbyFailed(this,player);
					ListenerRespond(jlf);
				}
			}else{
				EventOnPlayerJoinLobbyFailed jlf = new EventOnPlayerJoinLobbyFailed(this,player);
				ListenerRespond(jlf);
			}
			
		}else{
			if(MidJoin!=null && midjoin){
				if(this.getPlayerAmount()>=Default.MaxPlayer){
					player.sendMessage(ChatColor.RED+"加入失败：游戏人数已满！");
				}else{
					MidJoin.JoinGroup(player, false);
				}
				
			}else{
				player.sendMessage(ChatColor.RED+"加入失败：游戏正在进行中！");
			}
			
		}
		
		
		JoinSign.RefreshSign(this);
	}
	
	
	/**
	 * 令一名玩家离开这个游戏。
	 * @param player 需要离开的玩家
	 */
	public void Leave(Player player){
		for(Group gr : grouplist){
			if(gr.hasPlayer(player)){
				EventOnPlayerLeaveLobby jl = new EventOnPlayerLeaveLobby(this,player,canJoin());
				ListenerRespond(jl);
				gr.LeaveGroup(player, false);
				this.checkCanJoin();
				return;
			}
		}
	}
	/**
	 * 在游戏内转移玩家所处的队列。
	 * 该转移不会触发任何离开队列相关触发器。
	 * @param player 需要转移的玩家
	 * @param groupname 将转移到的队列名
	 */
	public void ChangeGroup(Player player,String groupname){
		if(this.getGroup(groupname)!=null){
			Group g = this.getGroup(groupname);
			if(g.isComplete()){
				Data.onDisable=true;
				Group.AutoLeaveGroup(player, true);
				Data.onDisable=false;
				g.JoinGroup(player, true);
			}else{
				player.sendMessage(ChatColor.RED+"加入失败：队列不完整！");
			}
		}else{
			player.sendMessage(ChatColor.RED+"加入失败：未找到可跳转的队列！");
		}
		
	}
	
	/**
	 * 令一名玩家自动离开他所处的游戏。
	 * @param player 需要离开的玩家。
	 * @param noTel 是否取消离开队列时的传送。
	 */
	public static void AutoLeave(Player player,boolean noTel){
		for(Lobby l : LobbyList){
			if(l.getPlayerList().contains(player)){
				l.Leave(player);
				return;
			}
		}
		player.sendMessage("您不在任何游戏中！");
	}

	/**
	 * 加载/重载所有游戏。
	 * @param lobby 加载游戏所依赖的文件夹(一般为插件配置的"lobby")
	 */
	public static void LoadAll(File lobby) {
		boolean exampled = false;
		
		for(File file : lobby.listFiles()){
			if(file.getName().equals("ExampleGame-"+Data.Version)){
				exampled = true;
			}
			if(file.isDirectory()){
				new Lobby(file).addToList();
			}
			
		}
		exampled = true;
		if(!exampled){
			File ex = new File(Data.lobbyDir, "ExampleGame-"+Data.Version);
			ex.mkdir();
			Data.fmain.saveResource(ex.getAbsolutePath(), true);

			new Lobby(ex);		
		}
	}
	
	public static void UnLoadAll() {
		// TODO 自动生成的方法存根
		for(Lobby l : LobbyList){
			for(Group g : l.grouplist){
				g.UnLoad();
			}
			l.ListenerRespond(new EventOnLobbyUnloaded(l,false));
		}
		LobbyList.clear();
	}
	
	static List<LobbyListener> llist = new ArrayList<>();

	public static void RegisterListener(LobbyListener l){
		llist.add(l);
	}
	public static void UnRegisterListener(LobbyListener l){
		llist.remove(l);
	}
	
	protected void ListenerRespond(EventLobby evt){
		for(int a = llist.size()-1;a>=0;a--){
			LobbyListener listener = llist.get(a);
			Method[] mlist = listener.getClass().getMethods();
			List<Method> runninglist = new ArrayList<>();
			for(Method meth : mlist){
				if(meth.isAnnotationPresent(ListenerTag.class)){
					runninglist.add(meth);
				}
			}
			for(int dtime = -5;dtime <= 5;dtime++){
				for(Method run : runninglist){
					if(run.getAnnotation(ListenerTag.class).runDelay() == dtime){
						try {
							if(run.getParameterTypes().length==1 && run.getParameterTypes()[0].equals(evt.getClass())){
								run.invoke(listener,evt);
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
	}

	protected void start() {
		setCanJoin(false);
		 ListenerRespond(new EventOnLobbyStart(this));
	}

}

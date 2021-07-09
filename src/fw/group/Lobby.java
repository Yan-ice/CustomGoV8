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
 * �����ÿһ�������Ӧһ����Ϸ��
 * @author Yan_ice
 *
 */
public class Lobby {
	private boolean CanJoin = false;
	
	private static Set<Lobby> LobbyList = new HashSet<>();
	
	private List<JoinSign> sign = new ArrayList<>();
	
	/**
	 * ��ȡ��Ϸ�б�
	 * @return �б�
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
	 * ��ȡȫ��Ϸ��ȫ�ּƷְ�(��Task�ж�Ӧglobalvalue{})
	 * @return �Ʒְ�
	 */
	public ValueBoard ValueBoard(){
		return Board;
	}
	/**
	 * ��ȡȫ��Ϸ����ҼƷְ�(��Task�ж�Ӧglobalplayervalue{})
	 * @return �Ʒְ�
	 */
	public PlayerValueBoard PlayerValueBoard(){
		return PlayerBoard;
	}
	/**
	 * ��ȡ��Ϸ������б�
	 * @return ����б�
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
	 * �Ƿ�������;���롣
	 * @param b ����
	 */
	public void MidJoinEnable(boolean b){
		midjoin = b;
	}
	
	/**
	 * ���������С�
	 * @param name �µ�����
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
	 * ��ȡ/���ظ���Ϸ��
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
     	         System.out.println("��ͨ��PlaceHolderAPI�����ⲿ������");
     	    } else {
     	         System.out.println("��PlaceHolderAPIδ�ܳɹ����ӣ�");
     	    }
		}
		
		if(this.getDefaultGroup()==null){
			System.out.println("���󣺸���Ϸû�����ó�ʼ���У�");
		}
		if(isComplete()){
			Data.ConsoleInfo("=====[��Ϸ"+Name+"��ȡ�ɹ���]=====");
			ListenerRespond(new EventOnLobbyLoaded(this,true));
		}else{
			Data.ConsoleInfo("=====[��Ϸ"+Name+"��ȡʧ�ܣ�]=====");
			ListenerRespond(new EventOnLobbyLoaded(this,false));
		}
		CanJoin=true;
	}
	
	/**
	 * ж�ظ���Ϸ��
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
	 * ���ô����Ƿ�������
	 * �������false����ζ�Ŵ���û�ж�ȡ�ɹ���
	 * @return �Ƿ�����
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
	 * ��ȡ��Ϸ����
	 * @return ����
	 */
	public String getName(){
		return Name;
	}
	
	
	/**
	 * ����Ĭ�϶��С�
	 * ����ö��б�������Ϸ�У��Զ�������Ϸ�С�
	 * @param gro ��Ҫ�����õĶ���
	 */
	public void setDefaultGroup(Group gro){
		if(setted){
			Data.ConsoleInfo("����Ϸ�������˳���һ��Ĭ�϶��У������ѡȡһ����");
		}
		setted = true;
		this.Default = gro;
		if(!grouplist.contains(gro)){
			grouplist.add(gro);
		}
		
	}
	
	private boolean setted = false;
	
	/**
	 * �����;������С�
	 * @return ��;�������
	 */
	public Group getMidJoinGroup(){
		return MidJoin;
	}
	
	/**
	 * ������;������С�
	 * ����ö��б�������Ϸ�У��Զ�������Ϸ�С�
	 * @param gro ��Ҫ�����õĶ���
	 */
	public void setMidJoinGroup(Group gro){
		this.MidJoin = gro;
		if(!grouplist.contains(gro)){
			grouplist.add(gro);
		}
	}
	
	/**
	 * ���Ĭ�϶��С�
	 * @return Ĭ�϶���
	 */
	public Group getDefaultGroup(){
		return Default;
	}
	
	/**
	 * ��ö����б�
	 * @return ��;�������
	 */
	public Set<Group> getGroupList(){
		return grouplist;
	}
	
	/**
	 * ���ȫ��Ϸ�������
	 * @return �����
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
	 * �����Ϸ�е�һ�����С��������ڸö��У�����null��
	 * @param Name ָ������Ϸ���֡�
	 * @return ����ָ�����ֵ���Ϸ��
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
	 * �����Ϸ��������ҡ�
	 * @param Name ָ������Ϸ���֡�
	 * @return ����ָ�����ֵ���Ϸ��
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
	 * ��þ���ָ�����ֵ���Ϸ��
	 * @param Name ָ������Ϸ���֡�
	 * @return ����ָ�����ֵ���Ϸ��
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
	 * ��һ����Ҽ��������Ϸ��
	 * @param player ��Ҫ��������
	 */
	public void Join(Player player){
		if(Default==null){
			player.sendMessage(ChatColor.RED+"����Ϸȱʧ��ʼ���У�");
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
					player.sendMessage(ChatColor.RED+"����ʧ�ܣ���Ϸ����������");
				}else{
					MidJoin.JoinGroup(player, false);
				}
				
			}else{
				player.sendMessage(ChatColor.RED+"����ʧ�ܣ���Ϸ���ڽ����У�");
			}
			
		}
		
		
		JoinSign.RefreshSign(this);
	}
	
	
	/**
	 * ��һ������뿪�����Ϸ��
	 * @param player ��Ҫ�뿪�����
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
	 * ����Ϸ��ת����������Ķ��С�
	 * ��ת�Ʋ��ᴥ���κ��뿪������ش�������
	 * @param player ��Ҫת�Ƶ����
	 * @param groupname ��ת�Ƶ��Ķ�����
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
				player.sendMessage(ChatColor.RED+"����ʧ�ܣ����в�������");
			}
		}else{
			player.sendMessage(ChatColor.RED+"����ʧ�ܣ�δ�ҵ�����ת�Ķ��У�");
		}
		
	}
	
	/**
	 * ��һ������Զ��뿪����������Ϸ��
	 * @param player ��Ҫ�뿪����ҡ�
	 * @param noTel �Ƿ�ȡ���뿪����ʱ�Ĵ��͡�
	 */
	public static void AutoLeave(Player player,boolean noTel){
		for(Lobby l : LobbyList){
			if(l.getPlayerList().contains(player)){
				l.Leave(player);
				return;
			}
		}
		player.sendMessage("�������κ���Ϸ�У�");
	}

	/**
	 * ����/����������Ϸ��
	 * @param lobby ������Ϸ���������ļ���(һ��Ϊ������õ�"lobby")
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
		// TODO �Զ����ɵķ������
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

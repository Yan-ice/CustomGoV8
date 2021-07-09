package fw.group.task;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import fw.Data;
import fw.group.Group;
import fw.location.FArena;
import fw.location.FLocation;

public class PlayerChooser {
	List<Player> player = new ArrayList<>();
	
	Player Target;
	World defaultWorld;
	Group g;
	
	PlayerChooser(Player Target,Group g,List<Player> plist,World def){
		defaultWorld = def;
		if(!plist.contains(Target)){
			plist.add(Target);
		}
		
		player = plist;
		this.g = g;
		this.Target = Target;
	}
	public List<Player> Choose(String Chooser){
		if(Chooser.indexOf("[") !=-1){
			RangeChoose(Chooser.split("\\[")[0]);
			GroupConditionChoose("["+ Chooser.split("\\[")[1]);
		}else{
			RangeChoose(Chooser);
		}
		return player;
	}
	protected void RangeChoose(String Range){
		//if (Range.indexOf("@") != -1) {
			String target = (Range.split("@")[1]);
			List<Player> newplayer = new ArrayList<>();
			switch (target) {
			case "a":
				if(player!=null && player.size()>0){
					for(Player pl : player){
						if(g.hasPlayer(pl)){
							newplayer.add(pl);
						}
					}
				}else{
					player=null;
					return;
				}
				break;
			case "e":
				if(player!=null && player.size()>0){
					return;
				}else{
					player=null;
					return;
				}
			case "p":
				if(Target!=null){
					newplayer.add(Target);
				}else{
					Data.ConsoleInfo("注意：您在运行无触发者任务时使用了@p指代器！");	
					player=null;
					return;
				}
				break;
			case "r":
				if(player!=null && player.size()>0){
					int a = Data.Random(0, player.size() - 1);
					int t = 0;
					while(!g.hasPlayer(player.get(a)) && t<=50){
						t++;
						a = Data.Random(0, player.size() - 1);
					}
					if(t>=50){
						player=null;
					}else{
						newplayer.add(player.get(a));
					}
					
					break;
				}else{
					player=null;
					return;
				}
			case "c":
				player=null;
				return;
			default:
				Calculater c = new Calculater(g,null,Target);
				target = c.ValueChange(target);
				for(Player p : player){
					if(p.getName().equals(target)){
						newplayer.add(p);
					}
				}
				break;
			}
			player=newplayer;
		//}

	}
	
	/**
	 * 处理的条件格式：[XXX,XXX,XXX]
	 */
	protected void GroupConditionChoose(String ConditionGroup){
		String[] condition = ConditionGroup.split("\\[")[1].split("\\]")[0].split(",");
		for(int a = 0;a<condition.length;a++){
			ConditionChoose(condition[a]);
		}
	}
	protected void ConditionChoose(String condition){
		String value;
		String cond;
		boolean F;
		if(condition.split("!=").length>1){
			cond = condition.split("!=")[0];
			value = condition.split("!=")[1];
			F=false;
		}else if(condition.split("=").length>1){
			cond = condition.split("=")[0];
			value = condition.split("=")[1];
			F=true;
		}else{
			return;
		}
		switch(cond){
		case "minLevel":
			MinLevel(Integer.valueOf(value));
			break;
		case "maxLevel":
			MaxLevel(Integer.valueOf(value));
			break;
		case "minLife":
			MinLife(Integer.valueOf(value));
			break;
		case "maxLife":
			MaxLife(Integer.valueOf(value));
			break;
		case "Permission":
			Permission(value,F);
			break;
		case "CheckValue":
			CheckValue(value,F);
			break;
		case "Area":
			Area(value,F);
			break;
		case "InGroup":
			InGroup(value,F);
			break;
		default:
			return;
		}
		return;
	}
	
	
	private void InGroup(String value,boolean b) {
		List<Player> newplayer = new ArrayList<>();
		for(Player pl : player){
			if(b){
				if(Group.SearchPlayerInGroup(pl).GetName().equals(value)){
					newplayer.add(pl);
				}
			}else{
				if(!Group.SearchPlayerInGroup(pl).GetName().equals(value)){
					newplayer.add(pl);
				}
			}
		}
		player = newplayer;
	}
	private void CheckValue(String value,boolean b) {
		List<Player> newplayer = new ArrayList<>();
		String Entry = value.split("-")[0];
		String val = "none";
		if(value.split("-")[0].length()>1){
			val = value.split("-")[1];
		}
		for(Player pl : player){
			if(b){
				if(g.PlayerValueBoard().getValue(Entry, pl)==Double.parseDouble(val)){
					newplayer.add(pl);
				}
			}else{
				if(g.PlayerValueBoard().getValue(Entry, pl)!=Double.parseDouble(val)){
					newplayer.add(pl);
				}
			}
			
		}
		player = newplayer;
	}
	private void MinLevel(int level){
		List<Player> newplayer = new ArrayList<>();
		for(int a = 0;a<player.size();a++){
			if(player.get(a).getLevel()>=level){
				newplayer.add(player.get(a));
			}
		}
		player = newplayer;
	}
	private void MaxLevel(int level){
		List<Player> newplayer = new ArrayList<>();
		for(int a = 0;a<player.size();a++){
			if(player.get(a).getLevel()<=level){
				newplayer.add(player.get(a));
			}
		}
		player = newplayer;
	}
	private void MinLife(int life){
		List<Player> newplayer = new ArrayList<>();
		for(int a = 0;a<player.size();a++){
			if(player.get(a).getHealthScale()*20>=life){
				newplayer.add(player.get(a));
			}
		}
		player = newplayer;
	}
	private void MaxLife(int life){
		List<Player> newplayer = new ArrayList<>();
		for(int a = 0;a<player.size();a++){
			if(((Damageable)player.get(a)).getHealth()<=life){
				newplayer.add(player.get(a));
			}
		}
		
		player = newplayer;
	}
	
	private void Permission(String Perm,boolean b){
		List<Player> newplayer = new ArrayList<>();
		for(Player p : player){
			if(b){
				if(p.hasPermission(new Permission(Perm))){
					newplayer.add(p);
				}
			}else{
				if(!p.hasPermission(new Permission(Perm))){
					newplayer.add(p);
				}
			}
		}
		player = newplayer;
	}
	private void Area(String Name,boolean b){
		List<Player> newplayer = new ArrayList<>();
		FArena a = new FArena(Name,defaultWorld);
		for(Player p : player){
			if(b){
				if(a.inArea(new FLocation(p))){
					newplayer.add(p);
				}
			}else{
				if(!a.inArea(new FLocation(p))){
					newplayer.add(p);
				}
			}
			
		}
		player = newplayer;
	}
}

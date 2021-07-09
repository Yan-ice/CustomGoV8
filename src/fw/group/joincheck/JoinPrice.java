package fw.group.joincheck;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fw.Data;
import fw.group.Group;
import fw.group.joinprice.tili.User;

public class JoinPrice{
	public static Set<JoinPrice> sjp = new HashSet<>();
	public boolean autoCheck = true;
	Group gro;
	
	int money = 0;
	int points = 0;
	int level = 0;
	int tili = 0;
	
	List<itemv> il = new ArrayList<>();
	
	int cmoney = 0;
	int cpoints = 0;
	int clevel = 0;
	int ctili = 0;
	List<itemv> cil = new ArrayList<>();
	
	public JoinPrice(Group gro){
		sjp.add(this);
		this.gro = gro;
		if(!gro.getFileConf().contains("JoinPrice")){
			return;
		}
		ConfigurationSection configurationSection = gro.getFileConf().getConfigurationSection("JoinPrice");
		
		if(configurationSection.contains("AutoCheck")){
			autoCheck = configurationSection.getBoolean("AutoCheck");
		}
		if(configurationSection.contains("Need")){
			ConfigurationSection uncon = configurationSection.getConfigurationSection("Need");
			for(String str : uncon.getKeys(false)){
				Execute(str,uncon.getString(str),false);
			}
		}
		if(configurationSection.contains("Consume")){
			ConfigurationSection con = configurationSection.getConfigurationSection("Consume");
			for(String str : con.getKeys(false)){
				Execute(str,con.getString(str),true);
			}
		}
		
	}
	
	private void Execute(String key,String value,boolean Con){
		if(Con){
			switch(key){
			case "Money":
				if(Data.isEco){
					cmoney = Integer.valueOf(value);
					if(cmoney>money){
						money = cmoney;
					}
				}else{
					Data.Debug("缺少Vault经济插件！玩家的加入花费被免除！");
				}
				break;
			case "Points":
				if(Data.isPoints){
					cpoints = Integer.valueOf(value);
					if(cpoints>points){
						points=cpoints;
					}
				}else{
					Data.Debug("缺少PlayerPoints插件！玩家的点券花费被免除！");
				}
				break;
			case "Tili":
				if(Data.isTili){
					ctili = Integer.valueOf(value);
					if(ctili>tili){
						tili = ctili;
					}
				}else{
					Data.Debug("缺少CustomGo-Tili插件！玩家的体力花费被免除！");
				}
				
				break;
			case "Items":
				if(value.contains(",")){
					String[] vl = value.split(",");
					for(String s : vl){
						String i = s.split("_")[0];
						int am = Integer.valueOf(s.split("_")[1]);
						
						boolean checked = false;
						for(itemv v : il){
							if(v.name==i){
								checked=true;
								if(v.amount<am){
									v.amount=am;
								}
							}
						}
						cil.add(new itemv(i,am));
						if(!checked){
							il.add(new itemv(i,am));
						}
					}
				}else{
					cil.add(new itemv(value.split("_")[0],Integer.valueOf(value.split("_")[1])));
				}
				break;
			case "Level":
				clevel = Integer.valueOf(value);
				if(clevel>level){
					clevel=level;
				}
				break;
			}
		}else{
			switch(key){
			case "Money":
				money = Integer.valueOf(value);
				break;
			case "Points":
				points = Integer.valueOf(value);
				break;
			case "Tili":
				tili = Integer.valueOf(value);
				break;
			case "Items":
				if(value.contains(",")){
					String[] vl = value.split(",");
					for(String s : vl){
						il.add(new itemv(s.split("_")[0],Integer.valueOf(s.split("_")[1])));
					}
				}else{
					il.add(new itemv(value.split("_")[0],Integer.valueOf(value.split("_")[1])));
				}
				break;
			case "Level":
				level = Integer.valueOf(value);
				break;
			}
		}
			
	}
	
	
	
	public boolean PlayerCheck(Player p){
		if(money>0){
			if(Data.isEco){
				if(Data.economy.getBalance(p)<money){
					p.sendMessage(ChatColor.RED+"您没有足够的金钱！");
					return false;
				}
			}else{
				cmoney = 0;
				Data.Debug("缺少经济插件！玩家的金钱加入花费被免除！");
			}
		}

		if(p.getLevel()<level){
			p.sendMessage(ChatColor.RED+"您没有足够的等级！");
			return false;
		}
		
		if(points>0){
			if(Data.isPoints){
				if(Data.playerPoints.getAPI().look(p.getUniqueId())<points){
					p.sendMessage(ChatColor.RED+"您没有足够的点券！");
					return false;
				}
			}else{
				cpoints = 0;
				Data.Debug("缺少点券插件！玩家的点券加入花费被免除！");
			}
		}
		
		if(tili>0){
			if(Data.isTili){
				if(User.getUser(p).getTili()<tili){
					p.sendMessage(ChatColor.RED+"您没有足够的体力！");
					return false;
				}
			}else{
				ctili = 0;
				Data.Debug("缺少体力插件！玩家的点券加入花费被免除！");
			}
		}
			
		
		
		Inventory inv = p.getInventory();
		for(itemv v : il){
			boolean checked = false;
			for(ItemStack i : inv){
				if(i!=null && i.getType()!=Material.AIR && i.hasItemMeta() && i.getItemMeta().getDisplayName()!=null && i.getItemMeta().getDisplayName().indexOf(v.name)!=-1){
					if(i.getAmount()>=v.amount){
						checked=true;
						break;
					}
				}
			}
			if(!checked){
				p.sendMessage(ChatColor.RED+"您没有所需的物品！");
				return false;
			}
		}
		
		return true;
	}
	
	
	public void PlayerConsume(Player p){
		if(cmoney>0){
			Data.economy.withdrawPlayer(p, cmoney);
		}
		
		if(clevel>0){
			p.setLevel(p.getLevel()-clevel);
		}
		if(cpoints>0){
			Data.playerPoints.getAPI().take(p.getUniqueId(), cpoints);
		}
		if(ctili>0){
			User.getUser(p).takeTili(ctili);
		}
		Inventory inv = p.getInventory();
		for(itemv v : cil){
			for(ItemStack i : inv){
				if(i!=null && i.getItemMeta()!=null && i.getItemMeta().getDisplayName()!=null && i.getItemMeta().getDisplayName().indexOf(v.name)!=-1){
					if(i.getAmount()>=v.amount){
						i.setAmount(i.getAmount()-v.amount);
						if(i.getAmount()==0){
							i.setType(Material.AIR);
						}
						break;
					}
					
				}
			}
		}
	}
	
	public static boolean itemCheck(Player p,String value){
		List<itemv> il = new ArrayList<>();
		if(value.contains(",")){
			String[] vl = value.split(",");
			for(String s : vl){
				il.add(new itemv(s.split("_")[0],Integer.valueOf(s.split("_")[1])));
			}
		}else{
			il.add(new itemv(value.split("_")[0],Integer.valueOf(value.split("_")[1])));
		}
		Inventory inv = p.getInventory();
		for(itemv v : il){
			boolean checked = false;
			for(ItemStack i : inv){
				if(i!=null && i.getType()!=Material.AIR && i.hasItemMeta() && i.getItemMeta().getDisplayName()!=null && i.getItemMeta().getDisplayName().indexOf(v.name)!=-1){
					if(i.getAmount()>=v.amount){
						checked=true;
						break;
					}
				}
			}
			if(!checked){
				p.sendMessage(ChatColor.RED+"您没有所需的物品！");
				return false;
			}
		}
		return true;
	}
	
	public static boolean itemConsume(Player p,String value){
		List<itemv> il = new ArrayList<>();
		if(value.contains(",")){
			String[] vl = value.split(",");
			for(String s : vl){
				il.add(new itemv(s.split("_")[0],Integer.valueOf(s.split("_")[1])));
			}
		}else{
			il.add(new itemv(value.split("_")[0],Integer.valueOf(value.split("_")[1])));
		}
		Inventory inv = p.getInventory();
		for(itemv v : il){
			boolean checked = false;
			for(ItemStack i : inv){
				if(i!=null && i.getType()!=Material.AIR && i.hasItemMeta() && i.getItemMeta().getDisplayName()!=null && i.getItemMeta().getDisplayName().indexOf(v.name)!=-1){
					if(i.getAmount()>=v.amount){
						checked=true;
						break;
					}
				}
			}
			if(!checked){
				p.sendMessage(ChatColor.RED+"您没有所需的物品！");
				return false;
			}
		}
		
		
		for(itemv v : il){
			for(ItemStack i : inv){
				if(i!=null && i.getItemMeta()!=null && i.getItemMeta().getDisplayName()!=null && i.getItemMeta().getDisplayName().indexOf(v.name)!=-1){
					if(i.getAmount()>=v.amount){
						i.setAmount(i.getAmount()-v.amount);
						if(i.getAmount()==0){
							i.setType(Material.AIR);
						}
						break;
					}
					
				}
			}
		}
		return true;
	}
}

class itemv{
	public String name;
	public int amount;
	public itemv(String Name,int Amount){
		name =Name;
		amount = Amount;
	}
}

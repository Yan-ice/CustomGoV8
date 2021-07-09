package fw.group.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fw.Data;
import fw.group.Group;

public class ItemTask implements Listener {
	public static List<ItemTask> l = new ArrayList<>();

	ItemStack Item;
	List<String> Task;
	String Name;
	int Cost;
	boolean isComplete = false;
	boolean consume = false;
	boolean NoMove = false;
	
	public File GetFile() {
		File[] list = Data.itemDir.listFiles();
		for (int a = 0; a < list.length; a++) {
			if (list[a].getName().equals(Name + ".yml")) {
				return list[a];
			}
		}
		return null;
	}

	public static ItemTask getItemTask(String name) {
		for (int a = 0; a < l.size(); a++) {
			if (l.get(a).Name.equals(name)) {
				return l.get(a);
			}
		}
		return null;
	}

	public ItemTask(String Name) {
		this.Name = Name;
	}

	private void setValue(String ID, String ItemName, List<String> ItemLore, List<String> Task) {
		try {
			Item = new ItemStack(Material.getMaterial(ID));
			ItemMeta meta = Item.getItemMeta();
			meta.setDisplayName(ItemName);
			meta.setLore(ItemLore);
			Item.setItemMeta(meta);
			this.Task = Task;
		} catch (NullPointerException e) {
			return;
		}
		isComplete = true;
		Data.fmain.getServer().getPluginManager().registerEvents(this, Data.fmain);
	}

	public void loadFile() {
		try {
			if (GetFile() != null) {
				File f = GetFile();
				FileConfiguration fil = YamlConfiguration.loadConfiguration(f);
				setValue(fil.getString("Id"), Data.ColorChange(fil.getString("ItemName")),
						Data.ColorChange(fil.getStringList("Lore")), fil.getStringList("Task"));
				if(fil.contains("Consume")){
					consume = fil.getBoolean("Consume");
				}
				if(fil.contains("CantMove")){
					NoMove = fil.getBoolean("CantMove");
				}
			} else {
				Data.ConsoleInfo("文件没有找到！");
			}
		} catch (NullPointerException e) {
			return;
		}

	}

	public static void UnLoadAll() {
		while (l.size() > 0) {
			HandlerList.unregisterAll(l.get(0));
			l.remove(0);
		}
	}

	public static void LoadAll(File dir) {
		UnLoadAll();
		File[] list = dir.listFiles();
		List<ItemTask> l = new ArrayList<>();
		if (list.length > 0) {
			for (int a = 0; a < list.length; a++) {
				ItemTask i = new ItemTask(list[a].getName().split("\\.")[0]);
				i.loadFile();
				l.add(i);
			}
			ItemTask.l = l;
		}
	}

	public void Give(Player player,int Slot) {
		if (isComplete) {
			Inventory inv = player.getInventory();
			if(Slot>-1 && Slot<36){
				if(inv.getItem(Slot)!=null && inv.getItem(Slot).getAmount()!=0){
					ItemStack oldi = inv.getItem(Slot);
					inv.setItem(Slot, Item);
					inv.addItem(oldi);
				}else{
					inv.setItem(Slot, Item);
				}
			}else{
				inv.addItem(Item);
			}
			
		} else {
			player.sendMessage(ChatColor.RED+"您无法获得物品" + Name + "。因为它的设置是错误的。");
		}

	}

	@EventHandler
	private void IListen(PlayerInteractEvent evt) {
		try{
			
			if (evt.getItem().getItemMeta().equals(Item.getItemMeta())) {
				if (evt.getPlayer()!=null && Group.SearchPlayerInGroup(evt.getPlayer()) != null) {
					if(consume){
						if(evt.getItem().getAmount()>1){
							evt.getItem().setAmount(evt.getItem().getAmount()-1);
						}else{
							evt.getPlayer().getInventory().clear(evt.getPlayer().getInventory().getHeldItemSlot());
						}
					}
					
					new GroupTaskRunner(Task, Group.SearchPlayerInGroup(evt.getPlayer()),evt.getPlayer(),Group.SearchPlayerInGroup(evt.getPlayer()).byLobby().getPlayerList());

				}
			}
		}catch(NullPointerException e){
			
		}
	}
	
	@EventHandler
	private void DropListen(PlayerDropItemEvent evt) {
		try{
			if (evt.getItemDrop().getItemStack().getItemMeta().getDisplayName().equals(Item.getItemMeta().getDisplayName())) {
				if (NoMove && Group.SearchPlayerInGroup(evt.getPlayer()) != null) {
					evt.setCancelled(true);
				}
			}
		}catch(NullPointerException e){
			
		}
	}
	
	@EventHandler
	private void ClickListen(InventoryClickEvent evt) {
		if(NoMove){
			try{
				if (evt.getCurrentItem().getItemMeta().getDisplayName().equals(Item.getItemMeta().getDisplayName())) {
					if (evt.getWhoClicked()!=null && Group.SearchPlayerInGroup((Player)evt.getWhoClicked()) != null) {
							evt.setCancelled(true);
					}
				}
			}catch(NullPointerException e){
				
			}
			
		}
		
			
	}
}

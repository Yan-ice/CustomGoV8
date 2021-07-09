package fw.frame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.JTextArea;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fw.Data;
import fw.group.Group;
import fw.group.task.GroupTaskRunner;



public class TaskDemoAPI implements Listener{
	
	Group g;
	Player p;
	static Set<TaskDemoAPI> list = new HashSet<>();
	public static void FWTask(Player p) {
		for(TaskDemoAPI d : list) {
			if(d.p.getName().equals(p.getName())) {
				d.exit();
				return;
			}
		}
		if(Group.SearchPlayerInGroup(p)!=null) {
			new TaskDemoAPI(p,Group.SearchPlayerInGroup(p));
		}else {
			p.sendMessage("您不在任何队列中，无法开启模拟器！");
		}
		
	}
	public void exit() {
		p.sendMessage("§b您已退出任务模拟器。如果想要再次进入，请输入§e/fwtask§b。");
		HandlerList.unregisterAll(this);
		list.remove(this);
	}
	public TaskDemoAPI(Player p,Group group) {
		Data.fmain.getServer().getPluginManager().registerEvents(this, Data.fmain);
		this.g = group;
		this.p = p;
		p.sendMessage("--------- §e[CustomGo]任务模拟器§b 正在运行中 ---------");
		p.sendMessage("§b在§e聊天栏§b输入你想要运行的任务§e(不带斜杠)§b，发送即可运行！");
		p.sendMessage("§b如果想退出任务模拟器模式，请在聊天栏输入§eclose§b或§e/fwtask§b。");
		p.sendMessage("§b如果想一次运行多条任务，请以§e /n §b分隔每条任务。");
		p.sendMessage("----------------------------------------------------");
		list.add(this);
	}
	
	public static ItemStack CreateItem(Material mt,int Amount,int Damage,int Data,String Name,List<String> lore){
		ItemStack price = new ItemStack(mt,Amount,(short)Damage,(byte)Data);
		ItemMeta IMeta = price.getItemMeta();
		IMeta.setDisplayName(Name);
		IMeta.setLore(lore);
		price.setItemMeta(IMeta);
		return price;
	}


	void check(){
		
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	void sayEvent(PlayerChatEvent evt) {
		String mes = evt.getMessage();
		evt.setCancelled(true);
		if(mes.startsWith("close")) {
			exit();
		}else {
			List<String> lore = new ArrayList<>();
			if(mes.contains("/n")) {
				String[] x = mes.split("/n");
				for(String p : x) {
					lore.add(p.trim());
				}
			}else {
				lore.add(mes);
			}
			for(String x : lore) {
				p.sendMessage("§7已运行任务 -> §r"+x);
			}
			p.sendMessage("§7==============================");
			new GroupTaskRunner(lore, g, p, g.byLobby().getPlayerList());
		}
		
	}
}

package fw.group.trigger;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fw.Data;
import fw.location.FLocation;

public class WalkOnBlock extends TriggerBase {

	public WalkOnBlock(String Name) {
		super(Name);
		// TODO Auto-generated constructor stub
	}
	boolean Cooldown = false;
	@EventHandler
	void Listen(PlayerMoveEvent evt2) {
		Player pl = evt2.getPlayer();
		if (!Cooldown && byGroup.hasPlayer(pl)) {
			if (Id != null & Id.size()>0) {
				if (Id.get(0) == "0") {
					Strike(pl);
					return;
				} else {
					Location BlockWalk = new Location(pl.getLocation().getWorld(), pl.getLocation().getBlockX(),
							(pl.getLocation().getBlockY() - 1), pl.getLocation().getBlockZ());
					Block Blo = BlockWalk.getBlock();
					for (int a = 0; a < Id.size(); a++) {
						if(Id.get(a).contains(",") || Id.get(a).contains(" ")){
							if (Blo.getLocation().equals((new FLocation(Id.get(a),pl.getWorld()).ToMC(null)))) {
								byGroup.Tags().addTag("BlockName", Blo.getType().name());
								Strike(pl);
								Cooldown = true;
								new BukkitRunnable(){

									@Override
									public void run() {
										Cooldown = false;
									}
									
								}.runTaskLater(Data.fmain, 20);
								return;
							}
						}else{
							try{
								if (Blo.getType().name().equalsIgnoreCase(Id.get(a)) || Blo.getType().getId() == Integer.valueOf(Id.get(a))) {
									byGroup.Tags().addTag("BlockName", Blo.getType().name());
									Strike(pl);
									Cooldown = true;
									new BukkitRunnable(){

										@Override
										public void run() {
											Cooldown = false;
										}
										
									}.runTaskLater(Data.fmain, 20);
									return;
								}
							}catch(NumberFormatException e){
							}
						}
						
					}
				}
			}
		}
	}
}

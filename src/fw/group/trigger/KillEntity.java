package fw.group.trigger;

import java.util.List;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

public class KillEntity extends TriggerBase {

	public KillEntity(String Name) {
		super(Name);
		// TODO Auto-generated constructor stub
	}

	public List<String> EntityName() {
		return Id;
	}

	
	@EventHandler(priority=EventPriority.HIGHEST)
	void KillEntityListen(EntityDeathEvent evt) {
		if (evt.getEntity() instanceof LivingEntity && !(evt.getEntity() instanceof ArmorStand)) {

			LivingEntity damaged = evt.getEntity();
			Player killer = damaged.getKiller();
			if(killer!=null && byGroup.hasPlayer(killer)){
				for (String ids : Id) {
					if (ids.equals("all")) {
						Strike(killer);
						return;
					}
				}
				
				if (damaged.getCustomName() != null) {
					for (String ids : Id) {
						if (damaged.getCustomName().indexOf(ids) != -1) {
							Strike(killer);
							return;
						}
					}
				}else
				if(damaged.getName()!=null){
					for (String ids : Id) {
						if (damaged.getName().indexOf(ids) != -1) {
							Strike(killer);
							return;
						}
					}
				}
				if (damaged.getUniqueId().toString() != null) {
					for (String ids : Id) {
						if (damaged.getUniqueId().toString().indexOf(ids) != -1) {
							Strike(killer);
							return;
						}
					}
				}
			}
		}
	}
}

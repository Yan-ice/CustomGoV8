package fw.group.trigger;

import java.util.List;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

public class KillAllEntity extends TriggerBase {

	public KillAllEntity(String Name) {
		super(Name);
		// TODO Auto-generated constructor stub
	}

	public List<String> EntityName() {
		return Id;
	}

	LivingEntity target;
	@EventHandler(priority=EventPriority.HIGHEST)
	void KillEntityListen(EntityDeathEvent evt) {
		if (evt.getEntity() instanceof LivingEntity && !(evt.getEntity() instanceof ArmorStand)) {
			List<Entity> pl = evt.getEntity().getNearbyEntities(150, 100, 150);
			boolean acc = false;
				if(Id!=null){
					for (String ids : Id) {
						if (evt.getEntity().getCustomName()!=null && evt.getEntity().getCustomName().indexOf(ids) != -1) {
							acc = true;
							target = evt.getEntity();
							break;
						}
						if (evt.getEntity().getUniqueId().toString().indexOf(ids) != -1) {
							acc=true;
							target = evt.getEntity();
							break;
						}
						if(ids.equals("all")) {
							acc = true;
							target = evt.getEntity();
						}
					}
				}else{
					acc = true;
					target = evt.getEntity();
				}
			
			if(!acc){
				return;
			}
			
			boolean dir = true;
			boolean hasp = false;
			for(Entity en : pl){					
				if(en instanceof LivingEntity && !(evt.getEntity() instanceof ArmorStand) && !en.equals(target) && !en.isDead()){
					LivingEntity cr = (LivingEntity)en;
					for (String ids : Id) {
						if (ids.equals("all")) {
							dir = false;
						}
					}
					
					if (cr.getCustomName() != null) {
						for (String ids : Id) {
							if (cr.getCustomName().indexOf(ids) != -1) {
								dir = false;
							}
						}
					}
					if (cr.getUniqueId().toString() != null) {
						for (String ids : Id) {
							if (cr.getUniqueId().toString().indexOf(ids) != -1) {
								dir = false;
							}
						}
					}
					if(en instanceof Player){
						if(byGroup.hasPlayer((Player)en)){
							hasp = true;
						}
					}
				}
			}
			if(dir && hasp){
				Strike(null);
			}
		}
	}

}

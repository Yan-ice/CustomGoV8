package fw.location;

import org.bukkit.Location;
import org.bukkit.World;
/**
 * 为了方便进行坐标获取与传送，
 * 作为FLocation的升级,区域指定。
 *
 */
public class FArena {
	FLocation LocA;
	FLocation LocB;
	public FArena(String Arena,World def){
		String[] v = Arena.split(" ");
		if(v.length<=2){
			v = Arena.split(",");
		}
		try{
			String world = "";
			if(v.length>=7){
				world = v[6];
			}
			LocA = new FLocation(v[0]+" "+v[1]+" "+v[2]+" "+world,def);
			LocB = new FLocation(v[3]+" "+v[4]+" "+v[5]+" "+world,def);
		}catch(ArrayIndexOutOfBoundsException e){
		}
	}
	
	public FArena(FLocation LocA,FLocation LocB) {
		this.LocA = LocA;
		this.LocB = LocB;
	}
	
	public FArena(){
	}
	
	/**
	 * 将坐标保存为一个X1,Y1,Z1,X2,Y2,Z2,World 格式的字符串。
	 * @return 保存好的字符串
	 */
	public String toString(){
		String[] n = LocA.ToString().split(" ");
		return n[0]+" "+n[1]+" "+n[2]+" "+LocB.ToString();
	}
	
	/**
	 * 用Minecraft的坐标来构造它。
	 * 
	 * @param LocA 坐标A
	 * @param LocB 坐标B        
	 */
	public FArena(Location LocA,Location LocB) {
		this.LocA = new FLocation(LocA);
		this.LocB = new FLocation(LocB);
	}

	/**
	 * 获得区域所处世界。
	 * @return 保存好的字符串
	 */
	public World getWorld() {
		return LocA.getWorld();
	}


	/**
	 * 检查一个坐标是否在区域中。
	 * @param Loc 需要检查的坐标
	 * @return 检查结果
	 */
	public boolean inArea(FLocation Loc) {
		if(LocA!=null){
			if(Dis(Loc.getX(),LocA.getX(),LocB.getX()) &&
					Dis(Loc.getY(),LocA.getY(),LocB.getY()) &&
					Dis(Loc.getZ(),LocA.getZ(),LocB.getZ())
				){
					return true;
				}
		}else{
			return false;
		}
		return false;
	}


	/** 
	 * 检查坐标是否完整。
	 * @return 检查结果
	 */
	public boolean isComplete() {
		if (LocA.isComplete() && LocB.isComplete()) {
			return true;
		}
		
		return false;
	}
	
	
	private boolean Dis(double T,double d,double e){
		if(d>e){
			return T<=d && T>=e;
		}else{
			return T<=e && T>=d;
		}
	}
}

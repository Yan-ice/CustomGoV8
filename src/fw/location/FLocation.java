package fw.location;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
/**
 * 为了方便进行坐标获取与传送，
 * 作为Location的代理类。
 *
 */
public class FLocation {
	double x;
	double y;
	double z;
	float yaw = 0;
	float pitch = 0;
	World world;
	String worldName="";
	boolean none = false;

	public FLocation(double x, double y, double z, World world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}

	public FLocation(double x, double y, double z,float yaw,float pitch, String world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.world = Bukkit.getWorld(world);
	}

	/**
	 * 直接将一个固定格式的字符串读取为坐标。
	 * 
	 * @param Location
	 *            一个按照 X,Y,Z,World 格式的字符串
	 *            或者X,Y,Z,Yaw,Pitch,World格式字符串
	 */
	public FLocation(String Location,World def) {
		if(Location.equals("none")){
			none = true;
			return;
		}
		try{
			String[] LocationS = Location.split(" ");
			if(LocationS.length<=2){
				LocationS = Location.split(",");
			}
			if (LocationS.length == 4) {
				x = Double.valueOf(LocationS[0]);
				y = Double.valueOf(LocationS[1]);
				z = Double.valueOf(LocationS[2]);
				worldName=LocationS[3];
				if(worldName!="none"){
					this.world = Bukkit.getWorld(worldName);
					if(this.world==null) {
						this.world = def;
					}
				}else {
					this.world = def;
				}
				
			}else if(LocationS.length == 6){
				x = Double.valueOf(LocationS[0]);
				y = Double.valueOf(LocationS[1]);
				z = Double.valueOf(LocationS[2]);
				yaw = Float.valueOf(LocationS[3]);
				pitch = Float.valueOf(LocationS[4]);
				worldName=LocationS[5];
				if(worldName!="none"){
					this.world = Bukkit.getWorld(worldName);
				}
			}
		}catch(NumberFormatException e){
		}

	}

	public boolean isNone(){
		return none;
	}
	/**
	 * 获取玩家坐标来构造它。
	 * 
	 * @param Pl
	 *            需要获取坐标的玩家
	 */
	public FLocation(Player Pl) {
		if(Pl==null){
			none = true;
			return;
		}
		World world = Pl.getWorld();
		double X = Pl.getLocation().getX();
		double Y = Pl.getLocation().getY();
		double Z = Pl.getLocation().getZ();
		X = (double) ((int) (X * 100)) / 100;
		Z = (double) ((int) (Z * 100)) / 100;
		x = X;
		y = Y;
		z = Z;
		this.world = world;
	}

	/**
	 * 获得Minecraft的坐标来构造它。
	 * 
	 * @param Loc
	 *            需要获取的坐标
	 */
	public FLocation(Location Loc) {
		if(Loc==null){
			none = true;
			return;
		}
		double X = Loc.getX();
		double Y = Loc.getY();
		double Z = Loc.getZ();
		X = (double) ((int) (X * 100)) / 100;
		Z = (double) ((int) (Z * 100)) / 100;
		x = X;
		y = Y;
		z = Z;
		this.world = Loc.getWorld();
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public World getWorld() {
		return world;
	}

	public void setLoc(double x, double y, double z, World world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}


	
	public Location ToMC(Player p) {
		if(none){
			return null;
		}
		if(p!=null && (worldName.equals("none")||worldName==""||world==null)){
			world = p.getWorld();
			worldName = p.getWorld().getName();
		}
		if(world==null){
			loadWorld();
		}
		if(world==null){
			return null;
		}
		Location loc = new Location(world, x, y, z);
		if(yaw!=0 && pitch!=0){
			loc.setYaw(yaw);
			loc.setPitch(pitch);
		}
		
		return loc;
	}
	/**
	 * 将坐标保存为一个X,Y,Z,World 格式的字符串。
	 * @return 保存好的字符串
	 */
	public String ToString() {
		if(isComplete()){
			return x+" "+y+" "+z+" "+worldName;
		}else{
			return "none";
		}

	}
	/**
	 * 在坐标列表中随机选择一个坐标返回。
	 * @param Loc 坐标列表
	 * @return 天选坐标~
	 */
	public static FLocation RandomLoc(List<FLocation> Loc) {
		Random random = new Random();
		int a = random.nextInt(Loc.size());
		return Loc.get(a);
	}

	/**
	 * 检查坐标是否完整。
	 * @return 检查结果
	 */
	public boolean isComplete() {
		if(none){
			return false;
		}
		if (world != null || worldName!=null) {
			return true;
		}
		
		return false;
	}
	
	public boolean equals(FLocation loc){
		if(loc!=null && !loc.isNone()){
			return (int)loc.getX()==(int)x && (int)loc.getY()==(int)y && (int)loc.getZ()==(int)z && world==loc.getWorld();
		}
		return false;
	}
	
	private void loadWorld(){
		if(worldName!=""){
			world=Bukkit.getWorld(worldName);
		}
	}
}

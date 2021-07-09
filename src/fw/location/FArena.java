package fw.location;

import org.bukkit.Location;
import org.bukkit.World;
/**
 * Ϊ�˷�����������ȡ�봫�ͣ�
 * ��ΪFLocation������,����ָ����
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
	 * �����걣��Ϊһ��X1,Y1,Z1,X2,Y2,Z2,World ��ʽ���ַ�����
	 * @return ����õ��ַ���
	 */
	public String toString(){
		String[] n = LocA.ToString().split(" ");
		return n[0]+" "+n[1]+" "+n[2]+" "+LocB.ToString();
	}
	
	/**
	 * ��Minecraft����������������
	 * 
	 * @param LocA ����A
	 * @param LocB ����B        
	 */
	public FArena(Location LocA,Location LocB) {
		this.LocA = new FLocation(LocA);
		this.LocB = new FLocation(LocB);
	}

	/**
	 * ��������������硣
	 * @return ����õ��ַ���
	 */
	public World getWorld() {
		return LocA.getWorld();
	}


	/**
	 * ���һ�������Ƿ��������С�
	 * @param Loc ��Ҫ��������
	 * @return �����
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
	 * ��������Ƿ�������
	 * @return �����
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

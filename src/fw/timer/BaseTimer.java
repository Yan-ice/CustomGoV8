package fw.timer;

import org.bukkit.scheduler.BukkitRunnable;

import fw.Data;

public abstract class BaseTimer extends BukkitRunnable {
	protected int Maxtime;
	protected int time;
	private boolean isRunning = false;

	public int getTime() {
		return time;
	}
	public int getMaxtime(){
		return Maxtime;
	}
	/**
	 * ������ʱ����
	 */
	public void Start() {
		if (!isRunning) {
			isRunning = true;
			this.runTaskTimer(Data.fmain, 2, 20);
		}
	}

	public void setMaxTime(int maxtime) {
		time = maxtime;
		Maxtime = maxtime;
	}
/**
 * ����ʹ���������������ʱ����
 * ���ΪStart()����!
 */
	@Override
	public void run() {
		EveryTick();
		if (ShutDown()) {
			whenShutDown();
			Stop();
			return;
		}
		if (time <= 0) {
			TimeUp();
			Stop();
			return;
		}
		time--;
	}

	/**
	 * ÿ��ʱһ�룬����ִ��һ�θ÷��������Ƿ������ʱ��
	 * 
	 * @return ���Ϊtrue,��ֱ��ֹͣ��ʱ��
	 */
	public abstract boolean ShutDown();

	/**
	 * ����ΪShutDownʹ��ʱ��ֹͣʱ��ִ�е����
	 */
	public abstract void whenShutDown();

	public abstract void EveryTick();

	/**
	 * ��ʱ�䵽��ʱִ�еķ�����
	 */
	public abstract void TimeUp();
	
	public void Stop(){
		if(isRunning()){
			cancel();
			isRunning = false;
			return;
		}
	}
	
	
	public boolean isRunning(){
		return isRunning;
	}
}

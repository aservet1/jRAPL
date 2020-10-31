
public interface AsyncEnergyManager {
	
	public abstract void init();

	public abstract void cleanup();

	public abstract void start();
	
	public abstract void stop();
	
	public abstract void reInit();

}

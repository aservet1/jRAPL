
package jrapl;

public abstract class AsyncEnergyMonitor extends EnergyMonitor {

	public abstract void start();

	public abstract void stop();

	public abstract String toString();

	public abstract void reset();

	public abstract void writeToFile(String fileName);

}

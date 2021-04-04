
package jrapl;

public interface AsyncMonitor
{
	public abstract void start();
	public abstract void stop();
	public abstract String toString();
	public abstract void reset();
}

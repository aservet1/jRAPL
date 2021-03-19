package threadtimer;

public class ThreadCompare
{
	public static void main(String[] args) throws InterruptedException
	{
		if (args.length == 0) {
			System.out.println("usage: java thread_timer_compare.ThreadCompare <time to keep thread alive> [J]\n"
				+ " -- J gives you javaSide, anything else or nothing gives you cSide");
			System.exit(2);
		}

		System.load(System.getProperty("user.dir")+"/libthreadie.so");

		int n = Integer.parseInt(args[0]);
		if (args.length >= 1 && args[1].equals("J")) {
			JavaSide j = new JavaSide();
			j.start();
			Thread.sleep(n);
			j.stop();
		} else {
			CSide.start();
			Thread.sleep(n);
			CSide.stop();
		}
	}
}

class CSide {
	public native static void start();
	public native static void stop();
}

class JavaSide implements Runnable {
	boolean exitflag = true;
	int count = 0;
	Thread t = null;

	public void start() {
		t = new Thread(this);
		t.start();
	}

	public void stop() {
		exitflag = true;
		try {
			t.join();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		t = null;
	}

	public void run() {
		exitflag = false;
		while (!exitflag) {
			try {
				Thread.sleep(0);
				count++;
			} catch (InterruptedException ex) {
				System.out.println("hey that thing happened. yikes! :)");
			}
		}
		System.out.printf("count: %d\n", count);
	}

}

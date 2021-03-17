package threadiecomp;

public class ThreadCompare
{
	public native static void start();
	public native static void stop();

	public static void main(String[] args) throws InterruptedException
	{
		System.load(System.getProperty("user.dir")+"/libthreadie.so");

		int n = Integer.parseInt(args[0]);
		if (args[1].equals("J")) {
			JavaSide j = new JavaMirror();
			j.start();
			Thread.sleep(n);
			j.stop();
		} else {
			start();
			Thread.sleep(n);
			stop();
		}
	}
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
				Thread.sleep(10);
				count++;
			} catch (InterruptedException ex) {
				System.out.println("hey that thing happened. yikes! :)");
			}
		}
		System.out.printf("count: %d\n", count);
	}

}

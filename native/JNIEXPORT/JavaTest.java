
package jRAPL;

class EnergyManager { // Prologue & Epilogue
	native static void profileInit();
	native static void profileDealloc();
}

class EnergyMonitor {
	native static String energyStatCheck(int socket);
}

public class JavaTest
{
	private native static void nativeSandbox();

	static {
		System.out.println("hello1");
		System.load("/home/alejandro/jRAPL/native/JNIEXPORT/libJNIRAPL.so");
		System.out.println("hello2");
	}

	public static void main(String[] args)
	{
		nativeSandbox();

		System.out.println("hello3");
		final int ALL_SOCKETS = 0;
		System.out.println("hello4");
		EnergyManager.profileInit();
		System.out.println("hello5");
		String energy = EnergyMonitor.energyStatCheck(ALL_SOCKETS);
		System.out.println("hello6");
		EnergyManager.profileDealloc();
		System.out.println("hello7");
		System.out.println(energy);
		System.out.println("hello8");
	}
}

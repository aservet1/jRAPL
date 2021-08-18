package jRAPL;

public class EnergyManager
{
	private boolean active = false;

	public void activate() {
		if (active) {
			System.err.println(
				"Error: "
				+ getClass().getName()
				+ "@"
				+ Integer.toHexString(hashCode())
				+ " already activated."
				+ " Double activate is not allowed. Exiting program."
			);
			System.exit(1);
		}

		active = true;
		NativeAccess.subscribe();
	}

	public void deactivate() {
		if (!active) {
			System.err.println(
				"Error: "
				+ getClass().getName()
				+ "@"
				+ Integer.toHexString(hashCode())
				+ " already deactivated."
				+ " Double deactivate is not allowed. Exiting program."
			);
			System.exit(1);
		}

		active = false;
        NativeAccess.unsubscribe();
	}
}

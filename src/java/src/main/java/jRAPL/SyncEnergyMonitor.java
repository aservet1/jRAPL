
package jRAPL;

public class SyncEnergyMonitor extends EnergyMonitor {

	@Override
	public void activate() {
		super.activate();
	}

	@Override
	public void deactivate() {
		super.deactivate();
	}

	public EnergyStats getSample() {
		return stringToEnergyStats (
			NativeAccess.energyStatCheck()
		);
	}

	public double[] getPrimitiveSample() {
		return statStringToPrimitiveSample (
			NativeAccess.energyStatCheck()
		);
	}

}

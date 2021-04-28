
// import jRAPL.EnergyStats;
// import jRAPL.EnergyDiff;
import jRAPL.*;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

public class ObjectMemory
{
	private static class DoubleArrayWrapped {
		double[] d;
	}

    public static void main(String[] args) {
        System.out.println(VM.current().details());
		System.out.println("-------------------------------------------------------");
        System.out.print(ClassLayout.parseClass(double[].class).toPrintable());
		System.out.println("-------------------------------------------------------");
        System.out.print(ClassLayout.parseClass(DoubleArrayWrapped.class).toPrintable());
		System.out.println("-------------------------------------------------------");
		System.out.print(ClassLayout.parseClass(EnergyStats.class).toPrintable());
		System.out.println("-------------------------------------------------------");
		System.out.print(ClassLayout.parseClass(EnergyDiff.class).toPrintable());
		System.out.println("-------------------------------------------------------");
		System.out.print(ClassLayout.parseClass(EnergySample.class).toPrintable());
		System.out.println("-------------------------------------------------------");
    }
}

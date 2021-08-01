import java.util.Arrays;
import jRAPL.EnergyStats;
import jRAPL.ArchSpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
public class ByteArrParse
{
	static final int SIZEOF_FLOAT = 4;
	static final int SIZEOF_LONG = 8;
	static final int BITS_PER_BYTE = 8;

	private static EnergyStats parseByteArray(byte[] bytes) {
		double[] energy = new double[ArchSpec.NUM_SOCKETS * ArchSpec.NUM_STATS_PER_SOCKET];
		int lo = 0, hi = SIZEOF_FLOAT;
		for (int z = 0; z < energy.length; z++) {
			// energy[z] = (double)parseFloat( Arrays.copyOfRange(bytes, lo, hi) );
			lo += SIZEOF_FLOAT; hi += SIZEOF_FLOAT;
		}
		long timestamp = parseLong( Arrays.copyOfRange(bytes, lo, hi) );
		return new EnergyStats( energy, usecToInstant(timestamp) );
	}

	private static Instant usecToInstant(long usec) {
		return Instant.EPOCH.plus(usec, ChronoUnit.MICROS);
	}
	// mega DRY here :(
	private static long parseLong(byte[] bytes) {
		assert(bytes.length == SIZEOF_LONG);
		printBinString(bytes);
		int offset = 0; long parsed = 0;
		for (int i = bytes.length-1; i >= 0; i--) {// maybe use the 'i' to control iteration through, and also control shifting, as opposed to 'offset' controlling shifting
			long mask = bytes[i] << (offset*BITS_PER_BYTE-1);
			parsed |= (mask);
			System.out.println(" === ");
			System.out.println(" bytes[i])\t"+Integer.toHexString(bytes[i]));
			System.out.println(" mask)\t\t"+Long.toBinaryString(mask));
			System.out.println(" parsed)\t"+Long.toBinaryString(parsed));
			System.out.println(" offset)\t"+offset);
			offset++;
		}
		return parsed;
	}

	private static void printBinString(byte[] bytes) {
		for (byte b : bytes) {
			String s = Integer.toBinaryString(b);
			String zeroes = new String();
			for (int i = 0; i < (8-s.length()); i++) {
				zeroes += "0";
			}
			System.out.printf(" %s%s",zeroes,s);
		}
		System.out.println();
	}

	public static void main(String[] args) {
		byte[] longarr = new byte[]{0x00,0x00,0x00,0x00,0x70,0x00,0x00,0x01,0x11};
		System.out.println(parseLong(longarr));

		// byte[] bytes = new byte[]{ 
		// (byte)0x3f, (byte)0xc0, (byte)0x00, (byte)0x00, (byte)0x40, (byte)0x2c, (byte)0xcc, (byte)0xcd,
		// 
		//	(byte)0x40, (byte)0x59, (byte)0x99, (byte)0x9a,
		// 
		//	(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x12, (byte)0xDB, (byte)0x95
		// };
		// System.out.println( parseByteArray(bytes).csv() );
	}
}

package ar.com.pabloferraris.mutants.persistence;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public final class DnaHashHelper {

	private static final Map<Character, Byte> charToByte;

	static {
		charToByte = new HashMap<Character, Byte>();
		charToByte.put('A', (byte)0b00);
		charToByte.put('C', (byte)0b01);
		charToByte.put('G', (byte)0b10);
		charToByte.put('T', (byte)0b11);
	}

	public static String getHash(String[] dna) {
		int m = dna.length;
		int n = dna[0].length();
		int size = (int) Math.ceil(((m * n) * 2) / 8f);
		byte[] rawData = new byte[size];
		int item = 0;
		for (int y = 0; y < m; y++)
			for (int x = 0; x < n; x++) {
				byte value = charToByte.get(dna[y].charAt(x));
				int i = (int) Math.floor(item / 4f);
				int j = (item % 4) * 2;
				rawData[i] |= (byte) (value << j);
				item++;
			}
		return Base64.getEncoder().encodeToString(rawData);
	}
}

package ar.com.pabloferraris.mutants.detection.nitrogenousBases;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Discover sequences of equals elements in a matrix, unraveling first the
 * horizontal, vertical and oblique lines. Then iterate over the list of strings
 * obtained.
 * 
 * @author Pablo Ferraris
 */
public class UnravelNitrogenousBasesDetectionStrategy extends NitrogenousBasesDetectionStrategy {

	@Override
	public boolean detect(String[] dna) {
		int count = 0;
		Iterable<String> lines = unravel(dna, getSequenceSize());
		for (String line : lines) {
			int start = 0;
			while (start <= line.length() - getSequenceSize()) {
				int end = start;
				int sum = 1;
				while (++end < line.length()) {
					if (line.charAt(start) == line.charAt(end)) {
						sum++;
					} else {
						break;
					}
				}
				if (sum >= getSequenceSize()) {
					if (++count >= getSequenceCount()) {
						return true;
					}
				}
				start = end;
			}
		}
		return false;
	}

	/**
	 * Converts a character matrix into a list of vertical, horizontal & obliquous
	 * lines
	 * 
	 * @param source  is the character matrix to scan
	 * @param minSize is the minimum size of each line. Lines of smaller size will
	 *                be discarded
	 * @return String list with each of vertical, horizontal & obliquous lines in
	 *         the matrix
	 */
	private List<String> unravel(String[] source, int minSize) {
		List<String> result = new ArrayList<String>();
		int n = source.length;
		int m = source[0].length();

		// Horizontal lines
		if (m >= minSize) {
			for (String line : source) {
				result.add(line);
			}
		}

		// Vertical lines
		if (n >= minSize) {
			for (int column = 0; column < m; column++) {
				final int columnAux = column;
				result.add(Stream.of(source).map(x -> x.charAt(columnAux)).map(String::valueOf)
						.collect(Collectors.joining()));
			}
		}

		String value = "";

		// Oblique lines starting in column 0
		for (int row = 0; row < m; row++) {
			if (row <= n - minSize) {
				value = "";
				for (int val = 0; val < Math.min(n - row, m); val++) {
					value += source[row + val].charAt(val);
				}
				result.add(value);
			}
			if (row >= minSize - 1) {
				value = "";
				for (int val = 0; val < Math.min(row + 1, m); val++) {
					value += source[row - val].charAt(val);
				}
				result.add(value);
			}
		}

		// Oblique lines starting in rows 0 & last one
		if (m - minSize > 0) {
			for (int column = 1; column <= (m - minSize); column++) {
				int count = source[0].length() - column;
				value = "";
				for (int val = 0; val < count; val++) {
					value += source[val].charAt(column + val);
				}
				result.add(value);
				value = "";
				for (int val = 0; val < count; val++) {
					value += source[source.length - (val + 1)].charAt(column + val);
				}
				result.add(value);
			}
		}

		return result;
	}
}

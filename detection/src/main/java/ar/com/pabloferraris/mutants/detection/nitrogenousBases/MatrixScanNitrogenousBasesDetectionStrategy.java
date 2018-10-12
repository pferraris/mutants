package ar.com.pabloferraris.mutants.detection.nitrogenousBases;

import java.util.Map;
import java.util.HashMap;

/**
 * Discover sequences of equals elements in a matrix, scanning it from left to
 * right and from top to bottom, looking for sequences in 4 directions which
 * ensures a total sweep.
 * 
 * @author Pablo Ferraris
 */
public class MatrixScanNitrogenousBasesDetectionStrategy extends NitrogenousBasesDetectionStrategy {

	private static Point[] directions = { new Point(0, 1), new Point(1, 1), new Point(1, 0), new Point(1, -1) };
	
	@Override
	public boolean detect(String[] dna) {
		return new Scanner(dna).getResult();
	}

	private class Scanner {
		private CheckedItems checked = new CheckedItems();
		private String[] dna;

		public Scanner(String[] dna) {
			this.dna = dna;
		}

		public boolean getResult() {
			int count = 0;
			for (int y = 0; y < dna.length; y++)
				for (int x = 0; x < dna[y].length(); x++)
					for (Point direction : directions)
						if (scan(y, x, direction, 1) >= getSequenceSize())
							if (++count >= getSequenceCount())
								return true;
			return false;
		}

		private int scan(int y, int x, Point direction, int size) {
			if (checked.contains(y, x, direction)) {
				return 0;
			}
			Point next = new Point(y + direction.getY(), x + direction.getX());
			if (next.getY() >= 0 && next.getY() < dna.length)
				if (next.getX() >= 0 && next.getX() < dna[next.getY()].length())
					if (dna[next.getY()].charAt(next.getX()) == dna[y].charAt(x))
						return scan(next.getY(), next.getX(), direction, size + 1);
			return size;
		}
	}

	private class CheckedItems {

		private Map<Integer, Map<Integer, Map<Point, Boolean>>> checked;

		public CheckedItems() {
			checked = new HashMap<Integer, Map<Integer, Map<Point, Boolean>>>();
		}

		public boolean contains(int y, int x, Point direction) {
			if (!checked.containsKey(y)) {
				checked.put(y, new HashMap<Integer, Map<Point, Boolean>>());
			}
			if (!checked.get(y).containsKey(x)) {
				checked.get(y).put(x, new HashMap<Point, Boolean>());
			}
			if (!checked.get(y).get(x).containsKey(direction)) {
				checked.get(y).get(x).put(direction, true);
				return false;
			}
			return true;
		}
	}

	private static class Point {
		private int y;
		private int x;

		public Point(int y, int x) {
			this.y = y;
			this.x = x;
		}

		/**
		 * @return the y
		 */
		private int getY() {
			return y;
		}

		/**
		 * @return the x
		 */
		private int getX() {
			return x;
		}
	}
}

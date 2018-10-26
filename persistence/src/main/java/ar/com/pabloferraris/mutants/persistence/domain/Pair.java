package ar.com.pabloferraris.mutants.persistence.domain;

/**
 * Represents a key and value pair
 * @author Pablo Ferraris
 *
 * @param <K> is the key type
 * @param <V>  is the value type
 */
public class Pair<K, V> {
	private final K key;
	private final V value;

	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return key;
	}
	
	public V getValue() {
		return value;
	}
}

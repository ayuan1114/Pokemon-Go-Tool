package pokemon;
public class Pair<K, V> {
    public K key;
    public V val;
    public Pair() {
        this.key = null;
        this.val = null;
    }
    public Pair(K key, V val) {
        this.key = key;
        this.val = val;
    }
}

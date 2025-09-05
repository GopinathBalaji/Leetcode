// Design Philosophy: "Synchronization of Two Data Structures"
/*
ArrayList<Integer> arr holds the values.
HashMap<Integer, Integer> pos maps a value → its index in arr.
Insert: if absent, pos.put(val, arr.size()) then arr.add(val).
Remove: swap arr[idx] with arr[last], update pos for the moved value, pop last, remove val from pos.
getRandom: pick a random index in [0, arr.size()) and return arr.get(idx).

Math.random() is in java.lang (import-free). It returns a double in [0.0, 1.0). Multiply by the list size and floor to get a uniform index.
*/
class RandomizedSet {
    private final Map<Integer, Integer> pos = new HashMap<>();
    private final List<Integer> arr = new ArrayList<>();

    public boolean insert(int val) {
        if (pos.containsKey(val)) return false;
        pos.put(val, arr.size());
        arr.add(val);
        return true;
    }

    public boolean remove(int val) {
        Integer idxObj = pos.get(val);
        if (idxObj == null) return false;
        int idx = idxObj, lastIdx = arr.size() - 1, lastVal = arr.get(lastIdx);

        // move last into idx
        arr.set(idx, lastVal);
        pos.put(lastVal, idx);

        // pop last & erase val
        arr.remove(lastIdx);
        pos.remove(val);
        return true;
    }

    public int getRandom() {
        int i = (int)(Math.random() * arr.size()); // 0..size-1
        return arr.get(i);
    }
}


/**
 * Your RandomizedSet object will be instantiated and called as such:
 * RandomizedSet obj = new RandomizedSet();
 * boolean param_1 = obj.insert(val);
 * boolean param_2 = obj.remove(val);
 * int param_3 = obj.getRandom();
 */
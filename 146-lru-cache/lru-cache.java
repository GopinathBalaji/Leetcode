class LRUCache {
    public LinkedList<Integer> list;
    public int size;
    public Map<Integer, Integer> map;

    public LRUCache(int capacity) {
        this.list = new LinkedList<>();
        this.size = capacity;
        this.map = new HashMap<>();

    }
    
    public int get(int key) {
        if(!map.containsKey(key)){
            return -1;
        }
        list.remove((Integer) key);
        list.addFirst(key);

        return map.get(key);
    }
    
    public void put(int key, int value) {
        if(map.containsKey(key)){
            map.put(key, value);
            list.remove((Integer) key);
            list.addFirst(key);
        }else{
            if(map.size() >= size){
                int lruKey = list.removeLast();
                map.remove(lruKey);
            }
            list.addFirst(key);
            map.put(key, value);
        }
    }
}

/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache obj = new LRUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */
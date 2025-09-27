// Using Doubly Linked List and HashMap
/*
Why this is O(1):
get: HashMap lookup + remove + addFirst → constant pointer updates.
put: Map ops + at most one removeLast (constant) + pointer updates.

## Step-by-step example

Capacity = 2
Ops: `put(1,1)`, `put(2,2)`, `get(1)`, `put(3,3)`, `get(2)`, `put(4,4)`, `get(1)`, `get(3)`, `get(4)`

We maintain DLL **head ⇄ ... ⇄ tail**, MRU near `head`, LRU near `tail`.

1. `put(1,1)`
   List: `head ⇄ [1] ⇄ tail`
   Map: {1}

2. `put(2,2)`
   Add at front: `head ⇄ [2] ⇄ [1] ⇄ tail`
   Map: {1,2}

3. `get(1)` → hit = 1
   Move 1 to front: `head ⇄ [1] ⇄ [2] ⇄ tail`
   Return 1.

4. `put(3,3)` → cap exceeded → evict LRU (tail.prev = 2)
   Remove 2; add 3 at front: `head ⇄ [3] ⇄ [1] ⇄ tail`
   Map: {1,3}

5. `get(2)` → miss → `-1`

6. `put(4,4)` → evict LRU (now 1)
   `head ⇄ [4] ⇄ [3] ⇄ tail`
   Map: {3,4}

7. `get(1)` → `-1`

8. `get(3)` → hit; move to front: `head ⇄ [3] ⇄ [4] ⇄ tail` → return 3

9. `get(4)` → hit; move to front: `head ⇄ [4] ⇄ [3] ⇄ tail` → return 4

Everything stays O(1) and consistent.


*/

class LRUCache {
    // ----- DLL node -----
    private static class Node {
        int key, value;
        Node prev, next;
        Node() {}
        Node(int k, int v) { key = k; value = v; }
    }

    // ----- state -----
    private final int cap;
    private final HashMap<Integer, Node> map;
    private final Node head; // dummy head (MRU side)
    private final Node tail; // dummy tail (LRU side)

    // ----- constructor -----
    public LRUCache(int capacity) {
        cap = capacity;
        map = new HashMap<>();
        head = new Node();
        tail = new Node();
        head.next = tail;
        tail.prev = head;
    }

    // ----- public API -----
    public int get(int key) {
        Node n = map.get(key);
        if (n == null) return -1;
        remove(n);
        addFirst(n);
        return n.value;
    }

    public void put(int key, int value) {
        if (cap == 0) return; // edge safety

        Node n = map.get(key);
        if (n != null) {
            n.value = value;
            remove(n);
            addFirst(n);
            return;
        }

        // new key
        Node node = new Node(key, value);
        map.put(key, node);
        addFirst(node);

        if (map.size() > cap) {
            Node lru = removeLast();
            map.remove(lru.key);
        }
    }

    // ----- DLL helpers (all O(1)) -----
    private void addFirst(Node n) {
        n.prev = head;
        n.next = head.next;
        head.next.prev = n;
        head.next = n;
    }

    private void remove(Node n) {
        Node p = n.prev;
        Node q = n.next;
        // n is always a real node (not sentinel), so p and q are non-null
        p.next = q;
        q.prev = p;
        // optional hygiene:
        n.prev = n.next = null;
    }

    private Node removeLast() {
        // LRU is just before tail
        Node lru = tail.prev;
        if (lru == head) return head; // empty list guard (shouldn’t happen)
        remove(lru);
        return lru;
    }
}


/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache obj = new LRUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */
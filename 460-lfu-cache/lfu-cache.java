// Method 1: 2 layer bookkeeping A) key to node map, B) freq to DoublyLinkedList map
/*
######## HINTS ############

* Requirements to keep in mind: `get` and `put` must be **O(1) average**, and eviction is **LFU** (lowest frequency). If ties, evict **LRU among that frequency**.

* Think in two layers of bookkeeping:

  **(A) Key layer:** `key -> (value, freq, node)`
  Use a HashMap so you never search.

  **(B) Frequency layer:** `freq -> ordered keys by recency`
  Each frequency has its own “LRU list” (usually a doubly linked list). That lets you evict the least-recently-used within the min frequency in O(1).

* Maintain a global `minFreq`:

  * Tracks the smallest frequency currently present in the cache
  * On eviction, you always evict from the list for `minFreq`

* Core operations you need (all O(1)):

  1. **touch(key)** (called by `get` and when updating existing key in `put`)

     * remove node from its current freq list
     * increment `freq`
     * add node to the front (MRU) of the new freq list
     * if the old freq list becomes empty and it was `minFreq`, increment `minFreq`
  2. **evict()**

     * look at list for `minFreq`
     * remove its LRU node (tail)
     * remove that key from the key map
  3. **insert(key, value)**

     * create node with `freq = 1`
     * add to freq=1 list
     * set `minFreq = 1`

* Data structures that make it clean:

  * `Map<Integer, Node> keyToNode`
  * `Map<Integer, DoublyLinkedList> freqToList`
  * `Node { int key, val, freq; Node prev, next; }`
  * `DoublyLinkedList` with sentinel head/tail and `addFirst`, `remove`, `removeLast`, `isEmpty`

* Edge cases:

  * `capacity == 0` → always return -1 for `get`, ignore `put`
  * Updating existing key: does **not** change size, but does increase freq (because it counts as access)
  * When inserting a new key and size == capacity → evict first
  * `minFreq` only changes when the old `minFreq` list becomes empty after a touch/eviction

#####################################

## The problem requirement in one sentence

LFU Cache must do `get` and `put` in **O(1)** average time:

* Evict the item with the **lowest frequency**
* If multiple items share that lowest frequency, evict the **least recently used among them**

So we need to support **two orderings at once**:

1. **Frequency** ordering (LFU)
2. **Recency within same frequency** ordering (LRU tie-break)

---

## Data structures in the skeleton

### 1) `keyToNode: Map<key, Node>`

This is for O(1) lookup by key.

Each `Node` stores:

* `key`
* `val`
* `freq` (how many times it’s been used)
* `prev/next` pointers (so we can remove it from a list in O(1))

So if I call `get(10)`, I instantly get the exact node object (no list traversal).

---

### 2) `freqToList: Map<freq, DLL>`

This handles the **LRU tie-break per frequency**.

For every frequency `f`, `freqToList.get(f)` is a doubly linked list containing **all nodes with frequency f**, ordered by recency:

* **front (near head)** = most recently used among frequency `f`
* **back (near tail)** = least recently used among frequency `f`

So eviction is:

* Look at `minFreq`
* Evict from `freqToList.get(minFreq).removeLast()` (LRU among that min freq)

---

### 3) `minFreq`

Tracks the **smallest frequency currently present** in the cache.

Why do we need it?

* Without it, you’d have to scan frequencies to find the min (not O(1)).
* With it, you always know which frequency bucket to evict from.

---

## The DLL operations (why they are O(1))

`DLL` is a doubly linked list with sentinel `head` and `tail`:

* `addFirst(node)` inserts right after `head` (MRU position)
* `remove(node)` splices node out using `prev/next`
* `removeLast()` removes `tail.prev` (LRU position)

All are pointer updates → **O(1)**.

---

## What “touch” means (the heart of LFU)

A “touch” happens when a key is used:

* `get(key)` touches
* `put(key, newValue)` for an existing key touches (because it counts as access)

Touch does:

1. Remove node from its current freq list
2. If that list becomes empty and it was `minFreq`, increase `minFreq`
3. Increment node’s freq
4. Add node to the front of the new freq list (MRU within the new freq)

This is how the cache updates both **frequency** and **recency within that frequency**.

---

## Walkthrough 1 (classic tie-breaking): capacity = 2

Operations:

1. `put(1,1)`
2. `put(2,2)`
3. `get(1)`
4. `put(3,3)`  (forces eviction)
5. `get(2)`
6. `get(3)`
7. `get(1)`

### After `put(1,1)`

* keyToNode: {1(freq=1)}
* freqToList[1]: [1] (MRU→LRU same here)
* minFreq = 1

### After `put(2,2)`

* keyToNode: {1(f1), 2(f1)}
* freqToList[1]: [2, 1]
  (2 is most recent because it was inserted last)
* minFreq = 1

### `get(1)` → touch(1)

* Remove 1 from freq 1 list:

  * freqToList[1] becomes [2]
* oldFreq was 1, minFreq=1 but list not empty → minFreq stays 1
* Increment 1’s freq to 2
* Add 1 to front of freqToList[2]:

  * freqToList[2] = [1]

Now:

* freq 1: [2]
* freq 2: [1]
* minFreq = 1

### `put(3,3)` (cache full → evict first)

Evict from `minFreq=1`:

* freqToList[1] is [2]
* removeLast() evicts 2
* remove key 2 from map

Insert 3 with freq=1:

* freqToList[1] becomes [3]
* minFreq reset to 1

Now:

* freq 1: [3]
* freq 2: [1]
* keys: {1,3}

✅ Correct eviction: key 2 had the lowest freq (1). Even if there were multiple freq=1 keys, eviction would use LRU among them.

### `get(2)` → -1 (evicted)

### `get(3)` → touch(3)

* 3 moves from freq1 to freq2
* freq1 becomes empty → since oldFreq=1 == minFreq, minFreq becomes 2
* freq2 becomes [3, 1] (3 is MRU within freq2)

### `get(1)` → touch(1)

* 1 moves from freq2 to freq3
* freq2 remains [3], minFreq still 2
* freq3 becomes [1]

All consistent.

---

## Walkthrough 2 (shows LRU tie-break inside same freq): capacity = 3

Operations:

1. `put(1,1)`
2. `put(2,2)`
3. `put(3,3)`
4. `get(1)`   → freq(1)=2
5. `get(2)`   → freq(2)=2
6. `put(4,4)` → must evict from minFreq bucket
7. `put(5,5)` → another eviction

### After 1–3

All inserted with freq=1:

* freq1 list: [3,2,1] (3 MRU, 1 LRU)
* minFreq=1

### `get(1)` touch(1): move 1 from freq1 → freq2

* Remove 1 from freq1: freq1 becomes [3,2]
* minFreq stays 1
* freq2: [1]

### `get(2)` touch(2)

* Remove 2 from freq1: freq1 becomes [3]
* freq2: addFirst(2) → [2,1] (2 MRU within freq2)

Now:

* freq1: [3]
* freq2: [2,1]
* minFreq=1

### `put(4,4)` (cache full → evict)

Evict from minFreq=1:

* freq1 is [3]
* evict LRU in freq1 → 3

Insert 4 at freq1:

* freq1 becomes [4]
* minFreq=1

Cache keys: {1(f2),2(f2),4(f1)}

✅ We evicted 3 because it had lowest frequency.

### Now make a tie case at freq1 and evict by LRU

If we do:

* `put(5,5)` with current keys {1,2,4} (full)

Evict from minFreq=1:

* freq1 currently [4] so evict 4
  Insert 5 → freq1 [5]

But if freq1 had multiple keys, the one at the **tail** would be evicted (least recently used among freq1).

That’s exactly why each frequency maintains an LRU list.

---

## Why `minFreq++` is correct in `touch`

In `touch(node)`:

```java
oldList.remove(node);
if (oldFreq == minFreq && oldList.isEmpty()) {
    minFreq++;
}
node.freq++;
addFirst(node) to new freq list
```

Interpretation:

* If you just removed the *last* node from the smallest-frequency bucket, that bucket disappears.
* Therefore the minimum frequency must increase (typically by 1, because you moved a node from `minFreq` to `minFreq+1`, and frequencies only increase by 1 at a time on touch).

Also note:

* On inserting a new node, you always set `minFreq = 1` because a brand-new key starts at frequency 1 and that’s the smallest possible.

---

## Quick sanity checklist for correctness

This approach is correct iff:

* Every key is in exactly **one** freq list at a time.
* `touch` always moves node from freq f → f+1 and puts it MRU in that list.
* `minFreq` always points to an existing non-empty bucket.
* Eviction removes from `freqToList[minFreq]` **tail**.

The skeleton satisfies these.

---

## One small improvement you might add

After eviction, if the `minFreq` list becomes empty, some implementations keep `minFreq` as-is until next insertion resets to 1. Your code comment notes this.

It still works because:

* If you evict, you immediately insert a new node with freq=1 → `minFreq = 1`.
* So you don’t need to recompute minFreq in `evict()`.
*/

class LFUCache {
    static class Node {
        int key, val, freq = 1;
        Node prev, next;
        Node(int k, int v) { key = k; val = v; }
    }

    static class DLL {
        Node head = new Node(0, 0), tail = new Node(0, 0);
        int size = 0;
        DLL() { head.next = tail; tail.prev = head; }

        void addFirst(Node x) {
            Node n = head.next;
            head.next = x; x.prev = head;
            x.next = n; n.prev = x;
            size++;
        }

        void remove(Node x) {
            x.prev.next = x.next;
            x.next.prev = x.prev;
            size--;
        }

        Node removeLast() {
            if (size == 0) return null;
            Node x = tail.prev;
            remove(x);
            return x;
        }

        boolean isEmpty() { return size == 0; }
    }

    private final int capacity;
    private int minFreq = 0;

    private final Map<Integer, Node> keyToNode = new HashMap<>();
    private final Map<Integer, DLL> freqToList = new HashMap<>();

    public LFUCache(int capacity) {
        this.capacity = capacity;
    }

    public int get(int key) {
        Node node = keyToNode.get(key);
        if (node == null) return -1;
        touch(node);
        return node.val;
    }

    public void put(int key, int value) {
        if (capacity == 0) return;

        Node node = keyToNode.get(key);
        if (node != null) {
            node.val = value;
            touch(node);
            return;
        }

        if (keyToNode.size() == capacity) {
            evict();
        }

        Node fresh = new Node(key, value);
        keyToNode.put(key, fresh);
        freqToList.computeIfAbsent(1, f -> new DLL()).addFirst(fresh);
        minFreq = 1;
    }

    private void touch(Node node) {
        int oldFreq = node.freq;
        DLL oldList = freqToList.get(oldFreq);
        oldList.remove(node);

        if (oldFreq == minFreq && oldList.isEmpty()) {
            minFreq++; // because the old min freq bucket became empty
        }

        node.freq++;
        freqToList.computeIfAbsent(node.freq, f -> new DLL()).addFirst(node);
    }

    private void evict() {
        DLL list = freqToList.get(minFreq);
        Node lru = list.removeLast();          // LRU among min frequency
        keyToNode.remove(lru.key);
        // minFreq will be reset to 1 on next insertion, or updated by touch()
    }
}


/**
 * Your LFUCache object will be instantiated and called as such:
 * LFUCache obj = new LFUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */
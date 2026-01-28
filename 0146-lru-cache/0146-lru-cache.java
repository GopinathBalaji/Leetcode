/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache obj = new LRUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */



// Method 1: Using Doubly Linked List and HashMap
/*
# WHAT WAS I DOING WRONG:

## 1) You can’t write pointer wiring as field initializers like that

These lines:

```java
DoublyListNode dummyHead = new DoublyListNode(0);
DoublyListNode dummyTail = new DoublyListNode(0);
dummyHead.next = dummyTail;
dummyTail.prev = dummyHead;
```

The last two statements are not allowed directly in the class body (outside a constructor/initializer block). This won’t compile.

Fix: do the wiring in the constructor (or an instance initializer block).

---

## 2) Node stores only `val`, but LRU needs both `key` and `value`

Your map is:

```java
Map<Integer, DoublyListNode> map;
```

So it maps **key -> node**.

But your node only has:

```java
int val;
```

That’s the *value*, not the key.
When you evict the LRU node, you must remove it from the map by its **key**, but you have no way to know the key from the node.

So your eviction removal is fundamentally broken unless the node stores `key`.

Correct node should be like:

```java
int key, val;
```

---

## 3) `get()` doesn’t return anything (compile error)

Your `get` method:

```java
public int get(int key) {
    ...
    int val = node.val;
    moveToFront(node);
}
```

Missing `return val;` at the end.

---

## 4) `popLRU()` uses undefined variable names and wrong pointer logic

You wrote:

```java
private DoublyListNode popLRU(){
    DoublyListNode lastNode = dummyTail.prev;
    tail.prev = lastNode.prev;
    lastNode.prev.next = dummyTail;

    return lastNode;
}
```

Problems:

* `tail` is not defined; you meant `dummyTail`.
* Pointer updates are inconsistent; simplest is just call your `remove(lastNode)`.

Correct would be:

```java
DoublyListNode lastNode = dummyTail.prev;
remove(lastNode);
return lastNode;
```

---

## 5) You’re removing from the map incorrectly

You wrote:

```java
map.values().remove(node.val);
```

Problems:

* `map.values()` is a collection of `DoublyListNode`, but you’re removing an `int` (`node.val`).
* Even if you tried `map.values().remove(node)`, that would be **O(n)** and violates O(1).
* You must remove by key: `map.remove(evicted.key)`.

Which again requires nodes to store `key`.

---

## 6) `put()` forgets to insert new key into the map

In the `else` case:

```java
DoublyListNode node = new DoublyListNode(value);
addFirst(node);
```

You never do:

```java
map.put(key, node);
```

So `get(key)` will never find it.

---

## 7) Shadowing variable names (won’t compile)

Inside:

```java
if(size > capacity){
    DoublyListNode node = popLRU();
    ...
}
```

You redeclare `node` inside a scope where `node` already exists (from the `else` branch). Java won’t allow redeclaration in the same scope.

Rename the evicted node to something else (e.g., `lru`).

---

## 8) `size` isn’t maintained correctly on eviction

You do `size++` when inserting, but never decrement on eviction.

Either:

* keep `size` accurate: decrement when you evict, or
* don’t track `size` separately and just use `map.size()`.

---

## 9) `moveToFront()` duplicates logic you already wrote

Not “wrong,” but it’s duplicated code. Prefer:

```java
private void moveToFront(DoublyListNode node){
    remove(node);
    addFirst(node);
}
```
Less bug-prone.

#############################################

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




 


// Method 1.5: My attempt (similar to the above version but less clean)
/*
*/
// class DoublyListNode{
//     int key;
//     int val;
    
//     DoublyListNode next;
//     DoublyListNode prev;

//     DoublyListNode(int key, int val){
//         this.key = key;
//         this.val = val;
//     }
// }


// class LRUCache {
//     int capacity;
//     Map<Integer, DoublyListNode> map;

//     DoublyListNode dummyHead = new DoublyListNode(0, 0);
//     DoublyListNode dummyTail = new DoublyListNode(0, 0);
    

//     public LRUCache(int capacity) {
//         this.capacity = capacity;
//         map = new HashMap<>();

//         dummyHead.next = dummyTail;
//         dummyTail.prev = dummyHead;
//     }

//     private void remove(DoublyListNode node){
//         node.prev.next = node.next;
//         node.next.prev = node.prev;
//     }

//     private void addFirst(DoublyListNode node){
//         DoublyListNode next = dummyHead.next;
//         dummyHead.next = node;
//         node.prev = dummyHead;
//         node.next = next;
//         next.prev = node;
//     }

//     private void moveToFront(DoublyListNode node){
//         remove(node);
//         addFirst(node);
//     }

//     private DoublyListNode popLRU(){
//         DoublyListNode lastNode = dummyTail.prev;
//         remove(lastNode);

//         return lastNode;
//     }
    
//     public int get(int key) {
//         if(!map.containsKey(key)){
//             return -1;
//         }

//         DoublyListNode node = map.get(key);
//         int val = node.val;

//         moveToFront(node);

//         return val;
//     }
    
//     public void put(int key, int value) {
//         if(map.containsKey(key)){
//             DoublyListNode node = map.get(key);
//             node.val = value;

//             map.put(key, node);
//             moveToFront(node);
//         }else{
//             DoublyListNode node = new DoublyListNode(key, value);
//             addFirst(node);
//             map.put(key, node);

//             if(map.size() > capacity){
//                 DoublyListNode last = popLRU();
//                 map.remove(last.key);
//             }
//         }
//     }
// }

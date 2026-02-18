// Method 1: minHeap approach
/*
## Core idea (what we *want* to maintain)

At any moment in the stream, we want the **kth largest** number seen so far.

A super useful way to think about that:

> If you keep the **largest k elements** you’ve seen so far,
> then the **kth largest** is just the **smallest element among those k**.

So we don’t need *all* numbers. We only need the “top k”.

---

## Why a **min-heap** (not max-heap)

### If we keep the top k elements…

* We need to quickly discard elements that are **not** in the top k.
* Among the top k, the “worst” one (the smallest) is the first to be kicked out when a bigger number arrives.

A **min-heap** gives us exactly that:

* `peek()` → returns the **smallest** element in the heap in **O(1)**
* `poll()` → removes that smallest element in **O(log k)**

So with a min-heap:

* Keep heap size ≤ k
* Whenever heap grows to k+1, remove the smallest → we’re back to the **largest k elements**

And then:

* The heap’s root (`peek()`) is the smallest of the largest k → **the kth largest overall**.

---

## The key invariant (the thing that’s always true)

After processing some numbers, this structure maintains:

1. `pq` contains **exactly the k largest elements seen so far** (or fewer if we haven’t seen k yet).
2. If it contains k elements, then `pq.peek()` is the **kth largest** element seen so far.

That’s the whole trick.


## Walkthrough using the official-style example

**k = 3**, initial `nums = [4, 5, 8, 2]`

We’ll show the heap contents conceptually as a *set* of values (heap internal order differs, but `peek()` is the minimum).

### Build phase (constructor)

Start: `pq = []`

#### add(4)

* offer 4 → pq = [4]
* size = 1 ≤ 3 → don’t poll
* peek = 4
  ✅ pq holds largest up to 3 elements so far (just 4)

#### add(5)

* offer 5 → pq = [4, 5]
* size = 2 ≤ 3 → don’t poll
* peek = 4
  ✅ largest elements so far are {4, 5}, kth largest not “meaningful” yet if fewer than k, but structure is fine

#### add(8)

* offer 8 → pq = [4, 5, 8]
* size = 3 = k → don’t poll
* peek = 4
  ✅ top 3 are {4, 5, 8}; **3rd largest = 4** (correct)

#### add(2)

* offer 2 → pq = [2, 4, 8, 5] (conceptually contains {2,4,5,8})
* size = 4 > 3 → poll smallest (2)
* pq becomes {4, 5, 8}
* peek = 4
  ✅ we kept the largest 3 elements and dropped the extra smallest

So after constructor, `pq` contains `{4, 5, 8}` and `peek()` = **4**, meaning 3rd largest so far is 4.

---

## Now process stream adds

### 1) add(3)

Current pq = {4, 5, 8}

* offer 3 → {3, 4, 5, 8}
* size 4 > 3 → poll smallest → remove 3
* pq back to {4, 5, 8}
* return peek() = 4

✅ Output is **4** (3rd largest among [4,5,8,2,3] is 4)

---

### 2) add(5)

pq = {4, 5, 8}

* offer 5 → {4, 5, 5, 8}
* poll smallest (because size 4) → remove 4
* pq becomes {5, 5, 8}
* return peek() = 5

✅ Output is **5**

---

### 3) add(10)

pq = {5, 5, 8}

* offer 10 → {5, 5, 8, 10}
* poll smallest → remove 5
* pq becomes {5, 8, 10}
* return peek() = 5

✅ Output is **5**

---

### 4) add(9)

pq = {5, 8, 10}

* offer 9 → {5, 8, 9, 10}
* poll smallest → remove 5
* pq becomes {8, 9, 10}
* return peek() = 8

✅ Output is **8**

---

### 5) add(4)

pq = {8, 9, 10}

* offer 4 → {4, 8, 9, 10}
* poll smallest → remove 4
* pq becomes {8, 9, 10}
* return peek() = 8

✅ Output is **8**

These outputs match the expected sequence:
**4, 5, 5, 8, 8**

---

## Why this is fast (complexity, very explicitly)

The heap never grows beyond **k + 1** briefly.

Each `add(val)` does:

* `offer` → **O(log k)**
* maybe `poll` → **O(log k)**
* `peek` → **O(1)**

So:

* **Time per add:** `O(log k)`
* **Space:** `O(k)`

Compare that to your max-heap approach:

* You were effectively doing `O(n log n)` per add because you rebuilt/cleared lots of elements.

---

## Intuition in one sentence

A **min-heap of size k** is like keeping a “VIP list” of the top k numbers, and the smallest VIP (`peek`) is exactly the **kth largest overall**.
*/
class KthLargest{
    private final PriorityQueue<Integer> pq = new PriorityQueue<>(); // minHeap
    private final int k;

    public KthLargest(int k, int[] nums){
        this.k = k;
        for(int x: nums){
            add(x);
        }
    }

    public int add(int val){
        pq.offer(val);
        if (pq.size() > k) pq.poll(); // remove smallest, keep only k largest

        return pq.peek();             // kth larges
    }
}







// Method 1.5: My inefficient maxHeap approach 
/*
################### WHY IS IT INEFFICIENT ##################

A few things are “wrong” with this approach (some are correctness/robustness, most are efficiency/design for this problem).

## 1) You’re solving the wrong subproblem (heap direction)

LeetCode 703 is designed for a **min-heap of size k**:

* Keep the **k largest** elements seen so far.
* The **root** (smallest among those k) is the **kth largest overall**.
* Each `add` is `O(log k)`.

Your code uses a **max-heap of all elements**, then repeatedly polls `k` times to “reach” the kth largest. That’s conceptually workable, but it leads directly to the next big issue.

## 2) `add()` is extremely inefficient (and will TLE)

In every `add(val)` you do:

* `maxHeap.add(val)` → `O(log n)`
* Poll `k` times → `O(k log n)`
* **Clear the entire heap by polling everything**:

  ```java
  while(!maxHeap.isEmpty()){
      maxHeap.poll();
  }
  ```

  That’s `O(n log n)` per call.
* Reinsert `k` values → `O(k log k)` (or `O(k log n)` depending on heap size)

So each `add()` is basically **O(n log n)** where `n` is total elements seen so far. With up to ~10⁴ adds, this becomes huge.

This is the main reason it fails on LeetCode: **time complexity**.

## 3) You destroy information you’ll need later (design flaw)

After each `add`, you keep only the top `k` elements and discard the rest. That’s *okay* if your structure is truly “top-k maintenance”, but then a max-heap isn’t the right structure: you’re doing extra work to throw data away.

If you want to keep only k elements, do it directly with a **min-heap of size k**.

## 4) Edge-case risk: `k` larger than heap size

In `add` you blindly do:

```java
for (int i = 0; i < k; i++) temp.add(maxHeap.poll());
```

If (for any reason) the heap has fewer than `k` elements, `poll()` returns `null` and you’ll get a `NullPointerException` (auto-unboxing, or later usage).

LeetCode constraints usually ensure `k` is valid, but in the early state you might have `nums.length < k`. The intended min-heap solution handles that naturally.

## 5) Memory churn / overhead

You allocate a new `ArrayList` every `add`, and you do lots of heap operations. Even if it’s “correct”, it’s unnecessarily heavy.


# If you insist on a max-heap, the minimal “fix”

You *must* avoid clearing and rebuilding every time. But with only a max-heap, you can’t get kth largest without removing top elements unless you maintain extra structure. In other words: **a single max-heap can’t support “kth largest stream” efficiently**.

You’d need either:

* a min-heap of size k (best), or
* **two heaps** (one for top k, one for the rest) — but that’s just re-deriving the min-heap-of-k approach.

So the real “wrong” thing is: **max-heap + repeated polling is fundamentally the inefficient approach for a stream**.

If you want, I can show the two-heap version too, but the min-heap-of-size-k is the cleanest and intended solution.
*/
// class KthLargest {

//     PriorityQueue<Integer> maxHeap;
//     int k;

//     public KthLargest(int k, int[] nums) {
//         maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
//         this.k = k;

//         for(int i=0; i<nums.length; i++){
//             maxHeap.add(nums[i]);
//         }
//     }
    
//     public int add(int val) {
//         maxHeap.add(val);
//         List<Integer> temp = new ArrayList<>();

//         for(int i=0; i<k; i++){
//             temp.add(maxHeap.poll());
//         }

//         int returnVal = temp.get(temp.size() - 1);

//         while(!maxHeap.isEmpty()){
//             maxHeap.poll();
//         }

//         for(int i=0; i<k; i++){
//             maxHeap.add(temp.get(i));
//         }

//         return returnVal;
//     }
// }







// Method 2: Using Binary Search Tree (BST)
/*
Below is the **BST “order-statistics tree”** approach your code is implementing: a normal BST, but **each node also stores the size of its subtree** (`count`). That extra field is what lets you find the **kth largest** quickly.

---

## 1) What `count` means (the whole trick)

In your `TreeNode`:

```java
int count = 1;
```

You are maintaining this invariant:

> **node.count = total number of values stored in the subtree rooted at node**
> (including the node itself, and including duplicates)

So if a node’s left subtree has 3 nodes and right subtree has 5 nodes, then:

* `node.count = 1 + 3 + 5 = 9`

This is exactly what you need to do “rank queries” like “find kth largest”.

---

## 2) How insertion updates `count`

Your insertion:

```java
private TreeNode addNumToBST(TreeNode node, int num) {
    if (node == null) return new TreeNode(num);

    node.count = node.count + 1;   // <-- key line

    if (num < node.val) node.left = addNumToBST(node.left, num);
    else node.right = addNumToBST(node.right, num);

    return node;
}
```

### What’s happening

* Every time you insert `num`, you walk down a path from the root to a leaf.
* Every node on that path gets its subtree size increased by 1 because its subtree just gained one new element.

### Duplicates

You route duplicates to the **right** (`else` branch). That’s fine as long as you’re consistent.

* This means the BST property becomes:
  left < node.val, right ≥ node.val

---

## 3) How `findNthLargest` works

Your search:

```java
private int findNthLargest(int n, TreeNode node) {
    if (node == null) return Integer.MIN_VALUE;

    int rightCount = (node.right != null) ? node.right.count : 0;

    if (rightCount == n - 1) return node.val;

    if (rightCount < n - 1) {
        return findNthLargest(n - rightCount - 1, node.left);
    } else {
        return findNthLargest(n, node.right);
    }
}
```

### Key idea: “how many elements are larger than this node?”

In a BST:

* Everything in the **right subtree** is **≥ node.val** (and strictly greater if no duplicates).
* For kth **largest**, you want to prioritize the right side.

Let:

* `rightCount = size of right subtree`

Then:

* If `rightCount == n - 1`
  → there are exactly `n-1` elements larger than this node
  → **this node is the nth largest**

* If `rightCount > n - 1`
  → the nth largest is **somewhere in the right subtree**
  → go right with the same `n`

* If `rightCount < n - 1`
  → the nth largest is **not in the right subtree and not this node**
  → it must be in the left subtree
  → skip:

  * all `rightCount` nodes on the right
  * plus this node itself (1)
    → new target rank becomes: `n - rightCount - 1`, go left

This is exactly the classic “order statistic tree” query.

---

## 4) Thorough walkthrough (LeetCode-style example)

Use the canonical stream example:

* `k = 3`
* initial `nums = [4, 5, 8, 2]`
* then calls: add(3), add(5), add(10), add(9), add(4)
* expected outputs: **4, 5, 5, 8, 8**

I’ll show the BST with each node written as `value(count)`.

---

### Constructor build: insert 4, 5, 8, 2

#### Insert 4

```
4(1)
```

#### Insert 5

* Path: 4 → right
* Increment 4.count

```
4(2)
   \
   5(1)
```

#### Insert 8

* Path: 4 → 5 → right
* Increment 4.count, 5.count

```
4(3)
   \
   5(2)
      \
      8(1)
```

#### Insert 2

* Path: 4 → left
* Increment 4.count

```
      4(4)
     /   \
  2(1)   5(2)
           \
           8(1)
```

Now the structure has 4 elements. The 3rd largest should be **4** (largest are 8,5,4,…).

---

### Query after constructor (conceptually `findNthLargest(3, root)`)

At node **4**:

* `rightCount = size(right subtree) = 5.count = 2`
* Need `n-1 = 2`
* `rightCount == n-1` → return **4**

✅ kth largest = 4

---

## Stream operations

### 1) add(3)

#### Insert 3

Path: 4 → left(2) → right
Increment counts along path: 4, 2

Tree becomes:

```
        4(5)
       /   \
   2(2)    5(2)
     \       \
     3(1)    8(1)
```

#### find 3rd largest

At **4**:

* rightCount = 2
* n-1 = 2 → match → return **4**

✅ output **4**

---

### 2) add(5) (duplicate)

#### Insert 5

Duplicates go right. Path: 4 → right(5) → right(8) → left (because 5 < 8)
Increment counts: 4, 5, 8

Tree:

```
        4(6)
       /    \
   2(2)     5(3)
     \      /  \
     3(1) 5(1)  8(2)
                /
              5(1)
```

(There are now three “5” nodes total.)

#### find 3rd largest

At **4**:

* rightCount = 5(3)
* n-1 = 2, rightCount = 3 > 2 → go right, still n=3

At **5(3)** (the right child of 4):

* rightCount = size(node.right) = 8.count = 2
* n-1 = 2 → match → return **5**

✅ output **5**

---

### 3) add(10)

#### Insert 10

Path: 4 → 5 → 8 → right
Increment counts: 4, 5, 8

Tree now has 7 elements.

#### find 3rd largest (expected still 5)

At **4**:

* rightCount = 5.count = 4 > 2 → go right (n=3)

At **5**:

* rightCount = 8.count = 3 > 2 → go right (n=3)

At **8**:

* rightCount = size(right subtree) = 10.count = 1
* n-1 = 2
* rightCount(1) < 2 → go left, but adjust n:

  * skip right subtree (1) + this node (1) = 2 elements
  * new n = 3 - 1 - 1 = 1
  * find 1st largest in left subtree of 8 (that left child is a 5)

That returns **5**.

✅ output **5**

---

### 4) add(9)

Insert 9 goes: 4 → 5 → 8 → 10 → left
Then find 3rd largest:

At **4**: go right
At **5**: go right
At **8**:

* rightCount = 10.count = 2 (nodes {10,9})
* n-1 = 2 → match → return **8**

✅ output **8**

---

### 5) add(4) (duplicate 4)

Duplicate 4 goes right from the root: 4 → 5 → left(5) → left (because 4 < 5)
Then find 3rd largest:

At **4**: go right
At **5**: go right
At **8**:

* rightCount still = 2
* n-1 = 2 → return **8**

✅ output **8**

So outputs are: **4, 5, 5, 8, 8** (matches expected).

---

## 5) Complexity and what’s “good/bad” about this BST approach

### Time

Each `add` and each `findNthLargest` is **O(h)** where `h` is the tree height.

* If the BST stays balanced: `h ≈ log n` → great performance.
* If numbers come in sorted order (or mostly sorted): BST becomes a linked list → `h ≈ n` → can degrade badly.

**LeetCode 703 usually prefers the min-heap O(log k)** because it’s guaranteed.

### Space

BST stores every element: **O(n)**
(min-heap solution stores only k elements: **O(k)**)


If you want, I can also show:

* a **counterexample** where this unbalanced BST becomes O(n) per add (sorted stream),
* or a **Treap (randomized balanced BST)** version in Java that keeps the same `count` idea but makes expected time **O(log n)** reliably.
*/
// class KthLargest {

//     private class TreeNode {
//         int val;
//         int count = 1;      // subtree size: left + right + self
//         TreeNode left, right;

//         TreeNode(int val) { this.val = val; }
//     }

//     int k;
//     TreeNode root;

//     public KthLargest(int k, int[] nums) {
//         this.k = k;
//         for (int num : nums) {
//             root = addNumToBST(root, num);
//         }
//     }

//     public int add(int val) {
//         root = addNumToBST(root, val);
//         return findNthLargest(k, root);
//     }

//     // Find nth largest using subtree sizes.
//     private int findNthLargest(int n, TreeNode node) {
//         if (node == null) return Integer.MIN_VALUE; // should not happen if at least k elements exist

//         int rightCount = (node.right != null) ? node.right.count : 0;

//         // If exactly n-1 elements are in the right subtree, current node is nth largest
//         if (rightCount == n - 1) return node.val;

//         // If right subtree has fewer than n-1 elements,
//         // skip right subtree + current node and go left
//         if (rightCount < n - 1) {
//             return findNthLargest(n - rightCount - 1, node.left);
//         }

//         // Otherwise nth largest is in right subtree
//         return findNthLargest(n, node.right);
//     }

//     // Standard BST insert, but also increments count on the way down
//     private TreeNode addNumToBST(TreeNode node, int num) {
//         if (node == null) return new TreeNode(num);

//         node.count++; // subtree rooted here gains one element

//         if (num < node.val) node.left = addNumToBST(node.left, num);
//         else node.right = addNumToBST(node.right, num); // duplicates go to right

//         return node;
//     }
// }





/**
 * Your KthLargest object will be instantiated and called as such:
 * KthLargest obj = new KthLargest(k, nums);
 * int param_1 = obj.add(val);
 */
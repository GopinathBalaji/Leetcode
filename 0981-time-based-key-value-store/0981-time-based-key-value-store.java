// Method 1: Binary Search (half-open style) for get() 
/*
# WHAT WAS I DOING WRONG:

Two main things are wrong in `get()` (one correctness bug, one missing case). Your `set()` part is fine **assuming timestamps for a key are added in increasing order** (which LeetCode guarantees for this problem).

## 1) You’re returning the wrong index

Your binary search is a **lower_bound** for `>= timestamp`:

* After the loop, `left` is the **first index i where `list[i].timestamp >= timestamp`**.

But the problem asks for the value with the **largest timestamp `<=` the query timestamp**.

That’s the **previous element** (i.e., `left - 1`) in most cases.

Example:

* stored times: `[1, 4, 10]`
* query `timestamp = 6`
* your search returns `left = 2` (time 10, first `>= 6`)
* you return time 10’s value ❌
* correct is time 4’s value ✅

## 2) You can go out of bounds (and you don’t handle “no <= timestamp”)

Because `right = list.size()`, it’s half-open `[0, n)`. If all stored timestamps are `< timestamp`, then `left` becomes `n`.

Then `list.get(left)` throws `IndexOutOfBoundsException`.

Also if the query timestamp is smaller than the smallest stored timestamp, you should return `""`.


## Alternative (use lower_bound >= timestamp and handle equality)

If you really want `>=` lower_bound, you must:

* if `left < n` and `time == timestamp`, return it
* else return `left - 1`
* and handle `left == 0` / `left == n`

But the `> timestamp` (upper_bound) version above is cleaner for “largest <=”.

---

### One more subtle assumption

This approach assumes per key the list is sorted by timestamp. LeetCode’s `set` calls for a key come in increasing timestamp order, so `.add(...)` is enough. If that guarantee didn’t exist, you’d need to insert in sorted order or sort before searching.
*/
class Pair{
    String value;
    int timestamp;

    public Pair(String value, int timestamp){
        this.value = value;
        this.timestamp = timestamp;
    }
}

class TimeMap {
    Map<String, List<Pair>> map;

    public TimeMap() {
        map = new HashMap<>();
    }
    
    public void set(String key, String value, int timestamp) {
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(new Pair(value, timestamp));
    }
    
    public String get(String key, int timestamp) {
        List<Pair> list = map.get(key);

        if(list == null || list.size() == 0){
            return "";
        }

        int left = 0;
        int right = list.size(); // [left, right)

        // find first index with time > timestamp
        while(left < right){
            int mid = left + (right - left) / 2;

            int midTime = list.get(mid).timestamp;

            if(midTime <= timestamp){
                left = mid + 1;
            }else{
                right = mid;
            }
        }

        // Now left is first index with time > timestamp
        // This also checks if the query timestamp is smaller than the smallest stored timestamp
        // (i.e. we may go out of bounds if we are at 0 since we do left - 1)
        int idx = left - 1;
        if(idx < 0){
            return "";
        }

        return list.get(idx).value;
    }
}

/**
 * Your TimeMap object will be instantiated and called as such:
 * TimeMap obj = new TimeMap();
 * obj.set(key,value,timestamp);
 * String param_2 = obj.get(key,timestamp);
 */





//  Method 2: Binary Search (closed interval style)
/*
## Why this works

For each `key`, we store a list of `(timestamp, value)` pairs.
LeetCode guarantees that for the same key, `set()` is called with **non-decreasing timestamps**, so the list is already **sorted by timestamp**.

So `get(key, timestamp)` becomes:

> In a sorted list of timestamps, find the **rightmost** timestamp that is `<= timestamp`.

That’s a perfect use-case for binary search.

---

## Closed-interval binary search pattern

We keep a search window **[l, r]** (both inclusive).

* If `list[mid].timestamp <= queryTimestamp`:

  * `mid` is a valid answer candidate.
  * But maybe there’s an even later valid timestamp to the right.
  * So we set `ans = mid` and move right: `l = mid + 1`.

* Else (`list[mid].timestamp > queryTimestamp`):

  * mid is too large, so all indices ≥ mid are too large (because sorted).
  * Move left: `r = mid - 1`.

At the end:

* `ans` holds the best index found, or remains `-1` if nothing was `<=`.

---

## Thorough walkthrough examples

### Example 1

Operations:

* `set("foo","bar",1)`
* `set("foo","bar2",4)`
  Query:
* `get("foo", 4)` → should return `"bar2"`

List for `"foo"`: `[(1,"bar"), (4,"bar2")]`

Binary search:

* `l=0, r=1, ans=-1`
* `mid=0`, `midTime=1`
  `1 <= 4` ✅
  `ans=0`, move right: `l=1`
* now `l=1, r=1`
* `mid=1`, `midTime=4`
  `4 <= 4` ✅
  `ans=1`, move right: `l=2`
* loop ends (`l=2 > r=1`)
  Return `list[ans].value = list[1].value = "bar2"`

---

### Example 2 (timestamp between entries)

Same list, query:

* `get("foo", 3)` → should return `"bar"` (timestamp 1)

* `l=0, r=1, ans=-1`

* `mid=0`, `midTime=1`
  `1 <= 3` ✅
  `ans=0`, `l=1`

* `mid=1`, `midTime=4`
  `4 <= 3` ❌ too big
  `r=0`

* loop ends (`l=1 > r=0`)
  Return `list[0].value = "bar"`

---

### Example 3 (timestamp earlier than everything)

Query:

* `get("foo", 0)` → should return `""`

* `l=0, r=1, ans=-1`

* `mid=0`, `midTime=1`
  `1 <= 0` ❌
  `r = -1`

* loop ends immediately
  `ans` is still `-1` → return `""`

---

### Example 4 (timestamp later than everything)

Query:

* `get("foo", 100)` → should return `"bar2"`

* `mid=0` time 1 ≤ 100 → ans=0, l=1

* `mid=1` time 4 ≤ 100 → ans=1, l=2
  End → return `"bar2"`

---

## Complexity

* `set`: amortized **O(1)** append
* `get`: **O(log n)** per key, where `n` is number of entries for that key
* Space: **O(total pairs)**

---

If you want, I can also give the **half-open** version (`[l, r)`, `r=mid`, `l=mid+1`) for this same “rightmost ≤ timestamp” search and show the equivalence.
*/

// class TimeMap {

//     private static class Pair {
//         String value;
//         int timestamp;
//         Pair(String value, int timestamp) {
//             this.value = value;
//             this.timestamp = timestamp;
//         }
//     }

//     private final Map<String, List<Pair>> map;

//     public TimeMap() {
//         map = new HashMap<>();
//     }

//     // O(1) amortized append (timestamps for the same key are non-decreasing in LC981)
//     public void set(String key, String value, int timestamp) {
//         map.computeIfAbsent(key, k -> new ArrayList<>()).add(new Pair(value, timestamp));
//     }

//     // O(log n) per get for that key
//     public String get(String key, int timestamp) {
//         List<Pair> list = map.get(key);
//         if (list == null || list.isEmpty()) return "";

//         int l = 0;
//         int r = list.size() - 1;

//         int ans = -1; // will store index of best candidate (time <= timestamp)

//         while (l <= r) {
//             int mid = l + (r - l) / 2;
//             int midTime = list.get(mid).timestamp;

//             if (midTime <= timestamp) {
//                 // mid is a valid candidate; try to find a later one
//                 ans = mid;
//                 l = mid + 1;
//             } else {
//                 // mid is too late; go left
//                 r = mid - 1;
//             }
//         }

//         return ans == -1 ? "" : list.get(ans).value;
//     }
// }

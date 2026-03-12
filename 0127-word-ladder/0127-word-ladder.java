// Bidirectional BFS + Wildcard pattern buckets
/*
# What the problem is (mental model)

Each word is a node. There’s an edge between two words if they differ by exactly **one** letter. You want the **shortest path length** from `beginWord` to `endWord`. That’s a shortest path in an **unweighted graph** → do **BFS**.

Two practical improvements:

1. **Wildcard pattern buckets** to find neighbors fast.
2. **Bidirectional BFS** to reduce the search space (expand from both ends and stop when frontiers meet).

---

# Code walkthrough

```java
public int ladderLength(String beginWord, String endWord, List<String> wordList) {
    Set<String> dict = new HashSet<>(wordList);
    if (!dict.contains(endWord)) return 0;
```

* Put the dictionary in a `HashSet` for O(1) lookups.
* If `endWord` isn’t even in the list, there’s no valid chain → return 0 immediately.

```java
    int L = beginWord.length();
```

* All words have the same length. We’ll use `L` repeatedly to build patterns.

## 1) Build the wildcard bucket map

```java
    Map<String, List<String>> buckets = new HashMap<>();
    for (String w : dict) addPatterns(w, L, buckets);
    addPatterns(beginWord, L, buckets);
```

* We create a map from **pattern** → **list of words** matching that pattern.
* A pattern is built by replacing one character with `*`.
  Example: `"hot"` → `*ot`, `h*t`, `ho*`.
* Why? Any two words that differ at exactly one position will **share a pattern** at that position, so all 1-step neighbors live together in the same bucket.
* We include `beginWord` so its patterns also exist in the map even if it’s not in the list.

Helper that builds the buckets:

```java
private void addPatterns(String w, int L, Map<String, List<String>> buckets) {
    for (int i = 0; i < L; i++) {
        String pat = w.substring(0, i) + '*' + w.substring(i + 1);
        buckets.computeIfAbsent(pat, k -> new ArrayList<>()).add(w);
    }
}
```

* For each index `i`, make the pattern `w` with the `i`-th char replaced by `*`.
* `computeIfAbsent` means “if `pat` isn’t already a key, insert it with a new empty list; then return the list.”
  Then `.add(w)` appends `w` to that list.

## 2) Bidirectional BFS setup

```java
    Set<String> beginSet = new HashSet<>();
    Set<String> endSet   = new HashSet<>();
    Set<String> visited  = new HashSet<>();

    beginSet.add(beginWord);
    endSet.add(endWord);
    visited.add(beginWord);
    visited.add(endWord);

    int steps = 1; // number of words in the path; beginWord layer = 1
```

* We’ll always expand the **smaller** frontier (either the “begin side” or the “end side”) to keep the branching factor low.
* `visited` is shared so we never revisit words from either side.
* `steps` counts **words** along the ladder (LeetCode wants this, not the number of edges); the level that contains `beginWord` is length 1.

## 3) Bidirectional BFS loop

```java
    while (!beginSet.isEmpty() && !endSet.isEmpty()) {
        if (beginSet.size() > endSet.size()) {
            Set<String> tmp = beginSet; beginSet = endSet; endSet = tmp;
        }
```

* Expand the **smaller** set this round by swapping if needed.

```java
        Set<String> next = new HashSet<>();

        for (String word : beginSet) {
            for (int i = 0; i < L; i++) {
                String pat = word.substring(0, i) + '*' + word.substring(i + 1);
                List<String> neigh = buckets.getOrDefault(pat, Collections.emptyList());
```

* For each word in the current frontier, generate its `L` patterns and pull **all candidate neighbors** from the corresponding buckets.

```java
                for (String nb : neigh) {
                    if (nb.equals(word)) continue;          // skip itself
                    if (endSet.contains(nb)) return steps + 1; // met the other side!
                    if (!visited.contains(nb)) {
                        visited.add(nb);
                        next.add(nb);
                    }
                }
                // Optional optimization: buckets.put(pat, Collections.emptyList());
```

* If a neighbor is in the **opposite frontier** (`endSet`), we just connected the two BFS waves. The shortest ladder length is `steps + 1` (the current layer plus the meeting word).
* Otherwise, if not visited, mark it visited and queue for the next layer (`next`).
* Optional optimization: clearing the bucket after using it avoids scanning the same list many times; it’s safe because any word you’d care about from this pattern will be recorded now (or is already visited). (Not strictly required.)

```java
            }
        }

        beginSet = next;
        steps++;
    }
    return 0;
}
```

* Advance to the next level; if the loop ends without meeting, there’s no path.

---

# Detailed example walkthrough

**Input**

```
beginWord = "hit"
endWord   = "cog"
wordList  = ["hot","dot","dog","lot","log","cog"]
```

## Build buckets

For each word, create patterns (replace one char with `*`):

* hot → `*ot`, `h*t`, `ho*`
* dot → `*ot`, `d*t`, `do*`
* dog → `*og`, `d*g`, `do*`
* lot → `*ot`, `l*t`, `lo*`
* log → `*og`, `l*g`, `lo*`
* cog → `*og`, `c*g`, `co*`
* beginWord "hit" → `*it`, `h*t`, `hi*`

So buckets look like:

* `*ot`: [hot, dot, lot]
* `h*t`: [hot, hit]
* `ho*`: [hot]
* `d*t`: [dot]
* `do*`: [dot, dog]
* `*og`: [dog, log, cog]
* `d*g`: [dog]
* `l*t`: [lot]
* `lo*`: [lot, log]
* `l*g`: [log]
* `c*g`: [cog]
* `co*`: [cog]
* `*it`: [hit]
* `hi*`: [hit]

## Bidirectional BFS

Initialize:

* `beginSet = {hit}`, `endSet = {cog}`, `visited = {hit, cog}`, `steps = 1`.

### Round 1 (expand `beginSet` since |beginSet| = 1, |endSet| = 1; either is fine)

* word = `hit`

  * patterns: `*it`, `h*t`, `hi*`
  * `*it` → [hit] → skip self
  * `h*t` → [hot, hit] → neighbor `hot` is **new** → `next = {hot}`, mark visited.
  * `hi*` → [hit] → skip self

Set up next frontier:

* `beginSet = {hot}`, `steps = 2`.

### Round 2 (expand smaller; both sides size 1 → expand `beginSet`)

* word = `hot`

  * patterns: `*ot`, `h*t`, `ho*`
  * `*ot` → [hot, dot, lot]

    * `dot`: not visited & not in endSet → add to next
    * `lot`: same → add to next
  * `h*t` → [hot, hit] → both visited → skip
  * `ho*` → [hot] → skip self

Now:

* `next = {dot, lot}`, `beginSet = {dot, lot}`, `steps = 3`.

### Round 3 (expand the **smaller** frontier)

Now |beginSet| = 2, |endSet| = 1 → swap and expand the end side (fewer nodes).

* After swap: `beginSet = {cog}`, `endSet = {dot, lot}` (the names still mean “side being expanded” vs “the other side”).

Expand `cog`:

* patterns: `*og`, `c*g`, `co*`

  * `*og` → [dog, log, cog]

    * `dog`: not visited; is it in `endSet`? No → add to next
    * `log`: not visited; in `endSet`? No → add to next
  * `c*g` / `co*` contain only `cog` → skip

Next frontier becomes `{dog, log}`; `steps = 4`.

### Round 4

Now |beginSet| = 2 (`{dog, log}`), |endSet| = 2 (`{dot, lot}`) → either side; suppose we expand `{dog, log}`.

* Expand `dog`:

  * patterns: `*og` → [dog, log, cog] → skip/already visited
    `d*g` → [dog] → skip
    `do*` → [dot, dog]

    * neighbor `dot` **is in endSet** → the two waves meet here → **return `steps + 1 = 5`**.

The shortest ladder contains 5 words:
`hit (1) → hot (2) → dot (3) → dog (4) → cog (5)`

---

# Why this approach is fast and safe

* **Wildcard buckets** replace “try 26 letters at each position” with “look up L buckets and iterate their contents,” typically fewer trials.
* **Bidirectional BFS** drastically reduces how many nodes you expand on large dictionaries.
* **Visited set** (shared by both sides) guarantees each word is processed at most once.
* Checking `endSet.contains(nb)` lets you stop as soon as frontiers touch, which is exactly the shortest path length.

---

# Complexity (informal)

* Building buckets: each of N dictionary words yields L patterns → **O(N·L)** entries (plus `beginWord`’s L patterns).
* BFS: each expansion looks at up to L buckets; each word is visited once → roughly **O(N·L)** total neighbor checks on typical inputs.
* Space: buckets store up to N·L entries; visited/frontiers store up to N words.

---

# Common pitfalls this code avoids

* Forgetting to include `beginWord` in buckets (then its first neighbors are harder to fetch).
* Using a single-direction BFS (works, but slower).
* Counting steps as edges instead of words (LeetCode expects word count, so `steps` starts at 1 and we return `steps + 1` on meeting).

If you want, I can show a variant that actually returns the **path** (not just the length) by keeping parent pointers—handy for debugging!
*/

// class Solution {
//     public int ladderLength(String beginWord, String endWord, List<String> wordList) {
//         Set<String> dict = new HashSet<>(wordList);
//         if (!dict.contains(endWord)) return 0;

//         int L = beginWord.length();

//         // 1) Build wildcard pattern map: e.g. "hot" -> "*ot","h*t","ho*"
//         // pattern -> list of words that match it
//         Map<String, List<String>> buckets = new HashMap<>();
//         // include beginWord so its patterns are usable in the first expansion
//         for (String w : dict) addPatterns(w, L, buckets);
//         addPatterns(beginWord, L, buckets);

//         // 2) Bidirectional BFS
//         Set<String> beginSet = new HashSet<>();
//         Set<String> endSet   = new HashSet<>();
//         Set<String> visited  = new HashSet<>();

//         beginSet.add(beginWord);
//         endSet.add(endWord);
//         visited.add(beginWord);
//         visited.add(endWord);

//         int steps = 1; // counts words in path (beginWord layer = 1)

//         while (!beginSet.isEmpty() && !endSet.isEmpty()) {
//             // always expand the smaller frontier
//             if (beginSet.size() > endSet.size()) {
//                 Set<String> tmp = beginSet; beginSet = endSet; endSet = tmp;
//             }

//             Set<String> next = new HashSet<>();

//             for (String word : beginSet) {
//                 // generate patterns for 'word' and expand via buckets
//                 for (int i = 0; i < L; i++) {
//                     String pat = word.substring(0, i) + '*' + word.substring(i + 1);
//                     List<String> neigh = buckets.getOrDefault(pat, Collections.emptyList());

//                     for (String nb : neigh) {
//                         if (nb.equals(word)) continue;       // skip self
//                         if (endSet.contains(nb)) return steps + 1; // met other side
//                         if (!visited.contains(nb)) {
//                             visited.add(nb);
//                             next.add(nb);
//                         }
//                     }
//                     // Optional optimization: clear to avoid reusing same bucket many times
//                     // (safe because we've already recorded all neighbors from this pattern)
//                     // buckets.put(pat, Collections.emptyList());
//                 }
//             }

//             beginSet = next;
//             steps++;
//         }

//         return 0;
//     }

//     private void addPatterns(String w, int L, Map<String, List<String>> buckets) {
//         for (int i = 0; i < L; i++) {
//             String pat = w.substring(0, i) + '*' + w.substring(i + 1);
//             buckets.computeIfAbsent(pat, k -> new ArrayList<>()).add(w);
//         }
//     }
// }








// Method 2: Single direction BFS
/*
####################### WHAT WAS WRONG WITH MY APPROACH ########################
There are several **logic bugs** in your BFS that make it return the wrong length (and can even explode / loop).


## 2) Your `ans++` is not measuring “ladder length” ❌

You do:

```java
ans++;
```

once per popped word. That counts **how many nodes you processed**, not the **number of transformation steps**.

Word Ladder wants the **shortest path length in edges + 1**, i.e. BFS **levels**.

✅ Fix: BFS by levels (process `size = queue.size()` per level), or store `(word, dist)` in the queue.

---

## 3) You check `visited` too late (on pop), so you enqueue duplicates a lot ⚠️

Right now you do:

* enqueue neighbors without marking them visited
* only skip when you pop them

This causes many duplicates in the queue.

✅ Typical BFS pattern:

* When you add a neighbor to the queue, immediately mark it visited.

---

## 4) You never stop when you reach `endWord` ❌

You never check:

* “if neighbor is endWord”
* or “if current word is endWord”

So even if you reach it, you keep exploring and `ans` keeps changing.

✅ Fix: as soon as you reach `endWord`, return the current BFS distance.

---

## 5) You don’t “consume” patterns, causing repeated neighbor expansions ⚠️

For each word, you generate patterns and fetch `graph.get(pattern)`.

If you don’t clear/remove the list after using a pattern, you can re-scan the same neighbor lists many times.

✅ Common optimization:

```java
graph.remove(pattern); // or clear list
```

after processing that pattern once.

(Not required for correctness, but helps performance a lot.)

---

# Minimal “your style” corrections (conceptually)

To keep your structure but fix correctness:

* Do level BFS to compute length
* Return as soon as endWord found
* Mark visited when enqueueing neighbors
* Optionally remove pattern list after use
#################################################


# Why your original code was wrong (and what this fixes)

### 1) Wrong visited marking

You were doing `visited.add(beginWord)` inside the loop.
That means you never actually mark the current popped word as visited, causing repeats and huge queue growth.

✅ Fixed by marking **the actual word** visited (and doing it when enqueuing).

---

### 2) `ans++` was not the ladder length

Word Ladder needs the **shortest number of transformations**, which is BFS **levels**, not “how many nodes did I pop”.

✅ Fixed by doing **level-order BFS** with `level` and processing `queue.size()` nodes per level.

---

### 3) Missing early exit

You should stop as soon as `endWord` is reached (BFS guarantees it’s the shortest).

✅ Fixed by returning immediately when `word.equals(endWord)`.

---

### 4) Pattern list reprocessing

Without clearing patterns, you can repeatedly scan the same neighbor lists many times.

✅ Fixed by `graph.remove(pattern)` after expanding it once.

---

# How the “pattern graph” works

Instead of explicitly connecting every word to every word that differs by 1 letter (which is expensive), we build an **index**:

* For each word, create patterns by replacing one character with `*`.
* Words that share a pattern are neighbors (1 letter apart).

Example for `"hot"`:

* `*ot`
* `h*t`
* `ho*`

If `"dot"` exists:

* it also has `*ot`
  So `"hot"` and `"dot"` are neighbors.

This lets BFS find neighbors quickly.

---

# Thorough example walkthrough

### Input

```text
beginWord = "hit"
endWord   = "cog"
wordList  = ["hot","dot","dog","lot","log","cog"]
```

## Step 1: Build pattern map

For `"hot"`:

* `*ot` -> ["hot"]
* `h*t` -> ["hot"]
* `ho*` -> ["hot"]

For `"dot"`:

* `*ot` -> ["hot","dot"]
* `d*t` -> ["dot"]
* `do*` -> ["dot"]

For `"dog"`:

* `*og` -> ["dog"]
* `d*g` -> ["dog"]
* `do*` -> ["dot","dog"]

For `"lot"`:

* `*ot` -> ["hot","dot","lot"]
* `l*t` -> ["lot"]
* `lo*` -> ["lot"]

For `"log"`:

* `*og` -> ["dog","log"]
* `l*g` -> ["log"]
* `lo*` -> ["lot","log"]

For `"cog"`:

* `*og` -> ["dog","log","cog"]
* `c*g` -> ["cog"]
* `co*` -> ["cog"]

So the pattern map lets us jump from a word to all 1-letter neighbors by looking up its patterns.

---

## Step 2: BFS levels

### Level 1

Queue: [`hit`], visited: {hit}

Pop `"hit"`:
Patterns:

* `*it` → (none)
* `h*t` → ["hot"]
* `hi*` → (none)

So we enqueue `"hot"`.

Queue after Level 1: [`hot`], visited: {hit, hot}

Increment level → level = 2

---

### Level 2

Queue: [`hot`]

Pop `"hot"`:
Patterns:

* `*ot` → ["hot","dot","lot"]  → enqueue dot, lot
* `h*t` → ["hot"]              → nothing new
* `ho*` → ["hot"]              → nothing new

Queue after Level 2: [`dot`, `lot`]
visited: {hit, hot, dot, lot}

level = 3

---

### Level 3

Queue: [`dot`, `lot`]

Pop `"dot"`:
Patterns:

* `*ot` → already removed (optimization), so skip
* `d*t` → ["dot"] (none new)
* `do*` → ["dot","dog"] → enqueue dog

Pop `"lot"`:
Patterns:

* `*ot` removed
* `l*t` → ["lot"] (none new)
* `lo*` → ["lot","log"] → enqueue log

Queue after Level 3: [`dog`, `log`]
visited includes dog, log

level = 4

---

### Level 4

Queue: [`dog`, `log`]

Pop `"dog"`:
Patterns:

* `*og` → ["dog","log","cog"] → enqueue cog
* `d*g` → ["dog"] (none)
* `do*` removed

Pop `"log"`:

* it would also lead to `cog`, but `cog` already visited / enqueued

Queue after Level 4: [`cog`]

level = 5

---

### Level 5

Pop `"cog"` → it equals endWord → return **5**

✅ Output: **5**, which matches the expected ladder:
`hit -> hot -> dot -> dog -> cog` (5 words)

---

# Complexity (intuition)

Let:

* `N` = number of words in wordList
* `L` = length of each word

Building the pattern map: about `N * L` patterns.
BFS explores each word once, and with `graph.remove(pattern)` each pattern list is expanded at most once.

So it’s efficient in practice and standard accepted.
*/

class Solution {
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        // Put all words into a set for quick existence check
        Set<String> set = new HashSet<>(wordList);
        if (!set.contains(endWord)) {
            return 0;
        }

        // Build pattern -> list of words that match that pattern
        // Example: "hot" => "*ot", "h*t", "ho*"
        HashMap<String, List<String>> graph = new HashMap<>();
        for (String word : wordList) {
            for (int i = 0; i < word.length(); i++) {
                String pattern = word.substring(0, i) + "*" + word.substring(i + 1);
                graph.computeIfAbsent(pattern, k -> new ArrayList<>()).add(word);
            }
        }

        // BFS from beginWord
        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();

        queue.offer(beginWord);
        visited.add(beginWord);

        int level = 1; // beginWord itself counts as level 1

        while (!queue.isEmpty()) {
            int size = queue.size(); // process one BFS level at a time

            for (int s = 0; s < size; s++) {
                String word = queue.poll();

                // If we reached endWord, return current level (shortest path length)
                if (word.equals(endWord)) {
                    return level;
                }

                // Generate all patterns of the current word and expand neighbors
                for (int i = 0; i < word.length(); i++) {
                    String pattern = word.substring(0, i) + "*" + word.substring(i + 1);

                    List<String> neighbors = graph.get(pattern);
                    if (neighbors == null) continue;

                    for (String neighbor : neighbors) {
                        if (!visited.contains(neighbor)) {
                            visited.add(neighbor);
                            queue.offer(neighbor);
                        }
                    }

                    // Important optimization: prevent re-processing the same pattern list again
                    // This keeps runtime from blowing up.
                    graph.remove(pattern);
                }
            }

            level++;
        }

        return 0;
    }
}

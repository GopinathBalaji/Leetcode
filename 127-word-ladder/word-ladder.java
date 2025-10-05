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

class Solution {
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> dict = new HashSet<>(wordList);
        if (!dict.contains(endWord)) return 0;

        int L = beginWord.length();

        // 1) Build wildcard pattern map: e.g. "hot" -> "*ot","h*t","ho*"
        // pattern -> list of words that match it
        Map<String, List<String>> buckets = new HashMap<>();
        // include beginWord so its patterns are usable in the first expansion
        for (String w : dict) addPatterns(w, L, buckets);
        addPatterns(beginWord, L, buckets);

        // 2) Bidirectional BFS
        Set<String> beginSet = new HashSet<>();
        Set<String> endSet   = new HashSet<>();
        Set<String> visited  = new HashSet<>();

        beginSet.add(beginWord);
        endSet.add(endWord);
        visited.add(beginWord);
        visited.add(endWord);

        int steps = 1; // counts words in path (beginWord layer = 1)

        while (!beginSet.isEmpty() && !endSet.isEmpty()) {
            // always expand the smaller frontier
            if (beginSet.size() > endSet.size()) {
                Set<String> tmp = beginSet; beginSet = endSet; endSet = tmp;
            }

            Set<String> next = new HashSet<>();

            for (String word : beginSet) {
                // generate patterns for 'word' and expand via buckets
                for (int i = 0; i < L; i++) {
                    String pat = word.substring(0, i) + '*' + word.substring(i + 1);
                    List<String> neigh = buckets.getOrDefault(pat, Collections.emptyList());

                    for (String nb : neigh) {
                        if (nb.equals(word)) continue;       // skip self
                        if (endSet.contains(nb)) return steps + 1; // met other side
                        if (!visited.contains(nb)) {
                            visited.add(nb);
                            next.add(nb);
                        }
                    }
                    // Optional optimization: clear to avoid reusing same bucket many times
                    // (safe because we've already recorded all neighbors from this pattern)
                    // buckets.put(pat, Collections.emptyList());
                }
            }

            beginSet = next;
            steps++;
        }

        return 0;
    }

    private void addPatterns(String w, int L, Map<String, List<String>> buckets) {
        for (int i = 0; i < L; i++) {
            String pat = w.substring(0, i) + '*' + w.substring(i + 1);
            buckets.computeIfAbsent(pat, k -> new ArrayList<>()).add(w);
        }
    }
}

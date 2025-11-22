// Method 1: Backtracking
/*
# Where was I going wrong:

## 1. You never mark cells as *visited*

In Word Search, each cell can be used **at most once per path**. Your current DFS can revisit the same cell multiple times in a single word path because you don’t track visited cells.

For example, with something like:

```text
A A
A A
```

you could walk back and forth over the same `A` indefinitely as long as letters match, which is not allowed.

You need either:

* a `boolean[][] visited` array, or
* temporarily modify `board[i][j]` (e.g., set it to `'#'`) and restore it after recursion.

Right now, you have neither.

---

## 2. Your boolean accumulation uses `&&` instead of `||`

In your DFS:

```java
boolean check = false;

if (j - 1 >= 0 && board[i][j-1] == word.charAt(idx)) {
    check = backtrack(board, word, i, j-1, idx + 1);
}
if (i - 1 >= 0 && board[i-1][j] == word.charAt(idx)) {
    check = check && backtrack(board, word, i-1, j, idx + 1);
}
...
```

Two problems:

1. You start with `check = false`.
2. You combine further results with `check && backtrack(...)`.

But logically you want: **“if any direction works, return true”**, i.e. an **OR**, not AND.

Correct behavior:

* If *one* neighbor path returns `true`, you want to return `true` immediately.
* You definitely **don’t** want a later `false` to cancel out a previous `true`.

So these lines should be something like:

```java
if (j - 1 >= 0 && board[i][j-1] == word.charAt(idx)) {
    if (backtrack(...)) return true;
}
...
return false;
```

or use `check = check || backtrack(...)`.

With `&&`, even if one direction succeeds, another direction returning false will make `check` false again.

---

## 3. Bug in the last recursive call (wrong coordinates)

Look at your last direction:

```java
if (j + 1 < board[0].length && board[i][j+1] == word.charAt(idx)) {
    check = check && backtrack(board, word, i, j-1, idx + 1);
}
```

Condition checks the **right** neighbor `j+1`, but the recursive call uses `j-1`:

```java
backtrack(..., i, j-1, ...)
```

So even when checking the right cell, you recurse to the **left** cell again. That’s just a typo but completely breaks the right-direction search.

It should be:

```java
if (j + 1 < board[0].length && board[i][j+1] == word.charAt(idx)) {
    check = check || backtrack(board, word, i, j+1, idx + 1);
}
```

---

## 4. More natural DFS structure

Your base case is fine:

```java
if (idx == word.length()) {
    return true;
}
```

But the usual pattern is:

1. Check bounds and visited.
2. Check if char matches current index.
3. If `idx == word.length() - 1` and char matches, return true.
4. Mark visited, recurse in 4 directions with `idx+1`, unmark visited, return OR of results.


Key differences vs your attempt:

* Added `visited[][]` to prevent reusing cells in the same path.
* Fixed logical combination to use OR (short-circuit).
* Fixed the `j+1` recursive call.
* Let DFS itself handle the matching of `word.charAt(idx)` instead of matching before calling.

---

### TL;DR where you went wrong

1. No `visited` tracking → can reuse cells illegally.
2. Combining recursion results with `&&` instead of `||`.
3. Typo in right-direction call (`j-1` instead of `j+1`).
4. No early return when any direction succeeds.


## Example board + word

Use the classic LeetCode example:

```text
board =
A B C E
S F C S
A D E E

word = "ABCCED"
```

Coordinates:

* (0,0) A   (0,1) B   (0,2) C   (0,3) E
* (1,0) S   (1,1) F   (1,2) C   (1,3) S
* (2,0) A   (2,1) D   (2,2) E   (2,3) E

One valid path for `"ABCCED"` is:

`A(0,0) → B(0,1) → C(0,2) → C(1,2) → E(2,2) → D(2,1)`

We’ll see how DFS + backtracking finds it.

---

## High-level picture of backtracking

At each `dfs(i, j, idx)`:

1. **Check**:

   * Are we in bounds?
   * Is this cell already used in this path? (visited)
   * Does `board[i][j]` match `word[idx]`?

2. **Choose**:

   * Mark `visited[i][j] = true` (we commit to using this cell for this character).

3. **Explore**:

   * Try all 4 neighbors with `idx + 1`.

4. **Un-choose (backtrack)**:

   * Set `visited[i][j] = false` before returning so other paths can reuse this cell in different ways.

Backtracking = **step 4**: we **undo** the choice when returning from recursion.

---

## Step-by-step trace: finding “ABCCED”

### 1. Outer loop: find starting cells

We iterate all cells, looking for `word.charAt(0) = 'A'`.

* At (0,0): board[0][0] = 'A' → matches → call
  `dfs(0, 0, idx = 0)`

For clarity, I’ll show calls as: `dfs(i, j, idx)` with current `word[idx]`.

---

### 2. dfs(0,0,0) — matching 'A'

* `idx = 0`, `word[0] = 'A'`
* `board[0][0] = 'A'` → match.
* `visited[0][0] = true` (choose this cell).

Now try 4 directions for next character `word[1] = 'B'`:

```text
found =
    dfs(0, -1, 1) ||  // left
    dfs(-1, 0, 1) ||  // up
    dfs(1, 0, 1) ||   // down
    dfs(0, 1, 1)      // right
```

* Left: `j = -1` → out of bounds → returns false.
* Up: `i = -1` → out of bounds → returns false.
* Down: `(1,0)` is 'S', but we’ll see that mismatches later.
* Right: `(0,1)` is 'B'.

Let’s say order is L, U, D, R:

#### 2.1. Try down first where match fails: dfs(1,0,1)

Call `dfs(1,0,1)`:

* Check:

  * in bounds,
  * not visited,
  * `board[1][0] = 'S'`, `word[1] = 'B'` → mismatch → returns **false**.
* We never mark visited[1][0] because mismatch happened before “choose”.

Back in `dfs(0,0,0)`, so far `found = false`.

#### 2.2. Try right: dfs(0,1,1) — matching 'B'

Call `dfs(0,1,1)`:

* `board[0][1] = 'B'`, `word[1] = 'B'` → match.
* `visited[0][1] = true` (choose this cell).

Now we need `word[2] = 'C'`. Try 4 neighbors of (0,1):

```text
dfs(0,0,2)   // left
dfs(-1,1,2)  // up
dfs(1,1,2)   // down
dfs(0,2,2)   // right
```

* Left: (0,0) is 'A' but visited → returns false.
* Up: out of bounds → false.
* Down: (1,1) is 'F' vs 'C' → mismatch → false.
* Right: (0,2) is 'C' → good, explore that.

So we go to `dfs(0,2,2)`.

---

### 3. dfs(0,2,2) — first 'C'

At `(0,2)`:

* `board[0][2] = 'C'`, `word[2] = 'C'` → match.
* Mark `visited[0][2] = true`.

Next char `word[3] = 'C'`.

Neighbors:

```text
dfs(0,1,3)   // left
dfs(-1,2,3)  // up
dfs(1,2,3)   // down
dfs(0,3,3)   // right
```

* Left: `(0,1)` is 'B' not 'C' → false.
* Up: out of bounds → false.
* Right: `(0,3)` is 'E' not 'C' → false.
* Down: `(1,2)` is 'C' → correct path.

So we go to `dfs(1,2,3)`.

Notice: we **tried a “wrong” neighbor** `(0,3)` and quickly failed, then tried the next neighbor `(1,2)`. That’s backtracking at work: it explores a branch (wrong neighbor), sees it fails, and returns to try another direction.

---

### 4. dfs(1,2,3) — second 'C'

At `(1,2)`:

* Match `word[3] = 'C'`.
* `visited[1][2] = true`.

Next char `word[4] = 'E'`.

Neighbors:

```text
dfs(1,1,4)   // left (F)
dfs(0,2,4)   // up (already used C)
dfs(2,2,4)   // down (E)
dfs(1,3,4)   // right (S)
```

* Left: F vs E → false.
* Up: (0,2) is visited → false.
* Right: S vs E → false.
* Down: `(2,2)` is 'E' → good → explore.

So we go to `dfs(2,2,4)`.

---

### 5. dfs(2,2,4) — 'E'

At `(2,2)`:

* `board[2][2] = 'E'`, `word[4] = 'E'` → match.
* `visited[2][2] = true`.

Next char `word[5] = 'D'`.

Neighbors:

```text
dfs(2,1,5)   // left (D)
dfs(1,2,5)   // up (visited C)
dfs(3,2,5)   // down (out of bounds)
dfs(2,3,5)   // right (E)
```

* Up: visited → false.
* Down: out of bounds → false.
* Right: 'E' vs 'D' → false.
* Left: `(2,1)`, 'D' vs 'D' → good → `dfs(2,1,5)`.

---

### 6. dfs(2,1,5) — 'D'

At `(2,1)`:

* `board[2][1] = 'D'`, `word[5] = 'D'` → match.
* Mark `visited[2][1] = true`.

Now `idx = 5` is the index of last char (`word.length() = 6`).

We call neighbors with `idx + 1 = 6`:

Any `dfs(..., 6)` hits:

```java
if (idx == word.length()) return true;
```

So one of those recursive calls (first valid neighbor) returns **true**, then `found` becomes **true** and bubbles up.

You don’t actually need a real neighbor here; you can also treat “we matched last char” as success and return `true` immediately before exploring neighbors. But with this structure, first neighbor call with `idx+1` returns `true`.

---

## Where exactly does “backtracking” happen?

The backtracking is in these two critical lines:

```java
visited[i][j] = true;  // choose

boolean found = dfs(...left...) ||
                dfs(...up...)   ||
                dfs(...down...) ||
                dfs(...right...);

visited[i][j] = false; // un-choose (backtrack)
```

Let’s highlight **how that helps** in concrete situations:

### A) Trying a wrong path and undoing it

At `dfs(0,2,2)` (first C):

* We mark `(0,2)` as visited.
* We try neighbors: left, up, right, down.
* Right `(0,3)` = 'E' does not match `word[3]='C'` → that recursive call returns `false`.
* We **do not** unmark `(0,2)` yet, because we’re still exploring other directions from `(0,2)`.
* Finally, we try down `(1,2)` and succeed.

Backtracking’s role here:

* It allows us to **explore multiple neighbor directions**.
* If a direction fails (returns false), we “backtrack up” and try another direction.

If we didn’t backtrack logically:

* You’d pick one direction and, if it fails, you’d be stuck and not try others.

### B) Avoiding reusing the same cell in one path (visited[][])

Consider `word = "ABCB"` on the same board. The path would need to reuse `(0,1)` 'B', which is illegal.

At some point, you’d end up at `(0,1)` 'B' with `idx = 1`. You’ll mark it `visited[0][1] = true`, and proceed. Later, to match the final 'B', you might try to step back onto `(0,1)`, but:

```java
if (visited[i][j]) return false;
```

This prevents you from going there again in the **same path**. That’s not “undoing” yet, but it’s a direct result of choosing and marking.

Then, when the entire path from that starting point fails (because you can’t reuse 'B'), you unwind the recursion, and every DFS call does:

```java
visited[i][j] = false; // backtrack
```

So after returning all the way to top level, `visited` is completely reset, and the **next starting cell** can still use all cells freely. That’s another aspect of backtracking: **resetting the state so other branches see a clean board**.

### C) Reusing cells *across* different paths, but not *within* one path

* While exploring one specific path, visited prevents reusing a cell = no cycles / reusing same letter.
* Once that path is done (either success or failure), backtracking unmarks everything used along that recursion chain.
* That allows the outer loop to start DFS from another starting point (maybe another 'A' in the grid) with a fresh `visited` state.

Without the unmarking line:

```java
visited[i][j] = false;
```

once a cell is visited in any DFS path, it would *never* be available to any other path, and you’d miss valid solutions that start elsewhere or reach the cell by a different route.

---

## Intuitive analogy

Think of it like exploring a maze:

* `visited[i][j] = true` = “I walked into this cell; I’ll remember that I’m here to avoid looping.”
* Recursing to neighbors = “Let me try every corridor from this room.”
* If all corridors fail, you **walk back out of the room** and say “Actually I’m no longer in this cell” → `visited[i][j] = false`.
* That way, if you later enter the same cell via a different path, you’re allowed to explore from it again.

Backtracking is precisely this *walk back out / undo* step.

In the Word Search solution, that line:

```java
visited[i][j] = false;
```

is what allows:

* Trying different routes from the same starting letter,
* Avoiding illegal reuse within one route,
* But still allowing reuse across different routes.
*/
class Solution {
    public boolean exist(char[][] board, String word) {
        int rows = board.length;
        int cols = board[0].length;
        boolean[][] visited = new boolean[rows][cols];

        for(int i=0; i<rows; i++){
            for(int j=0; j<cols; j++){
                if(board[i][j] == word.charAt(0)){
                    if(backtrack(board, word, i, j, 0, visited)){
                        return true;
                    }
                }
            }
        }

        return false;
    }


    private static boolean backtrack(char[][] board, String word, int i, int j, int idx, boolean[][] visited){
        // If we've matched all characters
        if(idx == word.length()){
            return true;
        }

        // Out of bounds or already used or char mismatch
        if(i < 0 || i >= board.length || j < 0 || j >= board[0].length){
            return false;
        }
        if(visited[i][j]){
            return false;
        }
        if(board[i][j] != word.charAt(idx)){
            return false;
        }

        // Use this cell
        visited[i][j] = true;

        // Explore 4 directions
        boolean found = backtrack(board, word, i, j-1, idx + 1, visited) ||
                        backtrack(board, word, i-1, j, idx + 1, visited) ||
                        backtrack(board, word, i+1, j, idx + 1, visited) ||
                        backtrack(board, word, i, j+1, idx + 1, visited);

        // Backtrack: unmark
        visited[i][j] = false;

        return found;
    }
}




// Method 2: Similar DFS approach
/*
## Core DFS idea

We want to know if there exists a path in the grid that spells `word`, moving **up/down/left/right**, using each cell **at most once** in a path.

Strategy:

1. Try to start the word from **every cell**.
2. From a starting cell that matches `word[0]`, run a **DFS**:

   * At position `(i,j)` trying to match `word[idx]`:

     * If out of bounds or already used in this path → fail.
     * If `board[i][j] != word[idx]` → fail.
     * If `idx == word.length - 1` and char matches → success.
     * Otherwise:

       * Temporarily mark `(i,j)` as used (e.g., change the char).
       * DFS to neighbors `(up, down, left, right)` with `idx+1`.
       * Restore the original char (backtrack).
3. If any DFS returns true, the word exists.


Key points:

* We don’t pass a `visited[][]`; instead we mark the used cell as `'#'` and restore it afterward.
* DFS returns `true` as soon as it finds a full match, and this short-circuits back up the recursion.

---

## Thorough example walkthrough

Use this board and word (classic test):

```text
board =
A B C E
S F C S
A D E E

word = "ABCCED"
```

Coordinates:

* (0,0) A   (0,1) B   (0,2) C   (0,3) E
* (1,0) S   (1,1) F   (1,2) C   (1,3) S
* (2,0) A   (2,1) D   (2,2) E   (2,3) E

We want a path that spells: `A → B → C → C → E → D`.

One valid path is:

`A(0,0) → B(0,1) → C(0,2) → C(1,2) → E(2,2) → D(2,1)`

We’ll track calls as `dfs(i,j,idx)` where `idx` is the index in `word`.

---

### Step 0 – Outer loop starts DFS from each cell

The outer `exist` method tries:

* (0,0), (0,1), (0,2), … until it finds a path.

Start with `(0,0)`:

```java
dfs(0, 0, 0)
```

We’re trying to match `word[0] = 'A'`.

---

### 1. dfs(0,0,0) – matching 'A'

* In bounds.
* `board[0][0] = 'A'`, matches `word[0] = 'A'`.
* `idx != lastIndex` (`0 != 5`), continue.
* Mark `(0,0)` as used: `board[0][0] = '#'`.

Now explore neighbors for `word[1] = 'B'`:

```text
dfs(-1,0,1)   // up
dfs(1,0,1)    // down
dfs(0,-1,1)   // left
dfs(0,1,1)    // right
```

Order doesn’t matter logically; let’s take them in the written order.

#### 1.1 dfs(-1,0,1)

* Out of bounds → returns **false**.

#### 1.2 dfs(1,0,1)

* `board[1][0] = 'S'`, but `word[1] = 'B'` → mismatch → **false**.

#### 1.3 dfs(0,-1,1)

* Out of bounds → **false**.

#### 1.4 dfs(0,1,1) – candidate for 'B'

At `(0,1)`:

* `board[0][1] = 'B'`, matches `word[1] = 'B'`.
* Not last char → continue.
* Mark `(0,1)` as used: `board[0][1] = '#'`.

Now search for `word[2] = 'C'` from `(0,1)`:

```text
dfs(-1,1,2)   // up
dfs(1,1,2)    // down
dfs(0,0,2)    // left
dfs(0,2,2)    // right
```

---

### 2. dfs(0,1,1) → neighbors for 'C'

#### 2.1 dfs(-1,1,2)

* Out of bounds → **false**.

#### 2.2 dfs(1,1,2)

* `board[1][1] = 'F'` vs `word[2]='C'` → mismatch → **false**.

#### 2.3 dfs(0,0,2)

* `board[0][0] = '#'` (we used it already for 'A') → mismatch → **false**.

#### 2.4 dfs(0,2,2) – candidate for first 'C'

At `(0,2)`:

* `board[0][2] = 'C'`, matches `word[2] = 'C'`.
* Not last char.
* Mark `(0,2)` as used: `board[0][2] = '#'`.

Now search for next `word[3] = 'C'` from `(0,2)`:

```text
dfs(-1,2,3)   // up
dfs(1,2,3)    // down
dfs(0,1,3)    // left
dfs(0,3,3)    // right
```

---

### 3. dfs(0,2,2) → neighbors for second 'C'

#### 3.1 dfs(-1,2,3)

* Out of bounds → **false**.

#### 3.2 dfs(1,2,3) – candidate for second 'C'

At `(1,2)`:

* `board[1][2] = 'C'`, matches `word[3] = 'C'`.
* Not last char.
* Mark `(1,2)` as used: `board[1][2] = '#'`.

Next char `word[4] = 'E'`.

Neighbors of `(1,2)`:

```text
dfs(0,2,4)    // up
dfs(2,2,4)    // down
dfs(1,1,4)    // left
dfs(1,3,4)    // right
```

We’ll return here after exploring them.

#### 3.3 (we will come back later to dfs(0,1,3), dfs(0,3,3) if needed)

---

### 4. dfs(1,2,3) → neighbors for 'E'

#### 4.1 dfs(0,2,4)

* `board[0][2] = '#'` (used for first 'C') → mismatch → **false**.

#### 4.2 dfs(2,2,4) – candidate for 'E'

At `(2,2)`:

* `board[2][2] = 'E'`, matches `word[4] = 'E'`.
* Not last char (`idx=4`, last index=5).
* Mark `(2,2)` as used: `board[2][2] = '#'`.

Next char `word[5] = 'D'`.

Neighbors of `(2,2)`:

```text
dfs(1,2,5)    // up
dfs(3,2,5)    // down
dfs(2,1,5)    // left
dfs(2,3,5)    // right
```

#### 4.3 dfs(1,1,4)

* This is 'F' vs 'E' → mismtach → false (we’d check it, but we already found a promising down path; I’m just following one successful branch).

#### 4.4 dfs(1,3,4)

* 'S' vs 'E' → mismatch → false.

---

### 5. dfs(2,2,4) → neighbors for 'D'

#### 5.1 dfs(1,2,5)

* `board[1][2] = '#'` (used for previous 'C') → mismatch → **false**.

#### 5.2 dfs(3,2,5)

* Out of bounds → **false**.

#### 5.3 dfs(2,1,5) – candidate for 'D'

At `(2,1)`:

* `board[2][1] = 'D'`, matches `word[5] = 'D'`.

Now note: `idx = 5` and `word.length() = 6`, so this is the **last character** (`idx == word.length() - 1`).

We hit:

```java
if (idx == word.length() - 1) {
    return true;
}
```

So `dfs(2,1,5)` returns **true**.

#### 5.4 dfs(2,3,5)

We don’t need to go here because we already had a `true` in the OR-chain.

Back in `dfs(2,2,4)`:

```java
boolean found =
    dfs(1,2,5) || // false
    dfs(3,2,5) || // false
    dfs(2,1,5) || // true
    dfs(2,3,5);   // short-circuits, not even called
```

So `found = true`.

We then backtrack:

```java
board[2][2] = 'E';  // restore
return true;
```

---

### 6. Backtracking up the call stack

Once a deep dfs returns `true`, it bubbles up:

* `dfs(2,2,4)` returned true → `dfs(1,2,3)` sees `found = true`, restores `(1,2)` to `'C'`, returns true.
* `dfs(1,2,3)` returning true → `dfs(0,2,2)` sees `found = true`, restores `(0,2)` to `'C'`, returns true.
* `dfs(0,2,2)` true → `dfs(0,1,1)` restores `(0,1) = 'B'`, returns true.
* `dfs(0,1,1)` true → `dfs(0,0,0)` restores `(0,0) = 'A'`, returns true.
* `dfs(0,0,0)` true → `exist(...)` returns true and the whole search stops.

Backtracking’s restoration steps:

```java
board[i][j] = temp;
```

are crucial because:

* While going *down* a successful path, we mark cells as `'#'`.
* While coming back *up* (regardless of success/failure), we restore them so other starting positions or alternative paths can use them.

---

## Why DFS + backtracking is necessary here

1. **DFS** lets us explore a path that spells the word, character by character, moving in 4 directions.
2. **Backtracking** (the restore step) ensures:

   * We don’t reuse cells in a single path (thanks to temporary marking).
   * But once a path finishes (success or failure), the grid is restored so other paths can try those cells.
3. The **OR-chain** of neighbor DFS calls means:

   * If one direction finds the word, we don’t waste time exploring the rest.
   * If a direction fails, we “backtrack” and try the next direction.

So in this example, we tried several dead directions (`S`, `F`, out of bounds, revisiting used cells), all of which returned false and got pruned, and the search eventually finds the one valid path.
*/

// class Solution {
//     public boolean exist(char[][] board, String word) {
//         int rows = board.length;
//         int cols = board[0].length;

//         for (int i = 0; i < rows; i++) {
//             for (int j = 0; j < cols; j++) {
//                 // Try to start from every cell
//                 if (dfs(board, word, i, j, 0)) {
//                     return true;
//                 }
//             }
//         }
//         return false;
//     }

//     // Try to match word[idx..end] starting at cell (i, j)
//     private boolean dfs(char[][] board, String word, int i, int j, int idx) {
//         // If we've matched all characters
//         if (idx == word.length()) {
//             return true;
//         }

//         // Check bounds
//         if (i < 0 || i >= board.length || j < 0 || j >= board[0].length) {
//             return false;
//         }

//         // Char mismatch
//         if (board[i][j] != word.charAt(idx)) {
//             return false;
//         }

//         // If this is the last character and it matches, success
//         if (idx == word.length() - 1) {
//             return true;
//         }

//         // Mark this cell as used (in-place)
//         char temp = board[i][j];
//         board[i][j] = '#';  // any sentinel not in the board

//         // Explore neighbors for the next character
//         boolean found =
//                 dfs(board, word, i - 1, j, idx + 1) || // up
//                 dfs(board, word, i + 1, j, idx + 1) || // down
//                 dfs(board, word, i, j - 1, idx + 1) || // left
//                 dfs(board, word, i, j + 1, idx + 1);   // right

//         // Backtrack: restore the cell
//         board[i][j] = temp;

//         return found;
//     }
// }

// Method 1: Greedy Approach
/*
### Hint 1 — What does `*` really mean?

Each `*` can act as **one of three**: `'('`, `')'`, or **empty**.
So the question is: *is there **some** way to choose for each `*` so the string is balanced?*

---

### Hint 2 — Think in ranges, not exact counts

As you scan left→right, instead of tracking a single “open count,” track a **range**:

* `minOpen` = the **fewest** opens you could have (if you spend `*` as `')'`/empty when possible)
* `maxOpen` = the **most** opens you could have (if you spend `*` as `'('`)

For each char:

* `'('` → `minOpen++`, `maxOpen++`
* `')'` → `minOpen--`, `maxOpen--`
* `'*'` → `minOpen--`, `maxOpen++`  (because `*` could be `')'` or `'('`)

But `minOpen` can’t go below 0: clamp `minOpen = max(0, minOpen)`.

---

### Hint 3 — Immediate failure condition

If ever `maxOpen < 0`, you’ve seen too many `')'` (even treating every prior `*` as `'(''`).
→ Impossible → return false right away.

---

### Hint 4 — Final success condition

After you finish scanning, you need to be able to close everything:
That’s possible **iff** `minOpen == 0`.
(If `minOpen > 0`, even treating all remaining `*` as `')'` wasn’t enough to close opens.)

---

### Hint 5 — Tiny walkthroughs to cement it

**Example A:** `s = "(*))"`
Start `min=0, max=0`

* `'('` → `min=1, max=1`
* `'*'` → `min=0, max=2`   (clamp min to 0)
* `')'` → `min=-1→0, max=1`
* `')'` → `min=-1→0, max=0`
  End: `min==0` → **true**.

**Example B:** `s = ")*("`

* `')'` → `min=-1→0, max=-1` → `max<0` → **false** immediately.

---

### Hint 6 — Common pitfalls

* Don’t let `minOpen` go negative (always clamp to 0).
* You **must** check `maxOpen < 0` during the scan, not just at the end.
* This is **existential** (“is there some assignment?”), which is why a range works.

---

### Hint 7 — Alternatives (if you want other angles)

* **DP (O(n²))**: `dp[i][j] = can substring i..j be valid` with transitions on `(`, `)`, `*`. Works but slower.
* **Two-stacks** (indices): one stack for `'('`, one for `'*'`. First match `')'` with `'('`, else with `'*'`. Then match remaining `'('` with later `'*'` (order matters). Also O(n).
*/
class Solution {
    public boolean checkValidString(String s) {
        int minOpen = 0;
        int maxOpen = 0;

        for(int i=0; i<s.length(); i++){
            if(s.charAt(i) == '('){
                minOpen++;
                maxOpen++;
            }else if(s.charAt(i) == ')'){
                minOpen--;
                maxOpen--;
            }else{                      // '*'
                minOpen--;
                maxOpen++;
            }

            minOpen = Math.max(0, minOpen);    // clamp

            if(maxOpen < 0){     // too many ')'
                return false;
            }
        }

        return (minOpen == 0);
    }
}





// Method 2: Backtracking / DFS
/*
# Core idea (backtracking)

Treat each `'*'` as a branch with **three choices**:

* `'*' → '('`
* `'*' → ')'`
* `'*' → ''`  (empty)

Scan left→right with a function `dfs(i, open)`:

* `i` = current index in `s`
* `open` = how many `'('` are currently unmatched

Rules:

* If `open < 0`, we already have more `')'` than `'('` → **invalid**.
* If `i == n` (end of string), the string is valid **iff** `open == 0`.
* Otherwise, consume `s[i]`:

  * `'('` → `dfs(i+1, open+1)`
  * `')'` → `dfs(i+1, open-1)`
  * `'*'` → try **any of**:

    * empty: `dfs(i+1, open)`
    * `'('`: `dfs(i+1, open+1)`
    * `')'`: `dfs(i+1, open-1)`

This naïvely is `O(3^k)` where `k` is # of `'*'`. We’ll prune + memoize.

---

# Pruning & memoization (turns it into O(n²))

**Prune 1 — Negative opens:**
If `open < 0` → impossible → return `false`.

**Prune 2 — Too many opens to close:**
Remaining characters = `rem = n - i`.
Each remaining position can reduce `open` by **at most 1** (if we use it as `')'`).
So if `open > rem`, we can’t possibly close them all → return `false`.

> This bound is safe and very effective.

**Memoization:**
Cache states `(i, open) → true/false`.
Then each state is solved once; there are at most `O(n²)` states (`open` ranges `0..n`).


**Why this works:** we explore all legal assignments of `'*'`, but the two prunes cut off hopeless branches early, and the memo prevents recomputation.

---

# Thorough example walkthroughs

## Example 1: `s = "(*))"` → **true**

We’ll show just enough of the search to see the logic.

* Start: `i=0, open=0`, `s[0]='('` → must take `'('`
  → `i=1, open=1`

* `s[1]='*'`: try options in order (empty, `'('`, `')'`)

  **Option A (empty):**

  * `i=2, open=1`, `s[2]=')'` → `open-1=0`
    → `i=3, open=0`, `s[3]=')'` → `open-1=-1` → **prune** (negative)

  **Option B ('*' as '('):**

  * `i=2, open=2`, `s[2]=')'` → `open-1=1`
    → `i=3, open=1`, `s[3]=')'` → `open-1=0`
    → `i=4 == n`, `open==0` → **valid** → return `true`.

Backtracking finds a valid assignment: `"( ( ) )"` (with `*` as `'('`).

Prunes used:

* Negative `open` pruned option A early.
* We didn’t need the `open > rem` prune in this path, but it helps in harder strings.

---

## Example 2: `s = ")*("` → **false**

* Start: `i=0, open=0`, `s[0]=')'` → `open-1=-1` → **prune immediately**
  No possible assignment for `'*'` because there is none; return `false`.

(If there were `'*'`, the `max-close` prune would also help in other positions.)

---

## Example 3: `s = "(*()*)"` → **true**

One possible assignment:

* `(` → open=1
* `*` as `(` → open=2
* `(` → open=3
* `)` → open=2
* `*` as `)` → open=1
* `)` → open=0

DFS will discover a path like the above; prunes keep it fast:

* `open` never negative,
* `open` never exceeds `rem` (the remaining capacity to close).

---

# Notes & comparisons

* **Backtracking vs Greedy-range:**
  Greedy-range is O(n)/O(1) and is the interview-favorite.
  Backtracking is conceptually simple and, with memo+prune, still efficient (O(n²) states).
* **Two-stacks method:**
  Also O(n), tracks indices of `'('` and `'*'`. Not backtracking, but good to know.
*/

// class Solution {
//     public boolean checkValidString(String s) {
//         int n = s.length();
//         Boolean[][] memo = new Boolean[n + 1][n + 1]; // memo[i][open]
//         return dfs(0, 0, s, memo);
//     }

//     private boolean dfs(int i, int open, String s, Boolean[][] memo) {
//         int n = s.length();

//         // Prune: too many ')' used
//         if (open < 0) return false;

//         // Prune: even if all remaining chars were ')', we can't close all opens
//         int rem = n - i;
//         if (open > rem) return false;

//         if (i == n) return open == 0;

//         if (memo[i][open] != null) return memo[i][open];

//         char c = s.charAt(i);
//         boolean ok;

//         if (c == '(') {
//             ok = dfs(i + 1, open + 1, s, memo);
//         } else if (c == ')') {
//             ok = dfs(i + 1, open - 1, s, memo);
//         } else { // '*'
//             // 1) treat as empty
//             if (dfs(i + 1, open, s, memo)) ok = true;
//             // 2) treat as '('
//             else if (dfs(i + 1, open + 1, s, memo)) ok = true;
//             // 3) treat as ')'
//             else ok = dfs(i + 1, open - 1, s, memo);
//         }

//         memo[i][open] = ok;
//         return ok;
//     }
// }






// Method 3: DP method
/*
Totally—let’s make the **prefix DP** idea super down-to-earth and then walk it through carefully.

---

# The core picture (in plain terms)

* Read the string **left → right**.
* At any point, we just need to know: **how many “(” are currently open and not yet closed?**
  Call that number `open`.

Because `*` can be `'('`, `')'`, or empty, there may be **many** possible `open` values after reading a prefix.
So we track a **set of possible `open` counts** after each position.

* Start with `{0}` (nothing read, nothing open).
* For each character:

  * `'('` → every `open` becomes `open+1`
  * `')'` → every `open>0` can become `open-1` (can’t go below 0)
  * `'*'` → each `open` can become:

    * `open` (treat `*` as empty),
    * `open+1` (treat `*` as `'('`),
    * `open-1` if `open>0` (treat `*` as `')'`)

At the end, if `0` is still in the set, we can assign the `*`s in some way that balances the string.

This is exactly what the DP does—just with a boolean array instead of a set.

---

# The DP state (one simple line)

Let `curr[k] = true` mean: “after reading up to here, it’s possible to have exactly `k` opens.”

* Initialize: `curr[0] = true`.
* For each character, build a new `next[]` from `curr[]` using the rules above.
* Move on: `curr = next`.
* Return `curr[0]` at the end.

Time is **O(n²)** in the worst case (because `open` can range up to `n`), space **O(n)**.

> Micro-optimizations (optional):
>
> * You can clamp the loop range: after `i` chars, `open` can’t exceed `i` and can’t be negative.
> * You can early-exit if a step produces no `true` entries.

---

# Detailed walkthrough (valid case)

Let’s trace `s = "(*()*)"` (answer: **true**).
We’ll keep the set of possible `open` counts after each char.

Start: `{0}`

1. `'('`

* From `0` → must open → `1`
  Set: `{1}`

2. `'*'`
   From `1`, three options:

* empty: `1`
* `'('`: `2`
* `')'`: `0`
  Set: `{0, 1, 2}`

3. `'('`
   Add one to each:

* `0 → 1`, `1 → 2`, `2 → 3`
  Set: `{1, 2, 3}`

4. `')'`
   Close one (only if >0):

* `1 → 0`, `2 → 1`, `3 → 2`
  Set: `{0, 1, 2}`

5. `'*'`
   From each `open` in `{0,1,2}`:

* Treat as empty: `{0,1,2}`
* Treat as `'('`: `{1,2,3}`
* Treat as `')'`: `{(0→none), (1→0), (2→1)}` = `{0,1}`
  Union → `{0,1,2,3}`

6. `')'`
   Close one (if >0):

* `0 → (invalid)`, `1 → 0`, `2 → 1`, `3 → 2`
  Set: `{0, 1, 2}`

End: `0` is in the set → **valid**.

A concrete assignment that works here:

* s = `( * ( ) * )`
* choose: `*` at pos 2 = `'('`, `*` at pos 5 = `')'`
* becomes `((()) )` → balanced.

---

# Another detailed walkthrough (invalid case)

`s = ")*("` (answer: **false**)

Start: `{0}`

1. `')'`
   Must close one, but we have `0` → no transitions → `{}` (empty set)
   We’re done; no way forward → **false**.

---

# Why this DP is correct (simple view)

We’re *exhaustively* tracking every possible number of unmatched `'('` you could have after each prefix, considering all choices for `'*'`. If **any** path can lead to a balanced string, `0` will still be possible at the end. If not, it won’t.

---

# (Optional) Tightening the loops a bit

* After reading `i` characters, `open` cannot exceed `i` (you can’t have more opens than characters read).
* Also, if `open > n - i` at any point, it’s impossible to close them with remaining characters; you can skip such `open`. (This is the same prune you’d use in DFS.)

These bounds cut constant factors but don’t change the idea.
*/
// class Solution {
//     public boolean checkValidString(String s) {
//         int n = s.length();
//         // curr[k] = reachable open-count k after processing current prefix
//         boolean[] curr = new boolean[n + 1];
//         curr[0] = true;

//         for (int idx = 0; idx < n; idx++) {
//             char c = s.charAt(idx);
//             boolean[] next = new boolean[n + 1];

//             for (int open = 0; open <= n; open++) {
//                 if (!curr[open]) continue;

//                 if (c == '(') {
//                     if (open + 1 <= n) next[open + 1] = true;
//                 } else if (c == ')') {
//                     if (open > 0) next[open - 1] = true;
//                 } else { // '*'
//                     // empty
//                     next[open] = true;
//                     // '('
//                     if (open + 1 <= n) next[open + 1] = true;
//                     // ')'
//                     if (open > 0) next[open - 1] = true;
//                 }
//             }

//             curr = next;
//         }

//         return curr[0];
//     }
// }





// Method 4: Stack Based Solution
/*
# Idea in one line

Scan left→right and remember **indices** of:

* `'('` in one stack, and
* `'*'` in another stack.

When you see a `')'`, try to match it with a previous `'('` first; if none, use a previous `'*'` as `')'`.
After the scan, any leftover `'('` must be matched by `'*'` that appear **after** them (so those `'*'` can act as `')'`). If you can do that for all, it’s valid.

Why it works:

* Matching `')'` greedily with `'('` first preserves `'*'` flexibility.
* Order matters: a `'*'` can only serve as a `')'` for a `'('` **to its left** (i.e., `starIndex > leftIndex`).

---

# Algorithm (greedy with two stacks)

1. Initialize two stacks of indices:

   * `left`: positions of `'('`
   * `star`: positions of `'*'`

2. For each character `s[i]`:

   * If `'('`: push `i` to `left`
   * If `'*'`: push `i` to `star`
   * If `')'`:

     * If `left` nonempty: pop one `'('` (best match)
     * Else if `star` nonempty: pop one `'*'` (treat it as `')'`)
     * Else: no thing to match → **invalid** (return `false`)

3. After the pass, you may still have extra `'('` in `left`. Try to match each with a later `'*'`:

   * While both stacks nonempty:

     * If `left.peek() < star.peek()`: pop both (use that `'*'` as `')'`)
     * Else (`left.peek() > star.peek()`): the nearest `'*'` is **before** this `'('` → can’t match → **invalid**
   * If any `'('` remain → **invalid**, else **valid**.

**Time:** O(n)
**Space:** O(n)

# Detailed walkthroughs

## Walkthrough 1 — Valid: `s = "(*))"`

Indices: `0:'(', 1:'*', 2:')', 3:')'`

Stacks shown as top→bottom.

**Scan:**

* i=0 `'('` → left: `[0]`, star: `[]`
* i=1 `'*'` → left: `[0]`, star: `[1]`
* i=2 `')'` → pop from `left` → left: `[]`, star: `[1]`
* i=3 `')'` → `left` empty, pop from `star` → left: `[]`, star: `[]`

**Post-processing:**

* `left` empty → **valid** (no unmatched `'('` left)

One valid assignment: treat `*` as `'('`: string becomes `"(())"`.

---

## Walkthrough 2 — Valid with leftovers matched by later stars: `s = "(*()*)"`

Indices: `0:'(', 1:'*', 2:'(', 3:')', 4:'*', 5:')'`

**Scan:**

* i=0 `'('` → left: `[0]`, star: `[]`
* i=1 `'*'` → left: `[0]`, star: `[1]`
* i=2 `'('` → left: `[2, 0]`, star: `[1]`
* i=3 `')'` → pop `left` → left: `[0]`, star: `[1]`
* i=4 `'*'` → left: `[0]`, star: `[4, 1]`
* i=5 `')'` → pop `left` → left: `[]`, star: `[4, 1]`

**Post-processing:**

* `left` is empty already → **valid**.

(Here we matched all `')'` with `'('` directly; stars are unused or can be empty.)

---

## Walkthrough 3 — Invalid due to order: `s = "*)("`

Indices: `0:'*', 1:')', 2:'('`

**Scan:**

* i=0 `'*'` → left: `[]`, star: `[0]`
* i=1 `')'` → `left` empty, pop from `star` → left: `[]`, star: `[]`
* i=2 `'('` → push → left: `[2]`, star: `[]`

**Post-processing:**

* Try to match remaining `'('` at index 2 with a later `'*'`: but `star` is empty → **invalid**.

(We used the only `'*'` earlier to cover `')'`, leaving a `'('` at the end with no later `'*'` to act as `')'`.)

---

## Walkthrough 4 — Invalid because nearest `'*'` is before `'('`: `s = ")*("`

Indices: `0:')', 1:'*', 2:'('`

**Scan:**

* i=0 `')'` → no `left`/`star` to match → **immediate false** (too many early closers)

(If you’d forced the scan to continue: you’d end with `left=[2]`, `star=[1]`; in post-processing `left.peek()=2` > `star.peek()=1` ⇒ the star is before the `'(''`, can’t match → **false**.)

---

# Why prefer stacks here?

* Simple, local decisions (match as you go).
* Clear “order” check at the end: `starIndex` must exceed `leftIndex` to act as a `')'`.
* Same O(n) time as the “range” greedy; a bit more state but very intuitive.
*/

// class Solution {
//     public boolean checkValidString(String s) {
//         Deque<Integer> left = new ArrayDeque<>(); // indices of '('
//         Deque<Integer> star = new ArrayDeque<>(); // indices of '*'

//         for (int i = 0; i < s.length(); i++) {
//             char c = s.charAt(i);
//             if (c == '(') {
//                 left.push(i);
//             } else if (c == '*') {
//                 star.push(i);
//             } else { // c == ')'
//                 if (!left.isEmpty()) {
//                     left.pop();        // use a '(' first
//                 } else if (!star.isEmpty()) {
//                     star.pop();        // use a '*' as ')'
//                 } else {
//                     return false;      // nothing to match ')'
//                 }
//             }
//         }

//         // Match remaining '(' with later '*' acting as ')'
//         while (!left.isEmpty() && !star.isEmpty()) {
//             if (left.peek() < star.peek()) {
//                 left.pop();
//                 star.pop();
//             } else {
//                 // the nearest '*' is before this '(' → can't act as ')'
//                 return false;
//             }
//         }

//         return left.isEmpty();
//     }
// }

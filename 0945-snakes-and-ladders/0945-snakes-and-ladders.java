// Graph BFS (BFS in graph finds shortest distance)
/*
Notes:
Instead of "Pair" a tiny helper class can be used (e.g., State { int pos; int dist; }), or an int[], or parallel queues.

Visited should be by final landing square (after snake/ladder).



## 1) Reframe the problem

* Think of each square `1..N²` as a **node** in a graph.
* From any node `u`, you can roll a die `1..6` → edges to `u+1, u+2, …, u+6` (bounded by `N²`).
* **Snakes/Ladders**: if a landing square `v` has a snake/ladder to `w` (board cell has a number ≠ -1), the move ends at **`w`**, not `v`.
* You want the **minimum number of moves** from `1` to `N²` → that’s a classic **BFS** on an unweighted graph.

---

## 2) The tricky part: coordinates ↔ label mapping

* The board labels are **serpentine** starting from the **bottom row**:

  * Bottom row (last row index): `1..N` left→right
  * Next row up: `N+1..2N` **right→left**
  * Alternating direction as you go up.
* You’ll need a function to convert a **label** `k (1..N²)` to `(row, col)`:

  * Compute `rowFromBottom = (k-1) / N` and `colInRow = (k-1) % N`.
  * Real row index: `row = N-1 - rowFromBottom` (because 0 is top).
  * If `rowFromBottom` is **even** → left→right: `col = colInRow`
  * If **odd** → right→left: `col = N-1 - colInRow`
* Similarly, when you read `board[row][col]`, a value `-1` means “no snake/ladder here”; otherwise it gives the **destination label**.

> Hint: Write and test this mapping with a tiny board (e.g., `N=3`) to make sure you don’t mix up the zig-zag.

---

## 3) BFS scaffolding (no code, just plan)

* Queue stores **(label, movesSoFar)**.
* Start from `(1, 0)`.
* While queue not empty:

  * Pop `u`.
  * For each roll `r` in `1..6`:

    * `v = min(u + r, N²)`
    * Convert `v` to `(row,col)` and check the board:

      * If board has destination `w != -1`, set `v = w`.
    * If `v` is not **visited**, mark visited and push `(v, movesSoFar + 1)`.
    * If `v == N²`, you can return `movesSoFar + 1` early.
* If BFS ends without reaching `N²`, answer is `-1`.

---

## 4) Visiting policy

* Mark **visited by label** (1..N²), **after** applying snake/ladder jump.
* Avoid revisiting nodes to prevent cycles (snakes could point down).

---

## 5) Edge cases to think through

* `N=1` → already at target (`1 == N²`) → `0` moves.
* Snakes/ladders **on the path of the initial row**.
* Multiple snakes/ladders chained? (Per problem, you **only** follow the one on the **landing** square, not further chaining in the same move.)
* Landing on a square that jumps **backwards** (snake) — ensure BFS still handles correctly.
* Dice overshoot: clamp at `N²`.

---

## 6) Testing your mapping

Before BFS, sanity-check your `label → (row,col)` helper:

* For `N=3`, labels should map like:

  * Row 2 (bottom): `1 2 3`
  * Row 1:          `6 5 4`
  * Row 0 (top):    `7 8 9`
* Verify a few conversions both ways (at least `1`, `N`, `N+1`, `N²`).

---

## 7) Complexity

* At most `N²` nodes, each with up to 6 edges → **O(N²)** time.
* Visited array of size `N²+1` → **O(N²)** space.

---

## 8) Common pitfalls

* Off-by-one in label math (`(k-1) / N`, `(k-1) % N` is safer).
* Forgetting the serpentine direction flip every row.
* Marking visited **before** applying snake/ladder (do it **after** you jump).
* Trying to chain multiple jumps in a single die roll (don’t).


Why this works:
BFS guarantees the first time you reach a square (after applying snake/ladder) is with the minimum number of rolls.
The serpentine mapping converts labels ↔ board indices correctly:
bottom row left→right when rowFromBottom is even,
next row right→left when odd.

Quick sanity checks:
n=1 → start equals goal → returns 0.
If a die roll lands on a ladder, you jump once (no chaining).
Overshoots clamp at goal.
visited prevents cycles (e.g., snakes pointing back).
*/

class Solution {
    public int snakesAndLadders(int[][] board) {

        int n = board.length;
        int goal = n*n;
                
        HashSet<Integer> visited = new HashSet<>();
        Deque<Pair<Integer, Integer>> q = new ArrayDeque<>();
        q.offer(new Pair<>(1, 0));
        visited.add(1);


        while(!q.isEmpty()){
            Pair<Integer, Integer> cur = q.poll();
            int currNode = cur.getKey();
            int diceRolls = cur.getValue();


            if(currNode == goal){
                return diceRolls;
            }

            for(int i=1; i<=6; i++){
                int v = Math.min(currNode + i, goal);
                int[] coord = getCoord(v, n);
                if(board[coord[0]][coord[1]] != -1){
                    v = board[coord[0]][coord[1]];
                }
                if(!visited.contains(v)){
                    visited.add(v);
                    q.offer(new Pair<>(v, diceRolls + 1));
                }
            }
        }

        return -1;
    }

    private int[] getCoord(int k, int n){
        int rowFromBottom = (k-1) / n;
        int colInRow = (k-1) % n;

        int row = n-1 - rowFromBottom;
        int col;

        if(rowFromBottom % 2 == 0){
            col = colInRow;
        }else{
            col = n-1 - colInRow;
        }

        return new int[]{row, col};
    }
}
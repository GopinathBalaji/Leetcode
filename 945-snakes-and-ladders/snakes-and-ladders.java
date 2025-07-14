// BFS (Level order Graph traversal)
/*

## \U0001f4cc Part 1: **Thought Process Behind the Problem (Snakes and Ladders)**

This is a **shortest path problem** on a **weirdly-shaped board**.

---

### \U0001f3af Objective:

Go from **square 1 to square $n \times n$** in **minimum number of moves**, moving only by rolling a die (1 to 6 steps), and accounting for **snakes and ladders**.

---

### \U0001f9e0 Key Ideas:

#### 1. **Model the board as a graph**

* Each square on the board is a **node**.
* You can move from a node to up to 6 other nodes (by rolling 1–6).
* If you land on a square with a snake/ladder (`board[row][col] != -1`), it **redirects you** to another square.

➡️ This is a classic case of a **graph traversal** where:

* Nodes = board squares (1 to n²),
* Edges = dice rolls + snake/ladder jumps.

#### 2. **Find the shortest path**

* We're looking for the **minimum number of dice rolls** to reach the final square.
* Since all moves have equal "cost", use **BFS (Breadth-First Search)**.

Why BFS? Because:

* It explores all squares reachable in 1 move, then in 2 moves, etc.
* First time you reach the goal = shortest path.

#### 3. **Convert board structure**

The board is in **2D**, but you must navigate it **linearly** (from square 1 to n²), and the square numbers follow a **zigzag** pattern.

---

## \U0001f5fa️ Visualization of Board Numbering (n=6)

```
36 35 34 33 32 31  ← row 0
25 26 27 28 29 30  → row 1
24 23 22 21 20 19  ← row 2
13 14 15 16 17 18  → row 3
12 11 10  9  8  7  ← row 4
 1  2  3  4  5  6  → row 5
```

* Square numbers go **left-to-right** on even-numbered rows **from the bottom**, and **right-to-left** on odd-numbered rows.
* So, to access `board[row][col]`, you must map the square number to `(row, col)` correctly.

---

## ✅ Part 2: **Detailed Explanation of the Code**

### \U0001f9e9 Coordinates Helper Method

```java
public int[] coordinates(int number, int n) {
    number = number - 1;
    int rowFromBottom = number / n;
    int row = n - 1 - rowFromBottom;

    int col;
    if (rowFromBottom % 2 == 0) {
        col = number % n; // left to right
    } else {
        col = n - 1 - (number % n); // right to left
    }

    return new int[]{row, col};
}
```

#### What this does:

* Given a square number (1 to n²), this returns its position on the board.
* Accounts for the zigzag layout by flipping `col` direction based on the row.

---

### \U0001f9ee Main BFS Loop

```java
public int snakesAndLadders(int[][] board) {
    int n = board.length;

    HashSet<Integer> visited = new HashSet<>();
    Queue<Integer> queue = new LinkedList<>();
    int count = 0;

    queue.add(1);
    visited.add(1);
```

* Start at square `1`, mark as visited.
* `count` is number of dice rolls (levels in BFS).

---

### \U0001f501 BFS Loop

```java
while (!queue.isEmpty()) {
    int size = queue.size();

    for (int i = 0; i < size; i++) {
        int curr = queue.poll();
```

* For all squares you can reach in `count` moves:

  * Poll them one by one.
  * For each, simulate rolling a die:

```java
        for (int move = 1; move <= 6; move++) {
            int next = curr + move;
            if (next > n * n) continue;
```

* From current square, try moving 1 to 6 steps ahead.
* Ignore if `next` is beyond the board.

---

### \U0001f3af Handling Snakes and Ladders

```java
            int[] coords = coordinates(next, n);
            int row = coords[0], col = coords[1];

            if (board[row][col] != -1) {
                next = board[row][col]; // snake or ladder
            }
```

* If you land on a snake or ladder (board value ≠ -1), jump to the destination square.

---

### ✅ Check if Reached End

```java
            if (next == n * n) return count + 1;
```

* If you're at the final square, return the number of moves it took to reach here.

---

### \U0001f4cd Visit and Enqueue

```java
            if (!visited.contains(next)) {
                visited.add(next);
                queue.add(next);
            }
        }
    }

    count++;
}
```

* Only visit each square once to avoid cycles or redundant paths.
* BFS processes all current-level nodes before incrementing `count`.

---

### ⛔ No Solution

```java
return -1;
```

If BFS completes and you never reach square `n²`, it means it’s **unreachable** — possibly due to the board layout.

---

## ✅ Summary of the Solution

| Part                       | Role                                              |
| -------------------------- | ------------------------------------------------- |
| `coordinates()`            | Converts square number to (row, col) on the board |
| `queue`                    | Tracks positions to explore using BFS             |
| `visited`                  | Avoids revisiting squares                         |
| `count`                    | Tracks number of dice rolls (levels in BFS)       |
| `if board[row][col] != -1` | Handles ladders/snakes                            |
| `if next == n*n`           | Checks for winning condition                      |

---

## \U0001f9e0 Final Thoughts

This is a fantastic example of:

* Converting a non-standard board to a linear structure.
* Using BFS for shortest path.
* Handling direction flips and jump logic elegantly.
*/
class Solution {
    public int snakesAndLadders(int[][] board) {
        int n = board.length;
        if (n == 1) return 0;

        int count = 0;
        HashSet<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();

        queue.add(1); // start from square 1
        visited.add(1); // mark as visited

        while (!queue.isEmpty()) {
            int size = queue.size(); // process all nodes at this level

            for (int i = 0; i < size; i++) {
                int curr = queue.poll();

                // Try all dice rolls from 1 to 6
                for (int move = 1; move <= 6; move++) {
                    int next = curr + move;
                    if (next > n * n) continue;  // don't go off board

                    int[] coords = coordinates(next, n);
                    int row = coords[0], col = coords[1];

                    // If there's a snake or ladder, jump to that square
                    if (board[row][col] != -1) {
                        next = board[row][col];
                    }

                    if (next == n * n) return count + 1;  // reached end

                    if (!visited.contains(next)) {
                        visited.add(next);
                        queue.add(next);
                    }
                }
            }

            count++;  // all squares reachable in 'count' moves
        }

        return -1;  // not reachable
    }

    public int[] coordinates(int number, int n) {
        number = number - 1;
        int rowFromBottom = number / n;
        int row = n - 1 - rowFromBottom;

        int col;
        if (rowFromBottom % 2 == 0) {
            col = number % n;
        } else {
            col = n - 1 - (number % n);
        }

        return new int[]{row, col};
    }
}
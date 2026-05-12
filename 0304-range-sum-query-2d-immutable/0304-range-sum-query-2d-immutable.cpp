// Method 1: Prefix Sum method
/*
Use these hints in order.

## Hint 1: Brute force idea

For each query:

```cpp
sumRegion(row1, col1, row2, col2)
```

You could loop through every cell inside the rectangle:

```text
for r from row1 to row2
    for c from col1 to col2
        add matrix[r][c]
```

This works, but if there are many queries, it becomes slow.

Time per query:

```text
O(number of cells in rectangle)
```

The goal is to make each query fast.

---

## Hint 2: Since the matrix is immutable

The word **immutable** is important.

It means the matrix does not change after construction.

So you can preprocess once in the constructor:

```cpp
NumMatrix(vector<vector<int>>& matrix)
```

Then answer each query quickly.

This suggests using **prefix sums**.

---

## Hint 3: Start with 1D prefix sum idea

For a 1D array:

```text
nums = [2, 4, 6, 8]
```

Build:

```text
prefix[i] = sum from index 0 to i - 1
```

So:

```text
prefix = [0, 2, 6, 12, 20]
```

Then sum from index `l` to `r` is:

```cpp
prefix[r + 1] - prefix[l]
```

For 2D, we use the same idea, but with rectangles.

---

## Hint 4: Build a 2D prefix sum matrix

Create another matrix:

```cpp
prefix
```

where:

```text
prefix[r][c]
```

means:

```text
sum of all elements in rectangle from (0, 0) to (r - 1, c - 1)
```

Notice the `r - 1` and `c - 1`.

So `prefix` should have one extra row and one extra column:

```cpp
vector<vector<int>> prefix(rows + 1, vector<int>(cols + 1, 0));
```

The extra row and column help avoid boundary checks.

---

## Hint 5: Prefix sum formula

For each matrix cell:

```cpp
matrix[r - 1][c - 1]
```

Fill:

```cpp
prefix[r][c] =
    matrix[r - 1][c - 1]
    + prefix[r - 1][c]
    + prefix[r][c - 1]
    - prefix[r - 1][c - 1];
```

Why subtract `prefix[r - 1][c - 1]`?

Because the top-left area is counted twice.

---

## Hint 6: Visual meaning

For a cell `(r, c)` in `prefix`:

```text
prefix[r][c] = sum of this area:

(0,0) ----------------
  |                  |
  |                  |
  |                  |
  ---------------- (r-1,c-1)
```

So it includes all original matrix cells:

```text
rows: 0 to r - 1
cols: 0 to c - 1
```

---

## Hint 7: Query formula

To get sum of rectangle:

```text
(row1, col1) to (row2, col2)
```

Use inclusion-exclusion:

```cpp
sum =
    prefix[row2 + 1][col2 + 1]
    - prefix[row1][col2 + 1]
    - prefix[row2 + 1][col1]
    + prefix[row1][col1];
```

---

## Hint 8: Why this formula works

Think of:

```cpp
prefix[row2 + 1][col2 + 1]
```

as the big rectangle from top-left to bottom-right.

It includes too much:

```text
1. Area above row1
2. Area left of col1
```

So subtract both:

```cpp
- prefix[row1][col2 + 1]
- prefix[row2 + 1][col1]
```

But the top-left corner area was subtracted twice, so add it back:

```cpp
+ prefix[row1][col1]
```

---

## Hint 9: Class design

You want:

```cpp
class NumMatrix {
private:
    vector<vector<int>> prefix;

public:
    NumMatrix(vector<vector<int>>& matrix) {
        // build prefix
    }

    int sumRegion(int row1, int col1, int row2, int col2) {
        // use formula
    }
};
```

---

## Hint 10: Complexity

Preprocessing:

```text
O(m * n)
```

Each query:

```text
O(1)
```

Extra space:

```text
O(m * n)
```

That is the main point of this problem: spend time once during construction, then answer every rectangle sum instantly.
*/
class NumMatrix {
private:
vector<vector<int>> prefix;

public:
    NumMatrix(vector<vector<int>>& matrix) {
        int rows = matrix.size();
        int cols = matrix[0].size();

        prefix.resize(rows+1, vector<int>(cols+1, 0));

        for(int r=1; r<=rows; r++){
            for(int c=1; c<=cols; c++){
                prefix[r][c] = matrix[r-1][c-1] + prefix[r-1][c] + prefix[r][c-1] - prefix[r-1][c-1];
            }
        }
    }
    
    int sumRegion(int row1, int col1, int row2, int col2) {
        int sum = 0;
        
        sum = prefix[row2 + 1][col2 + 1] - prefix[row1][col2 + 1] - prefix[row2 + 1][col1] + prefix[row1][col1];

        return sum;
    }
};

/**
 * Your NumMatrix object will be instantiated and called as such:
 * NumMatrix* obj = new NumMatrix(matrix);
 * int param_1 = obj->sumRegion(row1,col1,row2,col2);
 */

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/leethub-v4/bcilpkkbokcopmabingnndookdogmbna
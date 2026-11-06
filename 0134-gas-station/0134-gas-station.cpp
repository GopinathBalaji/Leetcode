// Method 1: Greedy
/*
## LeetCode 134: Gas Station (C++)

**Approach: Greedy | Difficulty: Medium**

### Hint 1: Think Greedy

You need to find a starting gas station from which you can complete the entire circular route.

At each station, calculate the net gas:

```cpp
int diff = gas[i] - cost[i];
```

### Hint 2: Check Total Gas

If the total gas is less than the total cost, completing the circuit is impossible.

```cpp
if (totalGas < totalCost)
    return -1;
```

### Hint 3: Track 3 Variables

```cpp
int total = 0;
int tank = 0;
int start = 0;
```

- `total`: Net gas across all stations.
- `tank`: Gas remaining from the current starting station.
- `start`: Candidate starting index.

### Hint 4: Reset When Tank Is Negative

If `tank < 0`, you cannot reach the next station from the current starting point.

Move your starting position to `i + 1`.

```cpp
if (tank < 0) {
    start = i + 1;
    tank = 0;
}
```

**Why?** If starting at `start` fails at index `i`, no station between `start` and `i` can be a valid starting point.

### Hint 5: Final Answer

If the total net gas is nonnegative, the greedy starting index is valid.

```cpp
return total >= 0 ? start : -1;
```

---

### Complete C++ Solution

```cpp
class Solution {
public:
    int canCompleteCircuit(vector<int>& gas, vector<int>& cost) {
        int total = 0;
        int tank = 0;
        int start = 0;

        for (int i = 0; i < gas.size(); i++) {
            int diff = gas[i] - cost[i];

            total += diff;
            tank += diff;

            if (tank < 0) {
                start = i + 1;
                tank = 0;
            }
        }

        return total >= 0 ? start : -1;
    }
};
```

### Complexity

- **Time:** O(n) — Single traversal.
- **Space:** O(1) — Constant extra space.

### Example

```cpp
gas  = [1, 2, 3, 4, 5]
cost = [3, 4, 5, 1, 2]
```

Net gas:

```cpp
diff = [-2, -2, -2, 3, 3]
```

Starting at index `3`:

- Station 3 → Tank = 3
- Station 4 → Tank = 6
- Station 0 → Tank = 4
- Station 1 → Tank = 2
- Station 2 → Tank = 0

**Answer: 3**

### Key Intuition

**Greedy + Total Sum = O(n)**

Two important observations:

1. If `sum(gas) < sum(cost)`, no solution exists.
2. If your tank becomes negative, skip all stations between your current starting point and the failure point.

**Interview tip:** The trick is understanding why resetting `start = i + 1` is safe. Once you prove that, the greedy solution becomes straightforward.
*/
class Solution {
public:
    int canCompleteCircuit(vector<int>& gas, vector<int>& cost) {
        int total = 0;
        int tank = 0;
        int start = 0;

        for(int i=0; i<gas.size(); i++){
            int diff = gas[i] - cost[i];

            total += diff;
            tank += diff;

            if(tank < 0){
                start = i + 1;
                tank = 0;
            }
        }

        return total >= 0 ? start : -1;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna
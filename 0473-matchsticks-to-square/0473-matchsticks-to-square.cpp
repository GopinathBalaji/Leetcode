// Method 1: Backtracking + Pruning problem
/*
This is a **backtracking + pruning** problem. The main idea is to assign each matchstick to one of the 4 sides.

### Hint 1: Check whether a square is even possible

Let:

```cpp
int total = accumulate(matchsticks.begin(), matchsticks.end(), 0);
```

A square has 4 equal sides, so:

```text
total % 4 must be 0
```

The target length of each side is:

```cpp
int target = total / 4;
```

If any matchstick is longer than `target`, you can also immediately return `false`.

### Hint 2: Track the current length of all 4 sides

A useful state is:

```cpp
vector<int> sides(4, 0);
```

Then your backtracking function can process one matchstick at a time:

```cpp
backtrack(matchsticks, index, sides, target)
```

For `matchsticks[index]`, try placing it on each of the four sides.

### Hint 3: Don't let a side exceed the target

Before placing a stick:

```cpp
if (sides[i] + matchsticks[index] > target) {
    continue;
}
```

Otherwise:

```cpp
sides[i] += matchsticks[index];

// recurse

sides[i] -= matchsticks[index];
```

That last line is your backtracking step.

### Hint 4: Base case

Once you've placed every matchstick:

```cpp
if (index == matchsticks.size()) {
    // did we successfully build the square?
}
```

If you've enforced that no side ever exceeds `target`, think about what condition you need here.

You can check all four sides, or notice that the total sum already guarantees something useful if the first three sides are correct.

### Hint 5: Sort in descending order first

This is an important optimization:

```cpp
sort(matchsticks.rbegin(), matchsticks.rend());
```

Why?

Because trying large matchsticks first makes impossible branches fail much sooner.

Compare trying:

```text
10, 9, 8, 2, 1
```

versus:

```text
1, 2, 8, 9, 10
```

The large sticks constrain your choices much more.

### Hint 6: Avoid equivalent branches

Suppose all four sides are currently:

```text
[0, 0, 0, 0]
```

Trying the same matchstick on side 0, side 1, side 2, and side 3 is effectively the same situation.

So if you try putting a stick onto an empty side and that branch fails, you don't need to try the other empty sides.

A useful pruning idea is:

```cpp
if (sides[i] == 0) {
    break;
}
```

after undoing the failed choice.

### Skeleton

```cpp
bool backtrack(vector<int>& sticks,
               int index,
               vector<int>& sides,
               int target) {

    if (index == sticks.size()) {
        // success condition
    }

    for (int i = 0; i < 4; i++) {

        if (sides[i] + sticks[index] > target) {
            continue;
        }

        sides[i] += sticks[index];

        if (backtrack(sticks, index + 1, sides, target)) {
            return true;
        }

        sides[i] -= sticks[index];

        // possible symmetry pruning
    }

    return false;
}
```

The main mental model is:

```text
Take current matchstick
→ try side 1
→ try side 2
→ try side 3
→ try side 4
→ backtrack whenever a side becomes impossible
```

The two most important optimizations are **sorting descending** and **skipping symmetric side assignments**.
*/
class Solution {
private:
    bool backtrack(vector<int>& matchsticks, int index, vector<int>& sides, int target){
        if(index == matchsticks.size()){
            return true;
        }

        for(int i=0; i<4; i++){
            if(sides[i] + matchsticks[index] > target){
                continue;
            }

            sides[i] += matchsticks[index];

            if(backtrack(matchsticks, index + 1, sides, target)){
                return true;
            }

            sides[i] -= matchsticks[index];

            // Pruning
            if(sides[i] == 0){
                break;
            }
        }

        return false;
    }


public:
    bool makesquare(vector<int>& matchsticks) {
        int total = std::accumulate(matchsticks.begin(), matchsticks.end(), 0);

        if(total % 4 != 0){
            return false;
        }

        int target = total / 4;
        vector<int> sides(4, 0);

        std::sort(matchsticks.begin(), matchsticks.end());

        return backtrack(matchsticks, 0, sides, target);
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna
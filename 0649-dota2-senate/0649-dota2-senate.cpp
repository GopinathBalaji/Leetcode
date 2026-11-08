// Method 1: Greedy
/*
## LeetCode 649: Dota2 Senate (C++)

**Approach: Greedy + Queue | Difficulty: Medium**

### Hint 1: Think Greedy

There are two parties:
- `R` → Radiant
- `D` → Dire

Each senator can ban one senator from the opposing party.

**Key idea:** The senator who gets to act first should ban the next available opponent.

### Hint 2: Use Two Queues

Store the indices of Radiant and Dire senators separately.

```cpp
queue<int> radiant, dire;

for (int i = 0; i < senate.size(); i++) {
    if (senate[i] == 'R')
        radiant.push(i);
    else
        dire.push(i);
}
```

### Hint 3: Compare Front Indices

The senator with the smaller index acts first and bans the opponent.

```cpp
int r = radiant.front();
int d = dire.front();

radiant.pop();
dire.pop();
```

### Hint 4: Winner Returns Next Round

The winning senator gets another turn in the next cycle.

Add `n` to their index to represent their next turn.

```cpp
if (r < d)
    radiant.push(r + n);
else
    dire.push(d + n);
```

**Why `+n`?** It simulates the circular order of the senate.

### Hint 5: Stop When One Queue Is Empty

Continue until one party has no senators remaining.

```cpp
while (!radiant.empty() && !dire.empty()) {
}
```

The party with remaining senators wins.

---

### Complete C++ Solution

```cpp
class Solution {
public:
    string predictPartyVictory(string senate) {
        int n = senate.size();

        queue<int> radiant, dire;

        for (int i = 0; i < n; i++) {
            if (senate[i] == 'R')
                radiant.push(i);
            else
                dire.push(i);
        }

        while (!radiant.empty() && !dire.empty()) {
            int r = radiant.front();
            int d = dire.front();

            radiant.pop();
            dire.pop();

            if (r < d)
                radiant.push(r + n);
            else
                dire.push(d + n);
        }

        return radiant.empty() ? "Dire" : "Radiant";
    }
};
```

### Complexity

- **Time:** O(n) — Each senator is eliminated at most once.
- **Space:** O(n) — Two queues store senator indices.

### Example

```cpp
senate = "RDD"
```

**Initial queues:**

```cpp
Radiant = [0]
Dire    = [1, 2]
```

**Round 1:**
- R(0) acts before D(1).
- R bans D(1).
- R returns at index `3`.

```cpp
Radiant = [3]
Dire    = [2]
```

**Round 2:**
- D(2) acts before R(3).
- D bans R(3).

```cpp
Radiant = []
Dire    = [5]
```

**Answer: `"Dire"`**

### Key Intuition

**Greedy + Queue = O(n)**

The senator who acts earlier gets to eliminate an opponent before that opponent can act.

Using `index + n` efficiently simulates future rounds without modifying the original string.

**Interview tip:** When a problem involves **circular turns, elimination, and repeated rounds**, consider using a queue to simulate the process efficiently.
*/
class Solution {
public:
    string predictPartyVictory(string senate) {
        int n = senate.size();

        queue<int> radiant, dire;

        for(int i=0; i<senate.size(); i++){
            if(senate[i] == 'R'){
                radiant.push(i);
            }else{
                dire.push(i);
            }
        }

        while(!radiant.empty() && !dire.empty()){
            int r = radiant.front();
            int d = dire.front();

            radiant.pop();
            dire.pop();

            if(r < d){
                radiant.push(r + n);
            }else{
                dire.push(d + n);
            }
        }

        return radiant.empty() ? "Dire" : "Radiant";
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna
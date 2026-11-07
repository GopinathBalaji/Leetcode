// Method 1: Greedy
/*
## LeetCode 846: Hand of Straights (C++)

**Approach: Greedy + Ordered Map | Difficulty: Medium**

### Hint 1: Think Greedy

You need to divide all cards into groups of size `groupSize`, where each group contains consecutive numbers.

**Key idea:** Always start a group with the smallest available card.

### Hint 2: Check Divisibility

If the total number of cards isn't divisible by `groupSize`, forming valid groups is impossible.

```cpp
if (hand.size() % groupSize != 0)
    return false;
```

### Hint 3: Use an Ordered Map

Store the frequency of each card using `map<int, int>`.

```cpp
map<int, int> freq;

for (int card : hand) {
    freq[card]++;
}
```

**Why `map`?** It automatically keeps cards sorted in ascending order.

### Hint 4: Build Consecutive Groups

Take the smallest available card and try to form a group of `groupSize` consecutive cards.

```cpp
int start = freq.begin()->first;

for (int i = 0; i < groupSize; i++) {
    int card = start + i;

    if (freq.find(card) == freq.end())
        return false;

    freq[card]--;

    if (freq[card] == 0)
        freq.erase(card);
}
```

### Hint 5: Repeat Until Empty

Keep forming groups until all cards are used.

```cpp
while (!freq.empty()) {
    int start = freq.begin()->first;
}
```

---

### Complete C++ Solution

```cpp
class Solution {
public:
    bool isNStraightHand(vector<int>& hand, int groupSize) {
        int n = hand.size();

        if (n % groupSize != 0)
            return false;

        map<int, int> freq;

        for (int card : hand) {
            freq[card]++;
        }

        while (!freq.empty()) {
            int start = freq.begin()->first;

            for (int i = 0; i < groupSize; i++) {
                int card = start + i;

                if (freq.find(card) == freq.end())
                    return false;

                freq[card]--;

                if (freq[card] == 0)
                    freq.erase(card);
            }
        }

        return true;
    }
};
```

### Complexity

- **Time:** O(n log n) — Map insertions, lookups, and deletions.
- **Space:** O(n) — Frequency map.

### Example

```cpp
hand = [1, 2, 3, 6, 2, 3, 4, 7, 8]
groupSize = 3
```

Sorted cards:

```cpp
[1, 2, 2, 3, 3, 4, 6, 7, 8]
```

Form groups:

- Group 1: `[1, 2, 3]`
- Group 2: `[2, 3, 4]`
- Group 3: `[6, 7, 8]`

**Answer: `true`**

### Key Intuition

**Greedy + Ordered Map = O(n log n)**

Always start with the smallest remaining card because it cannot be placed in a group starting with a larger card.

**Interview tip:** When a problem requires grouping elements into consecutive sequences, consider **sorting + greedy + frequency counting**.
*/
class Solution {
public:
    bool isNStraightHand(vector<int>& hand, int groupSize) {
        if(hand.size() % groupSize != 0){
            return false;
        }   

        map<int, int> freq;

        for(int card : hand){
            freq[card]++;
        }

        while(!freq.empty()){
            int start = freq.begin()->first;

            for(int i=0; i < groupSize; i++){
                int card = start + i;

                if(freq.find(card) == freq.end()){
                    return false;
                }

                freq[card]--;

                if(freq[card] == 0){
                    freq.erase(card);
                }
            }
        }

        return true;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna
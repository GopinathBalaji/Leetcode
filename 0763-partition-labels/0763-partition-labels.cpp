// Method 1: Greedy
/*
## LeetCode 763: Partition Labels (C++)

Approach: Greedy + Last Occurrence | Difficulty: Medium

### Hint 1: Think Greedy

Partition the string into as many parts as possible so that each character appears in at most one part.

Key idea: A partition cannot end until all characters encountered have their last occurrences inside it.

### Hint 2: Store Last Occurrences

Record the last index of each character.

```
vector<int> last(26);

for (int i = 0; i < s.size(); i++) {
    last[s[i] - 'a'] = i;
}
```

### Hint 3: Track Partition Boundaries

Maintain two variables:

```
int start = 0;
int end = 0;
```

- `start`: Beginning of the current partition.
- `end`: Farthest last occurrence of any character encountered.

### Hint 4: Expand the Partition

For every character, update the partition's ending index.

```
end = max(end, last[s[i] - 'a']);
```

Why? Every occurrence of the current character must belong to the same partition.

### Hint 5: Finalize a Partition

When `i == end`, the current partition is complete.

```
if (i == end) {
    result.push_back(end - start + 1);
    start = i + 1;
}
```

### Complete C++ Solution

```
class Solution {
public:
    vector<int> partitionLabels(string s) {
        vector<int> last(26, 0);

        for (int i = 0; i < s.size(); i++) {
            last[s[i] - 'a'] = i;
        }

        vector<int> result;
        int start = 0, end = 0;

        for (int i = 0; i < s.size(); i++) {
            end = max(end, last[s[i] - 'a']);

            if (i == end) {
                result.push_back(end - start + 1);
                start = i + 1;
            }
        }

        return result;
    }
};
```

### Complexity

- Time: O(n) — Two traversals of the string.
- Space: O(1) auxiliary space — Fixed array of 26 characters, excluding the output.

### Example

```
s = "ababcbacadefegdehijhklij"
```

Partitions:

```
ababcbaca | defegde | hijhklij
```

Lengths:

```
[9, 7, 8]
```

Answer: `[9, 7, 8]`

### Key Intuition

Greedy + Last Occurrence = O(n)

- Keep extending the partition until every character you've seen has its last occurrence within it.
- Once `i == end`, immediately close the partition to maximize the number of partitions.

Interview tip: When partitioning a string based on character occurrences, consider preprocessing the last occurrence of each character and greedily determining boundaries.
*/
class Solution {
public:
    vector<int> partitionLabels(string s) {
        vector<int> last(26);

        for(int i=0; i<s.size(); i++){
            last[s[i] - 'a'] = i;
        }

        vector<int> result;
        int start = 0;
        int end = 0;

        for(int i=0; i<s.size(); i++){
            end = std::max(end, last[s[i] - 'a']);

            if(i == end){
                result.push_back(end - start + 1);
                start = i + 1;
            }
        }

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna
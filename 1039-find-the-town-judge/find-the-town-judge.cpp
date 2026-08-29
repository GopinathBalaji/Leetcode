// Method 1: Considering indegree and outdegree
/*
Think of this as a **graph indegree/outdegree** problem.

The town judge must satisfy two conditions:

* They trust **nobody**
* **Everybody else** trusts them

### Hint 1: Track trust counts

For each person, track:

```text
outdegree = how many people they trust
indegree  = how many people trust them
```

If the judge is person `j`, then:

```text
outdegree[j] == 0
indegree[j] == n - 1
```

### Hint 2: Process each trust pair

For:

```cpp
[a, b]
```

it means:

```text
a trusts b
```

So:

```cpp
outdegree[a]++;
indegree[b]++;
```

Then scan people `1` through `n`.

### Hint 3: You can simplify to one score array

Instead of two arrays, use:

```cpp
vector<int> score(n + 1, 0);
```

For every:

```cpp
[a, b]
```

do:

```cpp
score[a]--; // a trusts someone
score[b]++; // b is trusted
```

Then ask:

> What score would the judge have?

They are trusted by `n - 1` people and trust nobody, so:

```text
score[judge] == n - 1
```

### Skeleton

```cpp
int findJudge(int n, vector<vector<int>>& trust) {
    vector<int> score(n + 1, 0);

    for (auto& t : trust) {
        int a = t[0];
        int b = t[1];

        // update a
        // update b
    }

    for (int person = 1; person <= n; person++) {
        // check judge condition
    }

    return -1;
}
```

One edge case to think about: `n = 1` and `trust` is empty. The only person should be the judge.
*/
class Solution {
public:
    int findJudge(int n, vector<vector<int>>& trust) {
        vector<int> score(n + 1, 0);

        for(int i=0; i<trust.size(); i++){
            int a = trust[i][0];
            int b = trust[i][1];

            score[a]--;
            score[b]++;
        }

        for(int person=1; person <= n; person++){
            if(score[person] == n-1){
                return person;
            }
        }

        return -1;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna
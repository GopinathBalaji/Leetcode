// Method 1: DSU + Prime Factorization
/*
This is a **DSU + Prime Factorization** problem.

The key idea is to avoid checking every pair of numbers directly. Two numbers are connected if they share a prime factor, so you can use each prime factor as a bridge between all numbers containing it.

### Hint 1: Reframe the graph

The problem says you can travel from index `i` to index `j` when:

```cpp
gcd(nums[i], nums[j]) > 1
```

That means the two numbers share at least one prime factor.

For example:

```text
6  = 2 × 3
15 = 3 × 5
```

They share factor `3`, so they are connected.

Instead of checking:

```text
every pair of indices
```

think:

```text
numbers sharing the same prime factor
→ belong to the same connected component
```

---

### Hint 2: Why not compare every pair?

A direct approach would do:

```cpp
for (int i = 0; i < n; i++) {
    for (int j = i + 1; j < n; j++) {
        if (gcd(nums[i], nums[j]) > 1) {
            // connect them
        }
    }
}
```

But this is roughly:

```text
O(n²)
```

which is too expensive.

You need to connect numbers through their **prime factors** instead.

---

### Hint 3: Use DSU for connectivity

Create a DSU over the indices:

```text
0, 1, 2, ..., n - 1
```

Whenever two indices share a prime factor:

```cpp
dsu.unite(i, j);
```

At the end, all indices should belong to one DSU component.

So your main question becomes:

```text
How can I efficiently find indices that share a prime factor?
```

---

### Hint 4: Factor each number into unique prime factors

For each:

```cpp
nums[i]
```

find its prime factors.

Example:

```text
nums[i] = 12
12 = 2² × 3
```

You only care about:

```text
2 and 3
```

not how many times they appear.

So conceptually:

```cpp
vector<int> factors = primeFactors(nums[i]);
```

---

### Hint 5: Remember the first index that used each prime

Use something like:

```cpp
unordered_map<int, int> factorOwner;
```

where:

```text
factorOwner[p] = some index containing prime p
```

Suppose you process:

```text
nums = [6, 10, 15]
```

For `6`:

```text
factors = {2, 3}

2 → index 0
3 → index 0
```

For `10`:

```text
factors = {2, 5}
```

Factor `2` was already seen at index `0`, so:

```cpp
dsu.unite(1, 0);
```

Then remember `5`.

For `15`:

```text
factors = {3, 5}
```

Both factors connect it to previous numbers.

Now all three indices belong to one component.

---

### Hint 6: You do not need to connect every pair sharing a factor

Suppose prime `2` appears in:

```text
index 0
index 4
index 7
index 10
```

You do **not** need:

```text
0 ↔ 4
0 ↔ 7
0 ↔ 10
4 ↔ 7
...
```

Just connect every new occurrence to one representative:

```text
0 ↔ 4
0 ↔ 7
0 ↔ 10
```

DSU handles the transitive connectivity.

So:

```cpp
if (factorOwner.count(p)) {
    dsu.unite(i, factorOwner[p]);
}
else {
    factorOwner[p] = i;
}
```

---

### Hint 7: Be careful with the number `1`

This is an important edge case.

For any number:

```cpp
gcd(1, x) == 1
```

So `1` cannot connect to any other number.

Therefore, if:

```cpp
nums.size() > 1
```

and some element is:

```cpp
1
```

the answer must be:

```cpp
false;
```

Example:

```text
[1, 2]
```

There is no valid traversal between the two indices.

But:

```text
[1]
```

is trivially valid because there is only one index.

---

### Hint 8: Simple prime factorization is enough

For a number:

```cpp
int x = nums[i];
```

try divisors:

```cpp
for (int p = 2; p * p <= x; p++)
```

If:

```cpp
x % p == 0
```

then `p` is a prime factor.

Process it once, then remove all copies:

```cpp
while (x % p == 0) {
    x /= p;
}
```

After the loop, if:

```cpp
x > 1
```

then `x` itself is one remaining prime factor.

For example:

```text
x = 30

2 divides 30
remove all 2s → 15

3 divides 15
remove all 3s → 5

remaining 5 > 1
→ factor 5
```

So the unique factors are:

```text
2, 3, 5
```

---

### Hint 9: Check whether everything belongs to one component

After processing all numbers and unioning indices that share factors, check:

```cpp
int root = dsu.find(0);
```

Then:

```cpp
for (int i = 1; i < n; i++) {
    if (dsu.find(i) != root) {
        return false;
    }
}
```

If every index has the same DSU root:

```cpp
return true;
```

---

### Hint 10: Why does this work even when two numbers don't directly share a factor?

Consider:

```text
6, 15, 35
```

Prime factors:

```text
6  → {2, 3}
15 → {3, 5}
35 → {5, 7}
```

Notice:

```cpp
gcd(6, 35) == 1
```

so there is no direct edge between them.

But:

```text
6 ↔ 15 ↔ 35
```

is a valid path.

The DSU captures exactly this kind of **transitive connectivity**.

---

### Skeleton

```cpp
class Solution {
private:
    class DSU {
        vector<int> parent;
        vector<int> rank;

    public:
        DSU(int n) {
            parent.resize(n);
            rank.resize(n, 0);

            for (int i = 0; i < n; i++) {
                parent[i] = i;
            }
        }

        int find(int x) {
            // path compression
        }

        void unite(int a, int b) {
            // union by rank / size
        }
    };

public:
    bool canTraverseAllPairs(vector<int>& nums) {
        int n = nums.size();

        if (n == 1) {
            return true;
        }

        // If any nums[i] == 1:
        // impossible

        DSU dsu(n);

        unordered_map<int, int> factorOwner;

        for (int i = 0; i < n; i++) {
            int x = nums[i];

            // factor x

            // for each unique prime factor p:
            //
            // if p was seen before:
            //     dsu.unite(i, factorOwner[p])
            //
            // otherwise:
            //     factorOwner[p] = i
        }

        // Check whether all indices
        // have the same DSU root

        return true;
    }
};
```

The core idea is:

```text
number
↓
prime factors
↓
connect indices sharing a prime factor
↓
DSU builds connected components
↓
all indices in one component?
```

The trickiest part is realizing that you **do not need to compute `gcd` for every pair**. Prime factors are the intermediate connection mechanism, and DSU handles all indirect paths automatically.
*/
class Solution {
private:
    class DSU {
    private:
        vector<int> parent;
        vector<int> rank;

    public:
        DSU(int n){
            parent.resize(n);
            rank.resize(n, 0);

            for(int i=0; i<n; i++){
                parent[i] = i;
            }
        }

        int find(int x){
            if(parent[x] == x){
                return x;
            }

            parent[x] = find(parent[x]);

            return parent[x];
        }

        void unite(int a, int b){
            int rootA = find(a);
            int rootB = find(b);

            if(rootA == rootB){
                return;
            }

            if(rank[rootA] < rank[rootB]){
                parent[rootA] = rootB;
            }else if(rank[rootB] < rank[rootA]){
                parent[rootB] = rootA;
            }else{
                parent[rootA] = rootB;
                rank[rootB]++;
            }
        }
    };

public:
    bool canTraverseAllPairs(vector<int>& nums) {
        int n = nums.size();

        if(n == 1){
            return true;
        }

        for(int i=0; i<n; i++){
            if(nums[i] == 1){
                return false;
            }
        }

        DSU dsu(n);

        unordered_map<int, int> factorOwner;

        for(int i=0; i<n; i++){
            int x = nums[i];

            for(int p=2; p * p <= x; p++){
                if(x % p == 0){

                    if(factorOwner.count(p)){
                        dsu.unite(i, factorOwner[p]);
                    }else{
                        factorOwner[p] = i;
                    }

                    while(x % p == 0){
                        x /= p;
                    }
                }
            }

            if(x > 1){
                if(factorOwner.count(x)){
                    dsu.unite(i, factorOwner[x]);
                }else{
                    factorOwner[x] = i;
                }
            }
        }

        int root = dsu.find(0);

        for(int i=1; i<n; i++){
            if(dsu.find(i) != root){
                return false;
            }
        }

        return true;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna
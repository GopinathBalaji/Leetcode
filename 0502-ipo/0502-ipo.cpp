// Method 1: Sorting + MaxHeap approach
/*
### Hint 1: Think “available projects”

At any moment, you can only choose projects where:

```text
capital[i] <= currentCapital
```

Among all projects you can currently afford, which one should you choose to maximize your capital as quickly as possible?

Hint: the one with the **largest profit**.

---

### Hint 2: You need two different orderings

You want to efficiently:

* discover newly affordable projects as your capital grows
* choose the affordable project with maximum profit

A useful combination is:

```text
Sort projects by required capital
+
Max-heap of profits
```

---

### Hint 3: Pair capital with profit

Create something like:

```cpp
vector<pair<int, int>> projects;
```

where each pair represents:

```text
{capitalRequired, profit}
```

Then sort by `capitalRequired`.

Now keep an index pointing to the next project you haven't considered yet.

---

### Hint 4: For each of the `k` project selections

Suppose your current capital is `w`.

First, move **all projects you can currently afford** into a max-heap:

```cpp
while (index < n && projects[index].first <= w) {
    // push its profit into maxHeap
    index++;
}
```

Why all of them?

Because once they're affordable, you want the heap to decide which has the highest profit.

---

### Hint 5: Pick the best available profit

After adding all affordable projects:

```cpp
w += maxHeap.top();
maxHeap.pop();
```

But think about this edge case:

```text
What if the heap is empty?
```

That means there is no project you can currently afford, so you can't increase your capital any further.

---

### Hint 6: Overall structure

Your algorithm will roughly look like:

```cpp
sort(projects.begin(), projects.end());

priority_queue<int> maxHeap;

for (int i = 0; i < k; i++) {

    // Add every project whose capital requirement <= w

    // If no projects are available, stop

    // Choose largest available profit
}
```

The key greedy idea is: **at each step, among every project currently possible, take the one giving the largest immediate increase in capital.**

Time complexity should end up around **`O(n log n + k log n)`**.
*/
class Solution {
public:
    int findMaximizedCapital(int k, int w, vector<int>& profits, vector<int>& capital) {
        vector<pair<int, int>> projects;

        for(int i=0; i<capital.size(); i++){
            projects.push_back({capital[i], profits[i]});
        }

        sort(projects.begin(), projects.end());

        priority_queue<int> maxHeap;
        int index = 0;

        for(int i=0; i<k; i++){

            while(index < profits.size() && projects[index].first <= w){
                maxHeap.push(projects[index].second);
                index++;
            }

            if(maxHeap.empty()){
                return w;
            }else{
                w += maxHeap.top();
                maxHeap.pop();
            }
        }

        return w;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna
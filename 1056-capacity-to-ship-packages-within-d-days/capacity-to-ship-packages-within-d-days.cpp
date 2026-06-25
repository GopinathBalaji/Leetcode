// Method 1: Binary Search over the possible answer space and not the array
/*
This is another **binary search on the answer** problem.

You are not searching through the array. You are searching for the smallest ship capacity that allows all packages to be delivered within `days`.

## Think about a candidate capacity

Suppose the ship capacity is:

```cpp
capacity = mid
```

Can you ship all packages in order within at most `days` days?

Simulate loading packages:

* Start with `currentLoad = 0`
* For each package:

  * If `currentLoad + package <= capacity`, put it on the current day’s ship.
  * Otherwise, start a new day and put this package there.

Count how many days are required.

## Binary search range

What is the minimum possible capacity?

```cpp
max(weights)
```

Because the ship must at least hold the heaviest single package.

What is the maximum possible capacity?

```cpp
sum(weights)
```

Because with that capacity, you can ship everything in one day.

So:

```cpp
left = max(weights);
right = sum(weights);
```

## Monotonic property

* Small capacity → may require too many days.
* Larger capacity → requires the same or fewer days.

That means binary search works.

For a trial capacity `mid`:

* If you can ship within `days`:

  * Try a smaller capacity.
  * `right = mid`
* Otherwise:

  * Capacity is too small.
  * `left = mid + 1`

## Core helper logic

Your helper should conceptually answer:

```cpp
bool canShip(vector<int>& weights, int days, int capacity)
```

Pseudo-code:

```cpp
usedDays = 1;
currentLoad = 0;

for each weight:
    if currentLoad + weight > capacity:
        usedDays++;
        currentLoad = 0;

    currentLoad += weight;

return usedDays <= days;
```

Notice that when a package does not fit, you must start a new day **before** adding that package.

## Binary search skeleton

```cpp
while (left < right) {
    int mid = left + (right - left) / 2;

    if (canShip(weights, days, mid)) {
        right = mid;
    } else {
        left = mid + 1;
    }
}

return left;
```

## Example intuition

```text
weights = [1,2,3,4,5,6,7,8,9,10]
days = 5
```

Try capacity `15`:

```text
Day 1: 1 + 2 + 3 + 4 + 5 = 15
Day 2: 6 + 7 = 13
Day 3: 8
Day 4: 9
Day 5: 10
```

It works in 5 days, so perhaps a smaller capacity might also work.

The answer is the **minimum capacity that is feasible**.
*/
class Solution {
private:
    bool canShip(vector<int>& weights, int days, int capacity){
        int usedDays = 1;
        int currentLoad = 0;

        for(int weight: weights){
            if(currentLoad + weight > capacity){
                usedDays++;
                currentLoad = 0;
            }

            currentLoad += weight;
        }

        return usedDays <= days;
    }

public:
    int shipWithinDays(vector<int>& weights, int days) {
        int n = weights.size();

        int left = 0;
        int right = 0;

        for(int weight: weights){
            left = std::max(left, weight);
            right += weight;
        }

        while(left < right){
            int mid = left + (right - left) / 2;

            if(canShip(weights, days, mid)){
                right = mid;
            }else{
                left = mid + 1;
            }
        }

        return left;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna
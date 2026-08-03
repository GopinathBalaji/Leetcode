// Method 1: Prefix-sum approach
/*
### Hint 1

Each trip is:

```cpp
[numPassengers, from, to]
```

Passengers get in at `from` and get out at `to`.

Think of each trip as creating two events:

```text
+numPassengers at from
-numPassengers at to
```

### Hint 2

Store the passenger change at each location:

```cpp
changes[from] += numPassengers;
changes[to] -= numPassengers;
```

Then move from left to right, maintaining the number of passengers currently in the car.

### Hint 3

At every location:

```cpp
currentPassengers += changes[location];
```

If:

```cpp
currentPassengers > capacity
```

return `false`.

### Hint 4

Dropping passengers off at `to` before picking up passengers for another trip starting at the same location is valid.

The difference-array approach handles this naturally because the changes are combined:

```cpp
changes[to] -= passengers;
changes[to] += otherPassengers;
```

### Hint 5

The locations are bounded, so you can use a fixed-size array:

```cpp
vector<int> changes(1001, 0);
```

Process each trip:

```cpp
changes[trip[1]] += trip[0];
changes[trip[2]] -= trip[0];
```

Then calculate the prefix sum.

### General structure

```cpp
vector<int> changes(1001, 0);

for (auto& trip : trips) {
    // add passengers at start
    // remove passengers at end
}

int passengers = 0;

for (int change : changes) {
    passengers += change;

    if (passengers > capacity) {
        return false;
    }
}

return true;
```

This runs in `O(n + 1000)` time and uses `O(1000)` space.
*/
class Solution {
public:
    bool carPooling(vector<vector<int>>& trips, int capacity) {
        vector<int> changes(1001, 0);

        for(auto& trip: trips){
            int numPassengers = trip[0];
            int from = trip[1];
            int to = trip[2];

            changes[from] += numPassengers;
            changes[to] -= numPassengers;
        }

        int passengers = 0;

        for(int change: changes){
            passengers += change;

            if(passengers > capacity){
                return false;
            }
        }

        return true;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna
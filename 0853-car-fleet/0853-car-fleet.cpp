// Method 1: Monotonic Increasing Stack approach
/*
## Hint 1: Think in terms of arrival time

Each car has:

```text
position[i]
speed[i]
```

The target is fixed.

For each car, compute how long it takes to reach the target:

```text
time = (target - position[i]) / speed[i]
```

The key is not just who is faster, but **whether a car catches up before the target**.

---

## Hint 2: Sort cars by position

Cars only interact with cars **in front of them**.

So sort cars by position.

Example:

```text
target = 12
position = [10, 8, 0, 5, 3]
speed    = [2, 4, 1, 1, 3]
```

Pair them:

```text
(position, speed)
(10, 2), (8, 4), (0, 1), (5, 1), (3, 3)
```

Sort by position:

```text
(0, 1), (3, 3), (5, 1), (8, 4), (10, 2)
```

But it is usually easier to process from **closest to target to farthest**:

```text
(10, 2), (8, 4), (5, 1), (3, 3), (0, 1)
```

---

## Hint 3: A car behind joins a fleet if it arrives sooner or equal

Suppose a car in front reaches the target in:

```text
frontTime
```

A car behind reaches the target in:

```text
currentTime
```

If:

```text
currentTime <= frontTime
```

then the behind car catches up before or exactly at the target.

So it becomes part of the front car’s fleet.

If:

```text
currentTime > frontTime
```

then it cannot catch up, so it forms a new fleet.

---

## Hint 4: Process from right to left

After sorting by position ascending, iterate from the end to the beginning.

Maintain the slowest arrival time of the fleet in front:

```cpp
double fleetTime = 0;
```

For each car from closest to target to farthest:

```cpp
time = (target - position) / speed
```

If:

```cpp
time > fleetTime
```

this car cannot catch the fleet ahead, so it starts a new fleet.

Then update:

```cpp
fleetTime = time;
```

Otherwise, it merges into the fleet ahead.

---

## Hint 5: Why `time <= fleetTime` means merge

Imagine:

```text
target = 12
car A: position = 10, speed = 2
car B: position = 8, speed = 4
```

Times:

```text
A time = (12 - 10) / 2 = 1
B time = (12 - 8) / 4 = 1
```

Car B catches car A exactly at the target, so they count as **one fleet**.

That is why equality also merges.

---

## Hint 6: Stack interpretation

You can also think of this as a monotonic stack of arrival times.

After sorting cars by position ascending, compute times.

Then traverse from right to left.

Push a time only when it is greater than the fleet time on top.

```cpp
if stack is empty or currentTime > stack.top():
    stack.push(currentTime)
```

The number of values in the stack is the number of fleets.

---

## Core idea

Sort cars by position. Then process from closest to target to farthest. A car forms a new fleet only if its arrival time is **greater** than the fleet ahead. Otherwise, it catches up and merges.

```text
Time: O(n log n)   // sorting
Space: O(n) or O(1) depending on implementation
```
*/
class Solution {
public:
    int carFleet(int target, vector<int>& position, vector<int>& speed) {
        int n = position.size();

        vector<pair<int, int>> cars;

        for (int i = 0; i < n; i++) {
            cars.push_back({position[i], speed[i]});
        }

        sort(cars.begin(), cars.end(), greater<pair<int, int>>());

        stack<double> st;

        for (int i = 0; i < n; i++) {
            double time = (double)(target - cars[i].first) / cars[i].second;

            if (st.empty() || time > st.top()) {
                st.push(time);
            }
        }

        return st.size();
    }
};





// Method 2: Same approach as above but without a Stack
/*
*/
// class Solution {
// public:
//     int carFleet(int target, vector<int>& position, vector<int>& speed) {
//         int n = position.size();

//         vector<pair<int, int>> cars;

//         for (int i = 0; i < n; i++) {
//             cars.push_back({position[i], speed[i]});
//         }

//         // Sort by position ascending
//         sort(cars.begin(), cars.end());

//         int fleets = 0;
//         double fleetTime = 0.0;

//         // Process from closest to target to farthest
//         for (int i = n - 1; i >= 0; i--) {
//             int pos = cars[i].first;
//             int spd = cars[i].second;

//             double time = (double)(target - pos) / spd;

//             // If this car takes longer than the fleet ahead,
//             // it cannot catch up, so it forms a new fleet.
//             if (time > fleetTime) {
//                 fleets++;
//                 fleetTime = time;
//             }
//         }

//         return fleets;
//     }
// };

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna
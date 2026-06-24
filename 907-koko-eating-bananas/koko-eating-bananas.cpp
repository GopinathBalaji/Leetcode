// Method 1: Binary Search over 1 to maxSpeed
/*
Think of this as a **binary search on the answer**, not on the banana piles.

Koko chooses one eating speed `k` bananas/hour. The question becomes:

> “Can Koko finish all piles within `h` hours at speed `k`?”

### Key observations

* Minimum possible speed: `1`
* Maximum possible speed: `max(piles)`

  * At this speed, each pile takes at most one hour.
* For a given speed `k`, a pile with `p` bananas takes:

```cpp
ceil(p / k)
```

hours.

In integer arithmetic, compute that as:

```cpp
(p + k - 1) / k
```

### Helper idea

Write a function like:

```cpp
bool canFinish(vector<int>& piles, int h, int speed)
```

It should:

1. For each pile, add how many hours it needs at `speed`.
2. Return whether total hours is `<= h`.

### Binary search decision

For a candidate speed `mid`:

* If Koko **can** finish within `h` hours:

  * `mid` might be the answer, but try a smaller speed.
  * Move `right`.
* Otherwise:

  * Speed is too slow.
  * Move `left`.

You are searching for the **smallest valid speed**.

### Binary-search template hint

Use a lower-bound style search:

```cpp
left = 1;
right = maxPile;

while (left < right) {
    mid = left + (right - left) / 2;

    if (canFinish(..., mid)) {
        right = mid;
    } else {
        left = mid + 1;
    }
}

return left;
```

### Important details

* Use `long long` for total hours because the sum can exceed `int`.
* Do not use floating-point `ceil`; integer math is cleaner.
* The monotonic property that makes binary search work is:

```text
larger eating speed → same or fewer total hours
```
*/
class Solution {
private:
    bool canFinish(vector<int> piles, int h, int speed){
        long long hours = 0;

        for(int pile: piles){
            hours += (pile + speed - 1) / speed;  // ceil(pile / speed)
        }

        return hours <= h;
    }

public:
    int minEatingSpeed(vector<int>& piles, int h) {
        int left = 1;

        int right = 0;
        for(int pile: piles){
            right = std::max(right, pile);
        } 
        
        while(left < right){
            int mid = left + (right - left) / 2;

            if(canFinish(piles, h, mid)){
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
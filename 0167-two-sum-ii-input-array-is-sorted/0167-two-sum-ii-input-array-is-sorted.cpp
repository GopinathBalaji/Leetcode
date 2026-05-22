// Method 1: Two-Pointer approach
/*
################################# WHAT WAS I DOING WRONG #############################

```cpp
numbers[left] + numbers[right]
```

You currently do this:

```cpp
if (target <= numbers[mid]) {
    right = mid;
} else {
    left = mid + 1;
}
```

But `numbers[mid]` alone does not tell you whether the correct pair is on the left side or right side. The target is a **sum of two numbers**, not a single number.

---

## Counterexample

```cpp
numbers = [2, 7, 11, 15]
target = 9
```

Start:

```cpp
left = 0   // numbers[left] = 2
right = 3  // numbers[right] = 15

sum = 2 + 15 = 17
mid = 1    // numbers[mid] = 7
```

Your code checks:

```cpp
target <= numbers[mid]
9 <= 7 // false
```

So it does:

```cpp
left = mid + 1;
left = 2;
```

Now you skipped index `0`, but index `0` is part of the correct answer:

```cpp
2 + 7 = 9
```

So the correct answer should be:

```cpp
[1, 2]
```

But your logic lost it.

---

## Correct two-pointer logic

Since the array is sorted:

```cpp
sum = numbers[left] + numbers[right]
```

If:

```cpp
sum == target
```

you found the answer.

If:

```cpp
sum < target
```

the sum is too small, so you need a bigger number. Move `left` rightward:

```cpp
left++;
```

If:

```cpp
sum > target
```

the sum is too large, so you need a smaller number. Move `right` leftward:

```cpp
right--;
```

---

## Why this works

Because the array is sorted.

Suppose:

```cpp
numbers[left] + numbers[right] < target
```

The current sum is too small. Decreasing `right` would only make the sum smaller, so the only useful move is:

```cpp
left++;
```

Suppose:

```cpp
numbers[left] + numbers[right] > target
```

The current sum is too large. Increasing `left` would only make the sum larger, so the only useful move is:

```cpp
right--;
```

Main issue:

```cpp
You are binary-searching using numbers[mid], but the decision should be based on the sum of numbers[left] and numbers[right].
```
*/
class Solution {
public:
    vector<int> twoSum(vector<int>& numbers, int target) {
        int n = numbers.size();

        int left = 0;
        int right = n-1;

        while(left < right){
            int sum = numbers[left] + numbers[right];
            int mid = left + (right - left) / 2;

            if(sum == target){
                return {left + 1, right + 1};
            }

            if(sum < target){
                left++;
            }else{
                right--;
            }
        }

        return {};
    }
};








// Method 2: Binary Search Approach
/*
### Complexity

* Outer loop runs `n` times
* Each binary search is `O(log n)`
* Total: **O(n log n)** time, **O(1)** extra space

---

## Why this is the “correct” binary-search pattern

Binary search requires a **monotonic condition** over a single sorted interval.

If you fix `i`, you’re looking for one value (`complement`) in the sorted subarray:

* `numbers[i+1 .. n-1]` is sorted
* “Is mid too small or too large?” is monotonic
  So classic binary search works perfectly.

This is different from trying to “binary search the two pointers” at the same time (which doesn’t form a monotonic predicate).

---

## Thorough walkthrough with an example

### Example

`numbers = [2, 7, 11, 15]`, `target = 9`

`n = 4`

---

### i = 0

* `numbers[i] = 2`
* `complement = 9 - 2 = 7`
* Search in subarray indices `[1..3]` → values `[7, 11, 15]`

Binary search:

* `left = 1`, `right = 3`
* `mid = 1 + (3-1)/2 = 2`
* `numbers[mid] = 11`
* Compare: `11 > 7` → move `right = mid - 1 = 1`

Next step:

* `left = 1`, `right = 1`
* `mid = 1`
* `numbers[mid] = 7`
* Match found!

Return:

* `{i+1, mid+1} = {1, 2}`

That matches the expected output.

---

## Another walkthrough (more iterations)

`numbers = [1, 2, 3, 4, 6, 10]`, `target = 16`

### i = 0

* `numbers[i] = 1`
* complement = 15
  Search `[2, 3, 4, 6, 10]`
* mid around 3/4, values too small → eventually not found

### i = 1

* `numbers[i] = 2`
* complement = 14
  Search `[3, 4, 6, 10]`
* not found

### i = 2

* `numbers[i] = 3`
* complement = 13
  Search `[4, 6, 10]`
* not found

### i = 3

* `numbers[i] = 4`
* complement = 12
  Search `[6, 10]`
* not found

### i = 4

* `numbers[i] = 6`
* complement = 10
  Search `[10]`
* mid = that element → found
  Return `{5, 6}` (1-based)

---

## Common pitfalls to avoid

* Search range must start at `i + 1` (can’t reuse the same element).
* Use `left <= right` in binary search.
* Return 1-based indices.
*/

// class Solution {
//     public int[] twoSum(int[] numbers, int target) {
//         int n = numbers.size();

//         for (int i = 0; i < n - 1; i++) {
//             int complement = target - numbers[i];

//             int left = i + 1;
//             int right = n - 1;

//             while (left <= right) {
//                 int mid = left + (right - left) / 2;

//                 if (numbers[mid] == complement) {
//                     // +1 because LeetCode wants 1-based indices
//                     return { i + 1, mid + 1 };
//                 } else if (numbers[mid] < complement) {
//                     left = mid + 1;
//                 } else {
//                     right = mid - 1;
//                 }
//             }
//         }

//         // Problem guarantees exactly one solution; this is just a fallback.
//         return {};
//     }
// }

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/leethub-v4/bcilpkkbokcopmabingnndookdogmbna
// Bottom-Up Dynamic Programming using Kadane's algorithm
/*
## Core Idea

Instead of checking every possible subarray (which is O(n²) or O(n³)), Kadane’s algorithm maintains a **running maximum**:

* At each position `i`, you decide:

  * **Extend the previous subarray** (add `arr[i]` to the running sum), OR
  * **Start fresh** at `arr[i]` (if the previous running sum became harmful).

So you keep two variables:

1. `currentMax` → max sum of subarray **ending at current index**
2. `globalMax` → max sum found so far across all indices

---

## Algorithm (intuitive steps)

1. Initialize `currentMax = arr[0]`, `globalMax = arr[0]`.
2. For each `arr[i]` (from i=1 to n-1):

   * `currentMax = max(arr[i], currentMax + arr[i])`
   * `globalMax = max(globalMax, currentMax)`
3. At the end, `globalMax` is your answer.

---

## Walkthrough Example

Array:

```
arr = [-2, 1, -3, 4, -1, 2, 1, -5, 4]
```

We want the maximum contiguous subarray sum.

### Step 1: Initialization

* `currentMax = -2` (arr\[0])
* `globalMax = -2`

---

### Step 2: Iterate

**i=1 → arr\[1] = 1**

* `currentMax = max(1, -2+1) = max(1, -1) = 1`
* `globalMax = max(-2, 1) = 1`

**i=2 → arr\[2] = -3**

* `currentMax = max(-3, 1+(-3)) = max(-3, -2) = -2`
* `globalMax = max(1, -2) = 1`

**i=3 → arr\[3] = 4**

* `currentMax = max(4, -2+4) = max(4, 2) = 4`
* `globalMax = max(1, 4) = 4`

**i=4 → arr\[4] = -1**

* `currentMax = max(-1, 4+(-1)) = max(-1, 3) = 3`
* `globalMax = max(4, 3) = 4`

**i=5 → arr\[5] = 2**

* `currentMax = max(2, 3+2) = max(2, 5) = 5`
* `globalMax = max(4, 5) = 5`

**i=6 → arr\[6] = 1**

* `currentMax = max(1, 5+1) = max(1, 6) = 6`
* `globalMax = max(5, 6) = 6`

**i=7 → arr\[7] = -5**

* `currentMax = max(-5, 6+(-5)) = max(-5, 1) = 1`
* `globalMax = max(6, 1) = 6`

**i=8 → arr\[8] = 4**

* `currentMax = max(4, 1+4) = max(4, 5) = 5`
* `globalMax = max(6, 5) = 6`

---

### Step 3: Final Answer

`globalMax = 6`

And the subarray giving this sum is `[4, -1, 2, 1]`.

---

## Complexity

* **Time:** O(n) (one pass)
* **Space:** O(1) (only a few variables)
*/
class Solution {
    public int maxSubArray(int[] nums) {
        int max_ending_here = nums[0];
        int max_so_far = nums[0];

        for(int i=1; i<nums.length; i++){
            max_ending_here = Math.max(nums[i], max_ending_here + nums[i]);
            max_so_far = Math.max(max_so_far, max_ending_here);
        }

        return max_so_far;
    }
}


// INTERVIEW FOLLOW-UP QUESTION: Return the actual Max Subarray and not just the sum
/*
Key idea:
Track where a candidate run starts. When you decide to “start fresh” at nums[i], record tempStart = i. Whenever you improve the global best, copy start = tempStart and end = i.
*/
// class Solution {

//     // Returns: {maxSum, startIndex, endIndex}
//     public int maxSubArray(int[] nums) {
//         int currSum = nums[0];
//         int maxSum = nums[0];

//         // indices to track and return
//         int start = 0, end = 0;   // best subarry so far
//         int tempStart = 0;   // start index of current running subarray

//         for(int i=1; i<nums.length; i++){

//             // Decide: extend or restart at i
//             if(currSum + nums[i] < nums[i]){
//                 currSum = nums[i];
//                 tempStart = i;   // restart here
//             }else{
//                 currSum += nums[i];  // extend
//             }


//             // Update global best
//             if(currSum > maxSum){
//                 maxSum = currSum;
//                 start = tempStart;
//                 end = i;
//             }
//         }

//         return new int[]{maxSum, start, end};
//     }


//     // Helper to extract the subarray
//     public static int[] subarraySlice(int[] nums, int start, int end){
//         return Arrays.copyOfRange(nums, start, end+1);
//     }
// }
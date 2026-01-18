// Method 1: Binary Search (mixing closed bounds and half-open updates)
/*
Your solution is **logically correct** for LeetCode 81. The only “issue” (and it’s expected) is that because of duplicates, it can degrade to **O(n)** in the worst case. That’s not a bug—LC81 is designed so you *can’t* guarantee `O(log n)` with duplicates.

### Why your code is correct

You’re using the same core logic as LC33:

* If `nums[mid] < nums[right]`, then the **right half `[mid..right]` is sorted** (strictly “better behaved” relative to `right`), so you can range-check the target there.
* If `nums[mid] > nums[right]`, then the **left half `[left..mid]` is sorted**, so you can range-check the target there.
* If `nums[mid] == nums[right]`, you **can’t tell which side is sorted** (duplicates mask the pivot), so you do `right--` to shrink safely.

#### Why `right--` is safe

In the `nums[mid] == nums[right]` case, you already checked `nums[mid] == target` and returned if true.
So if you reach the `else`, then:

* `nums[mid] != target`
* and since `nums[mid] == nums[right]`, it also means `nums[right] != target`

So removing `nums[right]` via `right--` cannot discard the target.

### The real “problem”: worst-case linear time

If the array has many duplicates, you may hit the `right--` case repeatedly, shrinking by 1 each time:

Example: `nums = [1,1,1,1,1,1,1]`, `target = 2`

Every iteration:

* `nums[mid] == nums[right]` and `nums[mid] != target`
* so `right--`
  That’s **O(n)**.

Even trickier: `nums = [1,1,1,1,0,1,1]`, `target = 0`
You might do several `right--` steps before the pivot becomes distinguishable.

### Quick walkthrough of a “duplicate-masking” case

`nums = [1,0,1,1,1]`, `target = 0`

* `left=0 right=4 mid=2 nums[mid]=1 nums[right]=1` → equal, not target → `right=3`
* `left=0 right=3 mid=1 nums[mid]=0` → found → `true`

Works, but you can see how duplicates force “peeling” the boundary.

### Small style note (not wrong)

Some people use `left++` when `nums[left] == nums[mid] == nums[right]` to potentially shrink faster in some patterns, but your `right--` alone is still correct (just can be slower on certain inputs).

**Bottom line:** Your solution is correct. The only thing to be aware of is the **worst-case O(n)** behavior, which is unavoidable for this problem.
*/
class Solution {
    public boolean search(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;

        while(left < right){
            int mid = left + (right - left) / 2;

            if(nums[mid] == target){
                return true;
            }

            if(nums[mid] < nums[right]){
                if(nums[mid] < target && target <= nums[right]){
                    left = mid + 1;
                }else{
                    right = mid;
                }
            }else if(nums[mid] > nums[right]){
                if(nums[left] <= target && target < nums[mid]){
                    right = mid;
                }else{
                    left = mid + 1;
                }
            }else{
                right--;
            }
        }

        return nums[left] == target;
    }
}





// Method 2: Binary Search with closed interval version
/*
*/
// class Solution {
//     public boolean search(int[] nums, int target) {
//         int l = 0, r = nums.length - 1;

//         while (l <= r) {
//             int mid = l + (r - l) / 2;

//             if (nums[mid] == target) return true;

//             // If we can't determine the sorted half due to duplicates
//             if (nums[l] == nums[mid] && nums[mid] == nums[r]) {
//                 l++;
//                 r--;
//                 continue;
//             }

//             // Left half [l..mid] is sorted
//             if (nums[l] <= nums[mid]) {
//                 if (nums[l] <= target && target < nums[mid]) {
//                     r = mid - 1;
//                 } else {
//                     l = mid + 1;
//                 }
//             }
//             // Right half [mid..r] is sorted
//             else {
//                 if (nums[mid] < target && target <= nums[r]) {
//                     l = mid + 1;
//                 } else {
//                     r = mid - 1;
//                 }
//             }
//         }

//         return false;
//     }
// }






// Method 3: Binary Search with half-open interval version
/*
*/
// class Solution {
//     public boolean search(int[] nums, int target) {
//         int l = 0, r = nums.length; // [l, r)

//         while (l < r) {
//             int mid = l + (r - l) / 2;

//             if (nums[mid] == target) return true;

//             // If duplicates block decision, shrink safely
//             if (nums[mid] == nums[r - 1]) {
//                 r--; 
//                 continue;
//             }

//             // Right half [mid..r-1] is sorted
//             if (nums[mid] < nums[r - 1]) {
//                 if (nums[mid] < target && target <= nums[r - 1]) {
//                     l = mid + 1;
//                 } else {
//                     r = mid; // keep [l, mid)
//                 }
//             }
//             // Left half [l..mid] is sorted
//             else { // nums[mid] > nums[r-1]
//                 if (nums[l] <= target && target < nums[mid]) {
//                     r = mid; // keep [l, mid)
//                 } else {
//                     l = mid + 1;
//                 }
//             }
//         }

//         return false;
//     }
// }

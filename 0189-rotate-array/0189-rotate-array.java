// Constant space solution - Reversal
class Solution {
    public void rotate(int[] nums, int k) {
        int n = nums.length;
        k = k % n;           // Normalize k
        if (k == 0) return;   // No need to rotate

        // Step 1: Reverse the entire array
        reverse(nums, 0, n - 1);
        // Step 2: Reverse the first k elements
        reverse(nums, 0, k - 1);
        // Step 3: Reverse the remaining n - k elements
        reverse(nums, k, n - 1);
        
    }

    public void reverse(int[] arr, int start, int end){
        while(start < end){
            int temp = arr[start];
            arr[start] = arr[end];
            arr[end] = temp;
            start ++;
            end --;
        }
    }
}


// Constant space solution - Cycle based rotation
// class Solution {
//     public void rotate(int[] nums, int k) {
//         int n = nums.length;
//         k = k % n;
//         if (k == 0) return;

//         int count = 0;        // how many elements have been moved
//         for (int start = 0; count < n; start++) {
//             int current = start;
//             int prevValue = nums[start];

//             do {
//                 int nextIndex = (current + k) % n;

//                 // swap prevValue into its destination
//                 int tmp = nums[nextIndex];
//                 nums[nextIndex] = prevValue;
//                 prevValue = tmp;

//                 current = nextIndex;
//                 count++;
//             } while (current != start);  // one full cycle completed
//         }
//     }
// }

// Why move from i to (i + k) % n?
// Rotating the array right by k positions means each element at index i should end up at index (i + k) mod n.

// Detailed Example
// Rotate nums = [1,2,3,4,5,6,7], k = 3, n = 7.

// Normalize k:
// k = 3 % 7 = 3.

// Starting at start = 0:

// current = 0, prev = 1

// next = (0 + 3) % 7 = 3 → swap: nums[3] becomes 1, prev becomes 4

// current = 3, count++

// next = (3 + 3) % 7 = 6 → swap: nums[6] = 4, prev = 7, count++

// current = 6

// next = (6 + 3) % 7 = 2 → swap: nums[2] = 7, prev = 3, count++

// current = 2

// next = (2 + 3) % 7 = 5 → swap: nums[5] = 3, prev = 6, count++

// current = 5

// next = (5 + 3) % 7 = 1 → swap: nums[1] = 6, prev = 2, count++

// current = 1

// next = (1 + 3) % 7 = 4 → swap: nums[4] = 2, prev = 5, count++

// current = 4

// next = (4 + 3) % 7 = 0 → swap: nums[0] = 5, prev = 1, count++

// current = 0, back to start — cycle complete.

// Final nums after cycle: [5,6,7,1,2,3,4]

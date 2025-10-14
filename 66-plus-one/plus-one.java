// Method 1: Using addFirst in linkedlist 
/*
Remove any special case for the last digit; set carry = 1 before the loop.
In the loop, use sum % 10 as the digit, sum / 10 as the carry.
*/
class Solution {
    public int[] plusOne(int[] digits) {
        LinkedList<Integer> temp = new LinkedList<>();

        int carry = 1;

        for(int i=digits.length-1; i>=0; i--){
            int val = digits[i] + carry;
            temp.addFirst(val%10);
            carry = val / 10;
        }

        if(carry != 0){
            temp.addFirst(carry);
        }

        int[] ans = temp.stream().mapToInt(i -> i).toArray();

        return ans;
    }
}




// Method 1.5: Same idea but faster using only arrays
/*
Why this works:
Starting from the last digit:
If it’s < 9, just add 1 and finish (no carry).
If it’s 9, set it to 0 and carry 1 to the next digit on the left.
If we carry past the most significant digit, the answer is 1 followed by zeros.

Time: O(n) worst-case (all 9s).
Space: O(1) extra (reuses input array unless we need one extra digit).

Quick walkthroughs:
[1,2,3] → bump last: [1,2,4] (return immediately).
[1,2,9] → last 9 → [1,2,0], carry to 2 (<9) → [1,3,0].
[9,9] → last 9 → [9,0], carry to first 9 → [0,0], overflow → new array [1,0,0].
*/

// class Solution {
//     public int[] plusOne(int[] digits) {
//         // walk from right to left
//         for (int i = digits.length - 1; i >= 0; i--) {
//             if (digits[i] < 9) {
//                 digits[i]++;          // no carry spill → done
//                 return digits;
//             }
//             digits[i] = 0;            // 9 + 1 → write 0 and keep carrying left
//         }
//         // if we got here, the number was all 9s (e.g., 9, 99, 999)
//         int[] res = new int[digits.length + 1];
//         res[0] = 1;                    // 999 + 1 → 1000
//         return res;
//     }
// }

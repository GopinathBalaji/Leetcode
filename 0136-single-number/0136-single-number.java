// Using XOR
/*
XOR, short for "exclusive or," is a binary operation that evaluates to 1 (true) if exactly one of the two input bits is 1, and 0 (false) otherwise.

So two same numbers will have their XOR as 0.
Therefore, If you XOR all the numbers, pairs cancel out (a ^ a = 0), and you're left with the number that appears once.

Let's trace [4, 1, 2, 1, 2]:

sql
Copy code
Step 0: result = 0

Step 1: result = 0 ^ 4 = 4
Step 2: result = 4 ^ 1 = 5
Step 3: result = 5 ^ 2 = 7
Step 4: result = 7 ^ 1 = 6
Step 5: result = 6 ^ 2 = 4

Final result = 4 (the number that appears only once)
*/
class Solution {
    public int singleNumber(int[] nums) {
        int result = 0;

        for(int num: nums){
            result ^= num;
        }

        return result;
    }
}
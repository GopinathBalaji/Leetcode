class Solution {
public:
//     Method 1: Math
    // Here are the principles at play:
// Incrementing or decrementing all elements has no effect on the answer - it is a no-op
// Incrementing N-1 elements is the same as decrementing one element
// Assuming those are true (explanations below), we can start thinking about our solution:
// If we're given
// [2, 3, 4]
// and that is the same as
// [0, 1, 2] (principle #1)
// and in each move we can decrement an element by 1 (principle #2)

// How many moves will it take to turn [0, 1, 2] into [0, 0, 0]?
// Well, it's just 1 + 2 = 3. That's it. That's the answer.

// To recap, here's what we did:

// To go from [2, 3, 4] => [0, 1, 2], we subtracted the smallest value (2 in this case) from all 
// the elements. If sum represents the sum of all the initial elements, we subtracted min * N from 
// sum.
// To go from [0, 1, 2] => 3 (answer), we just summed up 0 + 1 + 2 = 3, which is just the 
// remainder of the subtraction above.
// So answer = sum - min * N, which is the golden formula.
    int minMoves(vector<int>& nums) {
        int sum = 0;
        int mi = INT_MAX;
        
        for(int i=0;i<nums.size();i++){
          sum += nums[i];
            mi = min(mi,nums[i]);
        }
        
        return sum - mi*nums.size();
    }
};
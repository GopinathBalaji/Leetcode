// Boyer–Moore Voting Algorithm
/*
Think of each element as a vote. We keep a running candidate and a count (its lead).

When we see the candidate again, we increment (lead grows).
When we see a different number, we decrement (they cancel each other).

If the lead drops to 0, we discard the current candidate (it’s been “canceled out”) and start fresh with the next element.

Because a true majority element appears more than n/2 times, it cannot be completely canceled by all other elements; it’s guaranteed to be the final candidate.
*/
class Solution {
    public int majorityElement(int[] nums) {
        int candidate = 0;
        int count = 0;

        for(int x: nums){
            if(count == 0){
                candidate = x;
            }

            count += (x == candidate) ? 1 : -1;
        }

        return candidate;
    }
}
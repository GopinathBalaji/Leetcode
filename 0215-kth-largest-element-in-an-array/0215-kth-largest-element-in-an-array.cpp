// Method 1: Min-Heap of size atmost k
/*
*/
class Solution {
public:
    int findKthLargest(vector<int>& nums, int k) {
        priority_queue<int, vector<int>, greater<int>> minHeap;

        for(int i=0; i<k; i++){
            minHeap.push(nums[i]);
        }

        for(int i=k; i<nums.size(); i++){
            minHeap.push(nums[i]);

            minHeap.pop();
        }

        return minHeap.top();
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna
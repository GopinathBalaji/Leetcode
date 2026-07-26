// Method 1: Max-Heap approach
/*
*/
class Solution {
public:
    int lastStoneWeight(vector<int>& stones) {
        std::make_heap(stones.begin(), stones.end());

        while(stones.size() > 1){
            int max1 = stones.front();

            // Remove the maximum element properly
            std::pop_heap(stones.begin(), stones.end()); // Moves max to the back
            stones.pop_back(); // Physically removes it

            int max2 = stones.front();
            // Remove the maximum element properly
            std::pop_heap(stones.begin(), stones.end()); // Moves max to the back
            stones.pop_back(); // Physically removes it

            if(max1 == max2){
                continue;
            }else{
                // Add a new element properly
                stones.push_back(max1 - max2);
                std::push_heap(stones.begin(), stones.end()); // Re-heaps
            }
        }

        return stones.size() == 0 ? 0 : stones.front();
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna
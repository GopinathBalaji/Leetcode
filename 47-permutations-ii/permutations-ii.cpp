// Method 1: Backtracking with duplicate handling
/*

*/
class Solution {
private:
    void backtrack(vector<int>& nums, vector<int>& current, vector<vector<int>>& result, vector<bool>& used){
        if(current.size() == nums.size()){
            result.push_back(current);
            return;
        }
        
        for(int i=0; i<nums.size(); i++){
            if(used[i]){
                continue;
            }

            if(i > 0 && nums[i] == nums[i - 1] && !used[i - 1]){
                continue;
            }

            used[i] = true;
            current.push_back(nums[i]);

            backtrack(nums, current, result, used);

            current.pop_back();
            used[i] = false;
        }
    }

public:
    vector<vector<int>> permuteUnique(vector<int>& nums) {
        std::sort(nums.begin(), nums.end());

        vector<int> current;
        vector<vector<int>> result;
        vector<bool> used(nums.size(), false);

        backtrack(nums, current, result, used);

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna
class Solution {
public:
    int removeDuplicates(vector<int>& nums) {
        vector<int>::iterator it;
        int a = nums.size();
        
        for(int i=0;i<a;i++){
      if(count(nums.begin(),nums.end(),nums[i]) > 1){
          while(count(nums.begin(),nums.end(),nums[i]) > 1){
              it = find(nums.begin(),nums.end(),nums[i]);
              nums.erase(it);
          }
      }
            a = nums.size();
        }
        
        int b = nums.size();
        
        return b;
    }
};

// Method 2: Slow and Fast Pointers
   /*
   Since the array is already sorted, we can keep two pointers ii and jj, where ii is the slow-runner while jj is the fast-runner. As long as nums[i] = nums[j], we increment j to skip the duplicate.

When we encounter nums[j] != nums[i], the duplicate run has ended so we must copy its value to nums[i + 1]nums[i+1]. ii is then incremented and we repeat the same process again until j reaches the end of array.

public int removeDuplicates(int[] nums) {
    if (nums.length == 0) return 0;
    int i = 0;
    for (int j = 1; j < nums.length; j++) {
        if (nums[j] != nums[i]) {
            i++;
            nums[i] = nums[j];
        }
    }
    return i + 1;
}
   */

// Method 3: STL
   /*
    int removeDuplicates(vector<int>& nums) {
        nums.erase(std::unique(nums.begin(), nums.end()), nums.end());
        return nums.size();
    }
   */
class Solution {
public:
    vector<int> findDisappearedNumbers(vector<int>& nums) {
//         Method 1: Hash Map
        unordered_map<int,int> m1;
        vector<int> v1;
      for(int i=0;i<nums.size();i++){
          m1[nums[i]]++;
      }  
        for(int i=1;i<=nums.size();i++){
            if(!m1[i]){
                v1.push_back(i);
            }
        }
        return v1;
    }
};

// Method 2: Get index from each value and convert to negative
// The index that have not become negative means that number+1 does not exist in the array
   /*
     vector<int> findDisappearedNumbers(vector<int>& nums) {
        int n = nums.size();
        vector<int> res;
        for(int i = 0; i < n; i++){
            int idx = abs(nums[i]); 
            if(nums[idx-1] > 0) nums[idx-1] = -nums[idx-1]; // marking -ve    
        }
        for(int i = 0; i < n; i++)
            if(nums[i] > 0) res.push_back(i+1);
        
        return res;
    }
   */

// Method 2: Swapping 
// Swap the elements to its correct place and check which are not properly positioned
// The for loop keeps swapping elements until the values are in the indexes they should be in, 
// except for the repeated numbers. So nums [4,3,2,7,8,2,3,1] will become [1,2,3,4,3,2,7,8].
// Let us trace through each iteration that actually changes values:

// When i = 0, 
// nums[0] = 4, so put 4 into the 4th element. The 4th element happens to be 7, so swap 4 & 7 
// => [7,3,2,4,8,2,3,1]. Continue in while loop.
// nums[0] = 7, so put 7 into the 7th element, swap 7 & 3 => [3,3,2,4,8,2,7,1]. Continue in while 
// loop.
// Similarly, swap 3 & 2 => [2,3,3,4,8,2,7,1]. Continue in while loop.
// Similarly, swap 2 & 3 => [3,2,3,4,8,2,7,1]. Exit while loop. Iterate in for loop.
// When i = 1, 2, 3,
  // no more changes needed, because the 2nd, 3rd, and 4th elements are already 2, 3, and 4.
// When i = 4,
  // nums[4] = 8. Swap 8 & 1 => [3,2,3,4,1,2,7,8].
  // nums[4] = 1. Swap 1 & 3 => [1,2,3,4,3,2,7,8]
// When i = 5, 6, 7,
    // no more changes needed, since everything is now in order, except for the 5th and 6th 
    // element, because values 5 and 6 are lacking.

   /*
     vector<int> findDisappearedNumbers(vector<int>& nums) {
        vector<int> ans;
		
		// #1
        for(int i = 0; i < nums.size(); i++)
            while (nums[i] != i+1 && nums[nums[i]-1] != nums[i])
                swap(nums[i], nums[nums[i]-1]);
				
		// Compare nums array [1,2,3,4,3,2,7,8] with [1,2,3,4,5,6,7,8]
        for(int i = 1; i <= nums.size(); i++)
            if (nums[i-1] != i) // nums[4] != 5, nums[5] != 6
                ans.push_back(i); // add 5 and 6 to ans
				
        return ans;
    }
   */
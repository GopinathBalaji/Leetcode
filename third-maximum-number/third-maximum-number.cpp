class Solution {
public:
    int thirdMax(vector<int>& nums) {
        sort(nums.begin(),nums.end());
        int count = 1;
        
        for(int i=nums.size()-2;i>=0;i--){
            if(nums[i]<nums[i+1]){
                count++;
                if(count==3){
                    return nums[i];
                }
            }
        }
        return nums[nums.size()-1];
    }
};

// Method 2: Three Pointer Approach
   /*
     int thirdMax(vector<int>& nums) {
          long long int max_first = LONG_MIN;
        long long int max_second = LONG_MIN;
        long long int max_third = LONG_MIN;
        
        for (int n : nums) {
            if (n > max_first) {                
                max_third = max_second;
                max_second = max_first;
                max_first = n;
            } else if (n > max_second && n != max_first) {
                max_third = max_second;
                max_second = n;                
            } else if (n > max_third && n!= max_first && n != max_second) {
                max_third = n;
            }                                    
        }
        
        return max_third==LONG_MIN ? max_first : max_third;
     }
   */

// Method 3: Using Extra Space
   /*
     int thirdMax(vector<int>& nums) {
        map<int,int,greater<int>> heap;
        for(auto i : nums){
            heap[i]++;
        }
        if(heap.size()<3){
            return heap.begin()->first;
        }
        map<int,int,greater<int>> :: iterator l;
        l=heap.begin();
        advance(l,2);
        return l->first;
    }
   */
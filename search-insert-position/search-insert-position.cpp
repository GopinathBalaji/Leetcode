class Solution {
public:
    int searchInsert(vector<int>& nums, int target) {
        
        int left=0;
        int right=nums.size()-1;
        while(left<=right){
            int mid = left + (right-left)/2;
            if(target==nums[mid]){
                return mid;
            }
            if(target>nums[mid]){
                left = mid+1;
            }
            if(target<nums[mid]){
                right = mid-1;
            }
        }
        
        
        return left;
    }
};

// Method 2: Recursive 
   /*
    public int binarySearch(int[] nums, int si, int ei, int target){       
        if(ei >= si){
            int mid = (si+ei)/2;       
            if(nums[mid] == target) return mid;
        
            if(nums[mid] < target)
                return binarySearch(nums, mid+1, ei, target );
                
            return binarySearch(nums, si, mid-1, target );
        }
        return si;
    }
    
    public int searchInsert(int[] nums, int target) {
        return binarySearch(nums, 0, nums.length - 1, target);
    }
   */
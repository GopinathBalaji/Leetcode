class Solution {
public:
    int maxSubArray(vector<int>& nums) {
//         Method 1: Dynamic Programming
        
        if(nums.size()==1){
            return nums[0];
        }
        vector<int> v1(nums.size());
        int maxx = nums[0];
        v1[0] = nums[0];
        
        for(int i=1;i<nums.size();i++){
            v1[i] = max(nums[i],nums[i]+v1[i-1]);
            maxx = max(v1[i],maxx);
        }
        return maxx;
    }
};

 
// Method 2: Kadane's Algorithm

   /*
   int max_so_far = INT_MIN;
   int max_curr = 0;
   for(int i=0;i<nums.size();i++){
      max_curr += nums[i];
      if(max_so_far<max_curr){
      max_so_far += max_curr;
      }
      if(max_curr<0){
      max_curr = 0
      }
   }
   return max_so_far;
   */

// Method 3: Divide and Conquer
//          The array is divided into 3 parts: left,right and the sub-array that start form the middle and extends towards both sides, since it crosses the middle each time the function is called, it is named crossMax

   /*
    public int maxSubArray(int[] nums) {
        return findMaxSum(nums, 0, nums.length-1);     
    }
    
    private int findMaxSum(int[] nums, int s, int e){
        if(s==e) return nums[s];
        
        int mid = s + (e-s)/2;
        
        int leftMax = findMaxSum(nums, s, mid);
        int rightMax = findMaxSum(nums, mid+1, e);
        int crossMax = findMaxCrossSum(nums, s, mid, e);
      
        
        return Math.max(leftMax, Math.max(rightMax, crossMax));
    }
    
    private int findMaxCrossSum(int []nums, int s, int m, int e){

        int lSum=0, lMax=Integer.MIN_VALUE;
		
        for(int i=m; i>=s; i--){
            lSum+=nums[i];
            lMax = Math.max(lMax, lSum);        
        }
        
        int rSum=0, rMax=Integer.MIN_VALUE;
		
        for(int i=m+1; i<=e; i++){
            rSum+=nums[i];
            rMax = Math.max(rMax, rSum);
        }
        
        return lMax+rMax;
    }
   */
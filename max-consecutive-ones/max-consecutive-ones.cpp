class Solution {
public:
    int findMaxConsecutiveOnes(vector<int>& nums) {
//         Method 1: Greedy with extra space
        int a=0;
        vector<int> v1;
        for(int i=0;i<nums.size();i++){
            if(nums[i]==1){
                a++;
            }else{
                v1.push_back(a);
                a = 0;
            }
        }
        v1.push_back(a);
        sort(v1.begin(),v1.end());
        return v1.back();
    }
};

// Method 2: Without Extra Space (Greedy with no extra space)
   /*
    int findMaxConsecutiveOnes(vector<int>& nums) {
		int i=0;
		int max=0;
		for(int n=0;n<nums.size();n++){
			if(nums[n]==0){
				i=0;
			}
			else{
				i=i+1;
				if(i>max){
					max=i;
				}
			}
		}
		return max;
	}
   */

// Method 3: Sliding window Approach 1
   /*
    public int findMaxConsecutiveOnes(int[] nums) {
        int n = nums.length;
        int max = 0, left = 0, right = 0;
        while (left < n && right < n) {
            while (left < n && nums[left] == 0)
                left++;
            right = left;
            while (right < n && nums[right] == 1)
                right++;
            max = Math.max(max, right - left);
            left = right;
        }
        return max;
    }
   */

// Method 4: Sliding window Approach 2
   /*
    public int findMaxConsecutiveOnes(int[] nums) {
        int i=0,j=0;
        int max = Integer.MIN_VALUE;
        for(j=0; j<nums.length; j++)
        {
            if(nums[j] == 0)
            {
                max= Math.max(max, j-i);
                i=j+1;
            }
        }
        max= Math.max(max, j-i);
        return max;
    }
   */
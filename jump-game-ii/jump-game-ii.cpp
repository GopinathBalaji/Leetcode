class Solution {
public:
// Method 1: Similar to Dynamic Programming 
//             Update variable i to the farthest poin that can reached in a move
//     Similar questions ( Remove Covered Intervals, Minimum Number of Arrows to Burst Balloons, 
//    and Jump Game II.)
    
//     int jump(vector<int>& nums) {
//         if(nums.size()==1){
//             return 0;
//         }
        
//         int maxreach = 0 + nums[0];
//         int i=0,k=0,step=1;
        
//         while(maxreach < nums.size()-1){
//             step++;
//             for(int j=i+1;j<nums[i]+i;j++){
//                 if(nums[j] + j > maxreach){
//                     maxreach = nums[j] + j;
//                     k = j;
//                 }
//             }
//             i = k;
//         }
//         return step;
//     }
    
    int jump(vector<int>& a)
{
	if (a.size() == 1) return 0;
	int maxReach = 0 + a[0], i = 0, k = 0, step =1;
	while (maxReach < a.size() - 1)
	{
		step++;
		for (int j = i + 1; j <= a[i] + i; j++)
		{
			if (a[j] + j > maxReach )
			{
				maxReach = a[j] + j;
				k = j;
			}
		}
		i = k;
	}
	return step;
}
};
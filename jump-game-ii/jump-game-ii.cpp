class Solution {
public:
// Method 1: Similar to Dynamic Programming 
//             Update variable i to the farthest poin that can reached in a move
//     Similar questions ( Remove Covered Intervals, Minimum Number of Arrows to Burst Balloons, 
//    and Jump Game II.)
    
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
    
//     ---------------OR-------------------
//     Similar approach
// we keep two pointers start and end that record the current range of the starting nodes. Each time 
// after we make a move, update start to be end + 1 and end to be the farthest index that can be reached
// in 1 move from the current [start, end].
    /*
      int jump(vector<int>& nums) {
        int n = nums.size(), step = 0, start = 0, end = 0;
        while (end < n - 1) {
            step++; 
			int maxend = end + 1;
			for (int i = start; i <= end; i++) {
                if (i + nums[i] >= n - 1) return step;
				maxend = max(maxend, i + nums[i]);
			}
            start = end + 1;
            end = maxend;
        }
		return step;
    }
    */
};
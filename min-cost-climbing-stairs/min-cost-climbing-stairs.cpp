class Solution {
public:
//     Method 1: Bottom Up Dynamic Programming
    int minCostClimbingStairs(vector<int>& cost) {
     for(int i=2;i<cost.size();i++){
         cost[i] = cost[i] + min(cost[i-1],cost[i-2]);
     }
        return min(cost[cost.size()-1],cost[cost.size()-2]);
    }
};

// Method 2: Bottom Up but Space Optimized
   /*
    int n = cost.size();
        cost.push_back(0);
       
        int a = cost[0];
        int b = cost[1];
        int c;
        for(int i=2;i<=n;i++)
        {
            c = cost[i] + min(a,b);
            a = b;
            b = c;
        }
        return c;
   */

// Method 3: Top Down without Memoization

// We start at either step 0 or step 1. The target is to reach either last or second last step, 
// whichever is minimum.

// Step 1 - Identify a recurrence relation between subproblems. In this problem,
// Recurrence Relation:
// mincost(i) = cost[i]+min(mincost(i-1), mincost(i-2))
// Base cases:
// mincost(0) = cost[0]
// mincost(1) = cost[1]

// Step 2 - Covert the recurrence relation to recursion
   /*
    public int minCostClimbingStairs(int[] cost) {
	int n = cost.length;
	return Math.min(minCost(cost, n-1), minCost(cost, n-2));
}
private int minCost(int[] cost, int n) {
	if (n < 0) return 0;
	if (n==0 || n==1) return cost[n];
	return cost[n] + Math.min(minCost(cost, n-1), minCost(cost, n-2));
}
   */

// Method 4: Top Down wiht Memoization

   /*
    int[] dp;
public int minCostClimbingStairs(int[] cost) {
	int n = cost.length;
	dp = new int[n];
	return Math.min(minCost(cost, n-1), minCost(cost, n-2));
}
private int minCost(int[] cost, int n) {
	if (n < 0) return 0;
	if (n==0 || n==1) return cost[n];
	if (dp[n] != 0) return dp[n];
	dp[n] = cost[n] + Math.min(minCost(cost, n-1), minCost(cost, n-2));
	return dp[n];
}

---------------OR----------

// Since we can start from the first step or the second step, an extra 0 is added to help us 
// decide from where should we start, this way we don't have to make two calls in our main funcn.

vector<int> dp;
    int go(int index,vector<int> &cost)
    {
        if(index==0 || index==1)    return cost[index];
        if(dp[index] != -1) return dp[index];
        return dp[index] = cost[index] + min( go(index-1,cost),go(index-2,cost) );
    }
    int minCostClimbingStairs(vector<int>& cost) {
        int n = cost.size();
        dp.resize(n+1,-1);
        
        cost.push_back(0);
        return go(n,cost);
    }
   */
class Solution {
public:
    // Method 1: Top Down with Memoization
   int dp[1001];
     bool divisorGame(int n) {
       memset(dp,-1,sizeof dp);
        return recur(n);
    }
    
    int recur(int n){
        if(n==1){
          return 0;  
        }
        if(dp[n] != -1){
        return dp[n];
        }
        else{
            for(int i=1;i*i<=n;i++){
                if(n%i==0){
                    if(recur(n-1)==0){
                        return dp[n]=1;
                    }
                    // if((i!=1) && recur(n-(n/i))==0){
                    //    return dp[n]=1;
                    // }
                }
            }
            return dp[n]=0; 
        }
    }
    
};

// Method 2: Recursive Approach (TLE)
   /*
     bool divisorGame(int n) {
        return recur(n);
    }
    
    int recur(int n){
        if(n==1){
          return 0;  
        }else{
            for(int i=1;i<n;i++){
                if(n%i==0){
                    if(recur(n-1)==0){
                        return 1;
                    }
                }
            }
            return 0; 
        }
    }
   */

// Method 3: Mathematical Solution
// if Alice will lose for N, then Alice will must win for N+1, since Alice can first just make N 
// decrease 1.
// for any odd number N, it only has odd factor, so after the first move, it will be an even 
// number
   /*
     public boolean divisorGame(int n) {
        return n%2==0;
    }
   */

// Method 4: Bottom Up Dynamic Programming
// We can see this code's condition set "DP[i]=true" is "DP[i-j]=false"
// if Bob lose , means DP[i-j]=false , then Alice will win this game absolutely.
   /*
   
    bool divisorGame(int N) {
        bool dp[N+1];
        memset(dp, false, N+1);
        for(int i=2; i <= N; i++)
            for(int j = 1; j*j <= i;j++){
                if(i % j == 0 && !dp[i - j])
                    dp[i] = true;
            }
        
        return dp[N];
    }
   */
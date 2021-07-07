class Solution {
public:
//     Method 1:Bottom Up (Tabulation) Dyamic Programming
    int tribonacci(int n) {
        vector<int>a(n+1,0);
        if(n==0){
            return 0; 
        }
        if(n==1 || n==2){
            return 1;
        }
       a[0]=0;
       a[1]=1;
       a[2]=1;
       for(auto i=3;i<=n;i++){
           a[i]=a[i-1]+a[i-2]+a[i-3];
       }
        
      return a[n];     
    }
};

    //     Method 2: Recursive solution (TLE)
    /*
    int tribonacci(int n) {
        return fib(n);
    }
    
    int fib(int n){
        if(n==0){
            return 0;
        }
        if(n==1){
            return 1;
        }
        if(n==2){
            return 1;
        }
        return fib(n-1)+fib(n-2)+fib(n-3);
    }
};
   */

// Method 3: Top Down (Memoization) Dynamic Programming
   /*
    int tribonacci(int n){
        int dp[38] = {-1};
         return mem(dp,n);
    }
    
    int mem(int* dp,int n){
        int result = -1;
        if(dp[n] != -1){
           result = dp[n];
           return result;
        }
        if(n <= 1){
           result = n;
        }
        else if(n==2){
           result = 1;
        }
        else{
        result = mem(dp,n-1) + mem(dp,n-2) + mem(dp,n-3);
        }
        d[n] = result;
        return dp[n];
    }
   */


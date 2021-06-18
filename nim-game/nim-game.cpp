class Solution {
public:
    bool canWinNim(int n) {
       return n%4!=0;
    }
};

/*
// DP Solutions

public boolean canWinNim(int n) {
List list = new LinkedList();
for(int i=1; i <= n; i++){
if (i <= 3) list.add(true);
else {
boolean res = false;
res |= !list.get(0);
res |= !list.get(1);
res |= !list.get(2);
list.remove(0);
list.add(res);
}
}
return list.remove(2);


/////////////////
bool canWinNim(int n)
    {
        vector<bool> dp(n+1);
        dp[1] = true, dp[2] = true, dp[3] = true;
        for(int i=4; i<=n; i++)
            dp[i] = (!dp[i-1] || !dp[i-2] || !dp[i-3]);
        return dp[n];
    }
//////////////////
   public boolean canWinBash(int n) {
        boolean[] res = {false, true, true, true};
        for(int i = 4 ; i <= n ; i++)
            res[i % 4] = !(res[(i - 1) % 4] && res[(i - 2) % 4] && res[(i - 3) % 4]);
        return res[n % 4];
    } 
    
/////////////////////

public boolean canWinBash(int n) {
        if(n <= 0) throw new IllegalArgumentException();
        if(n < 4) return true;
        boolean[] res = new boolean[n + 1];
        res[1] = true;
        res[2] = true;
        res[3] = true;
        for(int i = 4 ; i <= n ; i++)
            res[i] = !(res[i - 1] && res[i - 2] && res[i - 3]);
        return res[n];
    }
*/
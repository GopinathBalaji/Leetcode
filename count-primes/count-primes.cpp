class Solution {
public:
 int countPrimes(int n) {
        if(n==0 or n==1) return 0;
        vector<int> prime(n,0);
        int count=0;
        for(int i=2;i*i<=n;i++){
            if(!prime[i]){
                for(int j=i*i;j<n;j+=i){
                    if(prime[j]==0){
                        prime[j]=1;
                        count++;      
                    }
                }
            }
        }
        return n-count-2;  
    }

};
class Solution {
public:
    vector<int> getRow(int rowIndex) {
//         Method 1: Bottom-Up Dynamic Programming
        vector<int> v1{1};
        if(rowIndex==0){
            return v1;
        }
        vector<vector<int>> ans;
        ans.push_back(v1);
        for(int i=1;i<=rowIndex+1;i++){
            vector<int> temp;
            temp.push_back(1);
            for(int j=1;j<i-1;j++){
                temp.push_back(ans[i-1][j-1] + ans[i-1][j]);
            }
            temp.push_back(1);
            ans.push_back(temp);
        }
        return ans[rowIndex+1];
    }
};

// Method 2: Without extra space 
//Now we know that nC0 is equal to 1.Let us store it in variable 'prev'.
// We will calculate next terms of the sequence using standard formula which is: nCr=((nCr-1)(n-r+1))/r
// Let us store this value in a variable named 'curr'.
// Now at each step we will update prev=curr for calculation of next term of sequence.

// Points to Remember:
// while calculating next term the value calculated may overflow for higher values of n. So store 'previous' term in a long long int variable 

   /*
   vector<int> getRow(int n) {
        if(n==0){
            return {1};
        }
        long long int prev=1;
        vector<int>result;
        result.push_back(1);
        for(int i=1;i<=n;++i){
            int curr=(prev*(n-i+1))/i;
            prev=curr;
            result.push_back(prev);
        }
        return result;
    }
   */
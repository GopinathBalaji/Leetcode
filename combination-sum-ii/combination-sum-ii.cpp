class Solution {
public:
//     Method 1
//      We sort so that we can skip same candidates. We need to skip same candidates because it will lead
//       to same solutions in different order.
    
    vector<vector<int>> combinationSum2(vector<int>& candidates, int target) {
        vector<vector<int>> res;
        vector<int> v;
        sort(candidates.begin(),candidates.end());
        int index = 0;
        int sum = 0;
        search(index,sum,target,candidates,v,res);
        return res;
    }
    
    bool isValidState(int sum,int target){
        return sum==target;
    }
    
    // bool isSafeCandidate(){}
    
    void search(int index,int sum,int target,vector<int> &candidates,vector<int> &v,vector<vector<int>> &res){
        if(sum>target){
            return;
        }
        if(isValidState(sum,target)){
            res.push_back(v);
            return;
        }
        for(int i=index;i<candidates.size();i++){
            sum+=candidates[i];
            v.push_back(candidates[i]);
            search(i+1,sum,target,candidates,v,res);
            sum-=candidates[i];
            v.pop_back();
             while(i < candidates.size()-1 && candidates[i] == candidates[i+1]) {// avoid redundant 
                                                                                 // candidates
                    i++;
              }
          }
        }
};

// Method 2
  // i > st: we will pick the number at the current curr position into the combination, regardless the
 // other conditions. This is important, since the iteration should allow us to select multiple instances
 // of a unique number into the combination.

   /*
     vector<vector<int> > ans;

void solve(vector<int>& candidates, int target, vector<int> &temp, int st, int sum){
 
    if(sum == target){
        ans.push_back(temp);
		return ;
    }
    if(sum > target){
        return ;
    }
   
    for(int i=st; i<candidates.size(); i++){
	//okay, so here the only difference is when we encounter same elements we dont need to 
	//include them to out current solution again and again we can just skip them. 
	//And since our array is sorted hence we can simply ignore out some elements 
	//which are already included in the array.
        if(i > st && candidates[i] == candidates[i-1]){
            continue;
        }
        
        temp.push_back(candidates[i]);
        solve(candidates, target, temp, i + 1, sum + candidates[i]);
        temp.pop_back();
    }
    
    return;
    
}

vector<vector<int>> combinationSum2(vector<int>& candidates, int target) {
    int n = candidates.size();
    if(n == 0){
        return {};
    }
    
    sort(candidates.begin(), candidates.end());
    
    vector<int> temp;
    
    solve(candidates, target, temp, 0, 0);
    
    return ans;
}
   */
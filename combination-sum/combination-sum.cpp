class Solution {
public:

    void search(vector<int>& candidates,vector<vector<int>> &result,vector<int> &v,int target,int        index,int sum)
    {
        if(sum>target)return ;//base case if sum is greater then target then return 

        if(isValidState(sum,target)){
            result.push_back(v);//id sum is equal to target then just add current to result
            return;
        }
        for(int i=index;i<candidates.size();i++){
            sum+=candidates[i];//and current value to sum
            v.push_back(candidates[i]);//and current value to current vector
            search(candidates,result,v,target,i,sum);//again reccure for same index i
            sum-=candidates[i];//back track mean remove value that previously added
            v.pop_back();//remove the value that previously added to current 
        }      
    }
    
     // bool isSafeCandidate(){}
    
     
    bool isValidState(int sum,int target){
        if(sum==target){
            return true;
        }
        return false;
    }

    vector<vector<int>> combinationSum(vector<int>& candidates, int target) {
        int sum=0;
        int index = 0;
        vector<vector<int>> result;
        vector<int> v;
        search(candidates,result,v,target,index,sum);
        return result;
    }
};
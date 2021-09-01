class Solution {
public:
//     Method 1
    
    vector<string> letterCombinations(string digits) {
        
        vector<string> res;
        vector<string> hash_map(10);
         hash_map[2]="abc";
         hash_map[3]="def";
         hash_map[4]="ghi";
         hash_map[5]="jkl";
         hash_map[6]="mno";
         hash_map[7]="pqrs";
         hash_map[8]="tuv";
         hash_map[9]="wxyz";
        if(digits.length()==0){
            return res;
        }
        
        string s;
        int i = 0;
        search(i,s,res,digits,hash_map);
        return res;
    }
    
    // bool isSafeCandidate(){}
    
    bool isValidState(int i,string digits){
        if(i == digits.length()){
            return true;
        }
        return false;
    }
    
    void search(int i,string s,vector<string> &res,string digits, vector<string> &hash_map){
       
        if(isValidState(i,digits)){
            res.push_back(s);
            return;
        }
        
        int x=digits[i]-'0';
        
        for(int j=0;j<hash_map[x].size();j++){
            s.push_back(hash_map[x][j]);
            search(i+1,s,res,digits,hash_map);
            s.pop_back();
        }
    }
};

// Method 2: Similar method but using Hashmap
   /*
    vector<string> letterCombinations(string digits) {
    unordered_map<char, string> myMap = {{'2',"abc"}, {'3',"def"}, {'4',"ghi"},
    {'5',"jkl"}, {'6',"mno"}, {'7',"pqrs"}, {'8',"tuv"}, {'9',"wxyz"}};
    vector<string> res;
    dfs(digits, myMap, 0, {}, res);
    return res;
}

void dfs(string digits, unordered_map<char, string> myMap, int idx, vector<char> path, vector<string>& res) {
    if (!path.empty() && path.size() == digits.size()) {
        string s = "";
        for (auto& c: path)
            s += c;
        res.push_back(s);
        return;
    }
    for (auto& c: myMap[digits[idx]]) {
        path.push_back(c);
        dfs(digits, myMap, idx+1, path, res);
        path.pop_back();
    }
}
   */
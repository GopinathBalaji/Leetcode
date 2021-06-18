class Solution {
public:
    bool wordPattern(string pattern, string s) {
      // MATCH CHARACTER OF 'PATTERN' TO EACH SUB-STRING OF 'S'
        
        vector<string> list;
        stringstream out(s);
        string word;
        while(out>>word){
            list.push_back(word);
        }
        unordered_map<char,string> m1;
        unordered_map<string,char> m2;
        
        int n = pattern.size();
        if(n!=list.size()){
            return false;
        }
        
        for(int i=0;i<n;i++){
            
            if(m1.find(pattern[i])==m1.end()){
                m1.insert({pattern[i],list[i]});
            }else{
                auto it = m1.find(pattern[i]);
                if(it->second!=list[i]){
                    return false;
                }
            }
            
            if(m2.find(list[i])==m2.end()){
                m2.insert({list[i],pattern[i]});
            }else{
                auto it2 = m2.find(list[i]);
                if(it2->second!=pattern[i]){
                    return false;
                }
            }
        }
        return true;
    }
};
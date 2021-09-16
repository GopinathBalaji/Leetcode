class Solution {
public:
    
    vector<string> restoreIpAddresses(string s) {
        vector<string> result;
        string temp;
        int start = 0;
        int part = 0;
        solve(s, temp, result, start, part);
        return result;
    }
    
    
    void solve(string s, string temp, vector<string> &result, int start, int part)
    {
        if(start == s.size() && part == 4)
        {
            result.push_back(temp);
            return;
        }
        
        for(int i = start; i < s.size(); i++)
        {
            if(part < 4 && i-start < 3 && isSafeCandidate(s, start, i))
            {
                temp.append(s.substr(start, i-start+1));
                part++;
                if(part < 4) temp.push_back('.');

                solve(s,temp,result, i+1, part);
                
                if(part < 4) temp.pop_back();
                part--;
                for(int j = 0; j < i-start+1; j++) temp.pop_back();
            }
        }
    }
    
    
    bool isSafeCandidate(string s, int start, int end)
    {
        string str = s.substr(start, end-start+1);
        int ip = stoll(str);
        
        if(s[start] == '0' && start != end) return false;
        else if(ip >= 0 && ip <= 255) return true;
        
        return false;
    }
};
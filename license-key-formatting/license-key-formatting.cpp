class Solution {
public:
    string licenseKeyFormatting(string s, int k) {
         string output;
        for(int i=s.length()-1;i>=0;i--){
            if(s[i] != '-'){
                output += toupper(s[i]);
                if(output.length()%(k+1) == k){
                    output += '-';
                }
            }
        }
        if(output.back() == '-'){
            output.pop_back();
        }
        reverse(output.begin(),output.end());
        
        return output;
    }
     /*
       OR
       
        string licenseKeyFormatting(string s, int k) {
        string ans;
        int count=0;
        for(int i=s.size()-1;i>=0;i--){
            if(s[i]=='-') continue;
            ans.push_back(toupper(s[i]));
            count++;
            if(count%k==0){
                ans.push_back('-');
                count=0;
            }
        }
        if(ans.back()=='-'){
            ans.pop_back();
        }
        reverse(ans.begin(),ans.end());
        return ans;
    }
     */
};

// Method 2: Insert '-' from the end after every 'k' characters
   /*
    string licenseKeyFormatting(string s, int k) {

    string temp = "";
	// removing '-' and convert all lower_case to upper_case
    for(int i = 0; i < s.length(); i++)
    {
        if(s[i] == '-')
            continue;
        if(isalpha(s[i]))
            temp += toupper(s[i]);
        else
            temp += s[i];
    }
    //cout<<temp<<endl;
    int count = 0;
    string ans = "";
    reverse(temp.begin(), temp.end());
	//inserting  '-' after every k character.
    for(int i = 0; i < temp.size(); i++)
    {
        if(count == k)
        {
            ans += '-';
            count = 0;
        }
        ans += temp[i];
        count++;
    }
   //cout<<ans<<endl;
    reverse(ans.begin(), ans.end());
    return ans;
}
   */
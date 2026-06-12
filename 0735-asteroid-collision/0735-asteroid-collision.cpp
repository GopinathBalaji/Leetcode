// Method 1: Using array to simulate
/*
*/
class Solution {
public:
    vector<int> asteroidCollision(vector<int>& asteroids) {
        vector<int> ans;

        for(int asteroid: asteroids){
            bool alive = true;

            while(!ans.empty() && ans.back() > 0 && asteroid < 0){
                if(ans.back() < -asteroid){
                    ans.pop_back();
                }else if(ans.back() == -asteroid){
                    ans.pop_back();
                    alive = false;
                    break;
                }else{
                    alive = false;
                    break;
                }
            }

            if(alive){
                ans.push_back(asteroid);
            }
        }

        return ans;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna
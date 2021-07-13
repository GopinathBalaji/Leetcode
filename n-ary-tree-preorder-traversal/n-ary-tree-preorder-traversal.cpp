/*
// Definition for a Node.
class Node {
public:
    int val;
    vector<Node*> children;

    Node() {}

    Node(int _val) {
        val = _val;
    }

    Node(int _val, vector<Node*> _children) {
        val = _val;
        children = _children;
    }
};
*/

class Solution {
public:
//     Method 1: Using 2 vectors
    vector<int> preorder(Node* root) {
        if(root==NULL){
            return {};
        }
        stack<Node*> s1;
        vector<int> v1;
        vector<Node*> temp;
        s1.push(root);
        
        while(!s1.empty()){
            Node* node = s1.top();
            v1.push_back(node->val);
            s1.pop();
            for(auto child:node->children){
              temp.push_back(child);
                cout<<child->val<<endl;
            }
            for(auto it=temp.rbegin();it != temp.rend();it++){
                s1.push(*it);
            }
            temp.clear();
        }
        return v1;
    }
};


// Method 2: Using Recursive DFS
   /*
    void solve(Node * root, vector<int> &ans) {
        if(!root) return;
        ans.push_back(root->val);
        for(int i=0;i<root->children.size();i++) {
            solve(root->children[i], ans);
        }
    }
    vector<int> preorder(Node* root) {
        vector<int> ans;
        solve(root, ans);
        return ans;
    }
   */

// Method 3: No extra space
   /*
     List result = new ArrayList<>();
    public List preorder(Node root) {
        if(root == null) return result;// if root is null return vacaant list
        Stack stack = new Stack<>();// else made a stack of node and store all the values in that
        stack.push(root);//add rootroot in stack
        while(!stack.isEmpty()){
            Node curr = stack.pop();
            result.add(curr.val);
            //pushing in children in reverse order
            for(int i=curr.children.size()-1;i>=0;i--){//starting from last child and go till 0th element we are doing this so we can store elements in reverse order
                stack.push(curr.children.get(i));//this wi push all the childrens of root
                
            }
        }
        return result;
    }
   */
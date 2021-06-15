/**
 * Definition for singly-linked list.
 * struct ListNode {
 *     int val;
 *     ListNode *next;
 *     ListNode() : val(0), next(nullptr) {}
 *     ListNode(int x) : val(x), next(nullptr) {}
 *     ListNode(int x, ListNode *next) : val(x), next(next) {}
 * };
 */
class Solution {
public:
    bool isPalindrome(ListNode* head) {
        if(head==NULL){
            return true;
        }
        
        ListNode* curr = head;
        string s = "";
        while(curr!=NULL){
            s.append(to_string(curr->val));
            curr = curr->next;
        }
      
        if(s == string(s.rbegin(),s.rend())){
            return true;
        }
        return false;
    }
};
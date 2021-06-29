/**
 * Definition for singly-linked list.
 * struct ListNode {
 *     int val;
 *     ListNode *next;
 *     ListNode(int x) : val(x), next(NULL) {}
 * };
 */
class Solution {
public:
    bool hasCycle(ListNode *head) {
//         Method 1: Use HashMap to check if node already exists. Not constant space
        
        unordered_map<ListNode*,bool> m1;
        ListNode* curr = head;
        while(curr!=NULL){
         if(m1.count(curr)!=0){
             return true;
         }
            m1[curr] = true;
            curr=curr->next;
        }
        return false;
    }
};


// Method 2: Fast and Slow Pointers (Floyd's cycle Detection Algorithm)
   /*
    ListNode* slow = head;
    ListNode* fast = head;
    
    while(fast && fast->next){
    fast = fast->next->next;
    slow = slow->next;
    if(fast==slow){
    return true;
    }
    }
    return false;
   */
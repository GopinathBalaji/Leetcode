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
    ListNode* deleteDuplicates(ListNode* head) {
//        Method 1: Slow and Fast Pointers approach
           if(head==NULL){
               return NULL;
           }
        
        ListNode* fast = head->next;
        ListNode* slow = head;
         while(fast!=NULL){
             if(fast->val != slow->val){
                slow = fast;
                 fast = fast->next;
             }else{
                 slow->next = fast->next;
                 delete fast;
                 fast = slow->next;
             }
         }
        return head;
    }
};

// Method 2: Using a single pointer
    /*
    public ListNode deleteDuplicates(ListNode head) {
    ListNode current = head;
    while (current != null && current.next != null) {
        if (current.next.val == current.val) {
            current.next = current.next.next;
        } else {
            current = current.next;
        }
    }
    return head;
}
    */

// Method 3: Recursion
   /*
   public ListNode deleteDuplicates(ListNode head) {

    // if head is null, there are no more chances of duplicates, so, just return
    if(head == null) return head; 

    // if my next node has same value as me, kick out next
    while(head.next != null && head.next.val == head.val){
        head.next = head.next.next;
    }

    // I have taken care of my duplicates, let my next node take care of it's duplicates
    head.next = deleteDuplicates(head.next);

    return head;
}
   */
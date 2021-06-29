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
    ListNode *getIntersectionNode(ListNode *headA, ListNode *headB) {
        if(headA==NULL || headB==NULL){
            ListNode* ans = new ListNode(0);
            return ans; 
        }
        
        ListNode* head1 = headA;
        ListNode* head2 = headB;
        int count1 = 0;
        int count2 = 0;
        while(head1!=NULL){
           count1++;
            head1 = head1->next;
        }
       while(head2!=NULL){
           count2++;
            head2 = head2->next;
        }
        
        int diff = 0;
        if(count1>count2){
            diff = count1-count2;
            while(diff!=0){
                headA = headA->next;
                diff--;
            }
        }else if(count2>count1){
            diff = count2-count1;
            while(diff!=0){
                headB = headB->next; 
                diff--;
            }
        }
        
        while(headA!=NULL and headB!=NULL){
            if(headA==headB){
                return headA;
            }
            headA = headA->next;
            headB = headB->next;
        }
        
        return NULL;
     
    }
};

// Method 2: Using HashSet
   /*
    ListNode *getIntersectionNode(ListNode *headA, ListNode *headB) {
         unordered_set <ListNode*> hash;
         while(headA || headB){
             if(headA ){
                 if (hash.find(headA) == hash.end()){
                     hash.insert(headA);
                     headA = headA->next;
                 }
                 else {return headA;}
             }
             if(headB ){
                 if (hash.find(headB) == hash.end()){
                     hash.insert(headB);
                     headB = headB->next;
                 }
                 else {return headB;}
             }  
        
         }
        return NULL;
    }
   */

// Method 3: Two Pointer
// Observation: The total number of nodes in the first list + distance of the head of 
// the second list from the intersection point = The total number of nodes in the second
// list + distance of the head of the first list from the intersection point.

// The idea is to take two pointers, x and y, initially pointing to the head of the 
// first and second lists. Then advance both pointers at the same pace until they meet
// at a common node. When x reaches its end, redirect it to the head of the second list.
// When y reaches its end, turn it to the head of the first list. The node where x meets
// y is the intersection node.
   /*
     public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        ListNode a=headA, b=headB;
        while(a!=b){
            if(a==null) a=headB;
            else  a=a.next;
            if(b==null) b=headA;
            else b=b.next;
        }
        return a;
    }
   */
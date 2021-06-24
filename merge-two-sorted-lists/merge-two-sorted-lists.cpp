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
    ListNode* mergeTwoLists(ListNode* l1, ListNode* l2) {
        if(l1==NULL){
            return l2;
        }
        if(l2==NULL){
            return l1;
        }
        if(l1==NULL && l2==NULL){
            return NULL;
        }
        
        vector<int> v1;
        
        ListNode* temp = l1;
        while(temp!=NULL){
            v1.push_back(temp->val);
            temp = temp->next;
        }
        
        ListNode* temp2 = l2;
        while(temp2!=NULL){
            v1.push_back(temp2->val);
            temp2 = temp2->next;
        }
        
        sort(v1.begin(),v1.end());
        
        ListNode* nodeptr = NULL;
        ListNode* rootptr = NULL;
        ListNode* lastptr = NULL;
        for(int i=0;i<v1.size();i++){
          if(!nodeptr){
              nodeptr = new ListNode(v1[i]);
              if(!rootptr){
                  rootptr = nodeptr;
              }
              if(lastptr){
                  lastptr->next = nodeptr;
              }
          }  
             lastptr = nodeptr;
              nodeptr = nodeptr->next;
        }
        return rootptr;
    }
};

// Method 2: Without using extra vector, i.e inplace sorting
   /*
    ListNode* mergeTwoLists(ListNode* l1, ListNode* l2) {
        if (l1==NULL) return l2;
        if (l2==NULL) return l1;
        ListNode* head,*prev;
        if (l1->val <= l2->val) {
            head = l1;
            l1 = l1->next;
        }
        else{
            head = l2;
            l2 = l2->next;
        }
        prev = head;
        while (l1!=NULL and l2!=NULL){
            if (l1->val <= l2->val) {
                prev->next = l1;
                l1 = l1->next;
                prev = prev->next;
            }
            else{
                prev->next = l2;
                l2 = l2->next;
                prev = prev->next;
            }
        }
        if (l1==NULL){
            prev->next = l2;
            return head;
        }
        prev->next = l1;
        return head;
        
    }
    */
    
//     Method 3: Resursive approach

       /*
        ListNode* mergeTwoLists(ListNode* l1, ListNode* l2)
    {
        if(l1 == NULL)
        {
            return l2;
        }
        if(l2 == NULL)
        {
            return l1;
        }
        
        if(l1->val <= l2->val)
        {
            l1->next = mergeTwoLists(l1->next, l2);
            return l1;
        }
        else
        {
            l2->next = mergeTwoLists(l1, l2->next);
            return l2;
        }
    }
       */
   
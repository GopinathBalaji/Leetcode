/*
// Definition for Employee.
class Employee {
public:
    int id;
    int importance;
    vector<int> subordinates;
};
*/

class Solution {
public:
    unordered_map<int,Employee*> m1;
    
    int getImportance(vector<Employee*> employees, int id) {
        for(Employee* e:employees){
            m1.insert({e->id,e});
        }
        return dfs(id);
    }
    
    int dfs(int id){
        Employee* emp = m1[id];
        int imp = emp->importance;
        for(int i:emp->subordinates){
            imp += dfs(i);
        }
        return imp;
    }
};

// Method 2: Iterative BFS
   /*
    int getImportance(vector<Employee*> employees, int id) {
    int answer = 0;
    
    unordered_map<int,Employee*>mp;                //construct graph
    for(int i = 0; i != employees.size(); i++) 
      mp[employees[i]->id] = employees[i];
    
    queue<int> q;                                  //BFS
    q.push(id);                                    //start BFS
    while(!q.empty()){
      id = q.front(); q.pop();                     //current nodes
      answer += mp[id]->importance;
      
      int limit = mp[id]->subordinates.size();     //for next level of BFS
      for(int i = 0; i != limit ;i++)
        q.push(mp[id]->subordinates[i]);
    }
    return answer;
  }
   */

// Method 3: Iterative DFS
   /*
    def getImportance(self, employees: List['Employee'], id: int) -> int:
	emp = {e.id:e for e in employees}
        
	stack = [emp[id]]
	total_imp = 0
	
	while stack:
		e = stack.pop()
		total_imp+=e.importance
		for s in e.subordinates:
			stack.append(emp[s])
	return total_imp
   */

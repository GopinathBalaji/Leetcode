<h2>1462. Course Schedule IV</h2><h3>Medium</h3><hr><div style="user-select: auto;"><p style="user-select: auto;">There are a total of <code style="user-select: auto;">numCourses</code> courses you have to take, labeled from <code style="user-select: auto;">0</code> to <code style="user-select: auto;">numCourses - 1</code>. You are given an array <code style="user-select: auto;">prerequisites</code> where <code style="user-select: auto;">prerequisites[i] = [a<sub style="user-select: auto;">i</sub>, b<sub style="user-select: auto;">i</sub>]</code> indicates that you <strong style="user-select: auto;">must</strong> take course <code style="user-select: auto;">a<sub style="user-select: auto;">i</sub></code> first if you want to take course <code style="user-select: auto;">b<sub style="user-select: auto;">i</sub></code>.</p>

<ul style="user-select: auto;">
	<li style="user-select: auto;">For example, the pair <code style="user-select: auto;">[0, 1]</code> indicates that you have to take course <code style="user-select: auto;">0</code> before you can take course <code style="user-select: auto;">1</code>.</li>
</ul>

<p style="user-select: auto;">Prerequisites can also be <strong style="user-select: auto;">indirect</strong>. If course <code style="user-select: auto;">a</code> is a prerequisite of course <code style="user-select: auto;">b</code>, and course <code style="user-select: auto;">b</code> is a prerequisite of course <code style="user-select: auto;">c</code>, then course <code style="user-select: auto;">a</code> is a prerequisite of course <code style="user-select: auto;">c</code>.</p>

<p style="user-select: auto;">You are also given an array <code style="user-select: auto;">queries</code> where <code style="user-select: auto;">queries[j] = [u<sub style="user-select: auto;">j</sub>, v<sub style="user-select: auto;">j</sub>]</code>. For the <code style="user-select: auto;">j<sup style="user-select: auto;">th</sup></code> query, you should answer whether course <code style="user-select: auto;">u<sub style="user-select: auto;">j</sub></code> is a prerequisite of course <code style="user-select: auto;">v<sub style="user-select: auto;">j</sub></code> or not.</p>

<p style="user-select: auto;">Return <i style="user-select: auto;">a boolean array </i><code style="user-select: auto;">answer</code><i style="user-select: auto;">, where </i><code style="user-select: auto;">answer[j]</code><i style="user-select: auto;"> is the answer to the </i><code style="user-select: auto;">j<sup style="user-select: auto;">th</sup></code><i style="user-select: auto;"> query.</i></p>

<p style="user-select: auto;">&nbsp;</p>
<p style="user-select: auto;"><strong style="user-select: auto;">Example 1:</strong></p>
<img alt="" src="https://assets.leetcode.com/uploads/2021/05/01/courses4-1-graph.jpg" style="width: 222px; height: 62px; user-select: auto;">
<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> numCourses = 2, prerequisites = [[1,0]], queries = [[0,1],[1,0]]
<strong style="user-select: auto;">Output:</strong> [false,true]
<strong style="user-select: auto;">Explanation:</strong> The pair [1, 0] indicates that you have to take course 1 before you can take course 0.
Course 0 is not a prerequisite of course 1, but the opposite is true.
</pre>

<p style="user-select: auto;"><strong style="user-select: auto;">Example 2:</strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> numCourses = 2, prerequisites = [], queries = [[1,0],[0,1]]
<strong style="user-select: auto;">Output:</strong> [false,false]
<strong style="user-select: auto;">Explanation:</strong> There are no prerequisites, and each course is independent.
</pre>

<p style="user-select: auto;"><strong style="user-select: auto;">Example 3:</strong></p>
<img alt="" src="https://assets.leetcode.com/uploads/2021/05/01/courses4-3-graph.jpg" style="width: 222px; height: 222px; user-select: auto;">
<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> numCourses = 3, prerequisites = [[1,2],[1,0],[2,0]], queries = [[1,0],[1,2]]
<strong style="user-select: auto;">Output:</strong> [true,true]
</pre>

<p style="user-select: auto;">&nbsp;</p>
<p style="user-select: auto;"><strong style="user-select: auto;">Constraints:</strong></p>

<ul style="user-select: auto;">
	<li style="user-select: auto;"><code style="user-select: auto;">2 &lt;= numCourses &lt;= 100</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">0 &lt;= prerequisites.length &lt;= (numCourses * (numCourses - 1) / 2)</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">prerequisites[i].length == 2</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">0 &lt;= a<sub style="user-select: auto;">i</sub>, b<sub style="user-select: auto;">i</sub> &lt;= n - 1</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">a<sub style="user-select: auto;">i</sub> != b<sub style="user-select: auto;">i</sub></code></li>
	<li style="user-select: auto;">All the pairs <code style="user-select: auto;">[a<sub style="user-select: auto;">i</sub>, b<sub style="user-select: auto;">i</sub>]</code> are <strong style="user-select: auto;">unique</strong>.</li>
	<li style="user-select: auto;">The prerequisites graph has no cycles.</li>
	<li style="user-select: auto;"><code style="user-select: auto;">1 &lt;= queries.length &lt;= 10<sup style="user-select: auto;">4</sup></code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">0 &lt;= u<sub style="user-select: auto;">i</sub>, v<sub style="user-select: auto;">i</sub> &lt;= n - 1</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">u<sub style="user-select: auto;">i</sub> != v<sub style="user-select: auto;">i</sub></code></li>
</ul>
</div>
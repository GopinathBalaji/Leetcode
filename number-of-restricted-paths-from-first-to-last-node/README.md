<h2>1786. Number of Restricted Paths From First to Last Node</h2><h3>Medium</h3><hr><div style="user-select: auto;"><p style="user-select: auto;">There is an undirected weighted connected graph. You are given a positive integer <code style="user-select: auto;">n</code> which denotes that the graph has <code style="user-select: auto;">n</code> nodes labeled from <code style="user-select: auto;">1</code> to <code style="user-select: auto;">n</code>, and an array <code style="user-select: auto;">edges</code> where each <code style="user-select: auto;">edges[i] = [u<sub style="user-select: auto;">i</sub>, v<sub style="user-select: auto;">i</sub>, weight<sub style="user-select: auto;">i</sub>]</code> denotes that there is an edge between nodes <code style="user-select: auto;">u<sub style="user-select: auto;">i</sub></code> and <code style="user-select: auto;">v<sub style="user-select: auto;">i</sub></code> with weight equal to <code style="user-select: auto;">weight<sub style="user-select: auto;">i</sub></code>.</p>

<p style="user-select: auto;">A path from node <code style="user-select: auto;">start</code> to node <code style="user-select: auto;">end</code> is a sequence of nodes <code style="user-select: auto;">[z<sub style="user-select: auto;">0</sub>, z<sub style="user-select: auto;">1</sub>,<sub style="user-select: auto;"> </sub>z<sub style="user-select: auto;">2</sub>, ..., z<sub style="user-select: auto;">k</sub>]</code> such that <code style="user-select: auto;">z<sub style="user-select: auto;">0 </sub>= start</code> and <code style="user-select: auto;">z<sub style="user-select: auto;">k</sub> = end</code> and there is an edge between <code style="user-select: auto;">z<sub style="user-select: auto;">i</sub></code> and <code style="user-select: auto;">z<sub style="user-select: auto;">i+1</sub></code> where <code style="user-select: auto;">0 &lt;= i &lt;= k-1</code>.</p>

<p style="user-select: auto;">The distance of a path is the sum of the weights on the edges of the path. Let <code style="user-select: auto;">distanceToLastNode(x)</code> denote the shortest distance of a path between node <code style="user-select: auto;">n</code> and node <code style="user-select: auto;">x</code>. A <strong style="user-select: auto;">restricted path</strong> is a path that also satisfies that <code style="user-select: auto;">distanceToLastNode(z<sub style="user-select: auto;">i</sub>) &gt; distanceToLastNode(z<sub style="user-select: auto;">i+1</sub>)</code> where <code style="user-select: auto;">0 &lt;= i &lt;= k-1</code>.</p>

<p style="user-select: auto;">Return <em style="user-select: auto;">the number of restricted paths from node</em> <code style="user-select: auto;">1</code> <em style="user-select: auto;">to node</em> <code style="user-select: auto;">n</code>. Since that number may be too large, return it <strong style="user-select: auto;">modulo</strong> <code style="user-select: auto;">10<sup style="user-select: auto;">9</sup> + 7</code>.</p>

<p style="user-select: auto;">&nbsp;</p>
<p style="user-select: auto;"><strong style="user-select: auto;">Example 1:</strong></p>
<img alt="" src="https://assets.leetcode.com/uploads/2021/02/17/restricted_paths_ex1.png" style="width: 351px; height: 341px; user-select: auto;">
<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> n = 5, edges = [[1,2,3],[1,3,3],[2,3,1],[1,4,2],[5,2,2],[3,5,1],[5,4,10]]
<strong style="user-select: auto;">Output:</strong> 3
<strong style="user-select: auto;">Explanation:</strong> Each circle contains the node number in black and its <code style="user-select: auto;">distanceToLastNode value in blue. </code>The three restricted paths are:
1) 1 --&gt; 2 --&gt; 5
2) 1 --&gt; 2 --&gt; 3 --&gt; 5
3) 1 --&gt; 3 --&gt; 5
</pre>

<p style="user-select: auto;"><strong style="user-select: auto;">Example 2:</strong></p>
<img alt="" src="https://assets.leetcode.com/uploads/2021/02/17/restricted_paths_ex22.png" style="width: 356px; height: 401px; user-select: auto;">
<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> n = 7, edges = [[1,3,1],[4,1,2],[7,3,4],[2,5,3],[5,6,1],[6,7,2],[7,5,3],[2,6,4]]
<strong style="user-select: auto;">Output:</strong> 1
<strong style="user-select: auto;">Explanation:</strong> Each circle contains the node number in black and its <code style="user-select: auto;">distanceToLastNode value in blue. </code>The only restricted path is 1 --&gt; 3 --&gt; 7.
</pre>

<p style="user-select: auto;">&nbsp;</p>
<p style="user-select: auto;"><strong style="user-select: auto;">Constraints:</strong></p>

<ul style="user-select: auto;">
	<li style="user-select: auto;"><code style="user-select: auto;">1 &lt;= n &lt;= 2 * 10<sup style="user-select: auto;">4</sup></code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">n - 1 &lt;= edges.length &lt;= 4 * 10<sup style="user-select: auto;">4</sup></code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">edges[i].length == 3</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">1 &lt;= u<sub style="user-select: auto;">i</sub>, v<sub style="user-select: auto;">i</sub> &lt;= n</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">u<sub style="user-select: auto;">i </sub>!= v<sub style="user-select: auto;">i</sub></code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">1 &lt;= weight<sub style="user-select: auto;">i</sub> &lt;= 10<sup style="user-select: auto;">5</sup></code></li>
	<li style="user-select: auto;">There is at most one edge between any two nodes.</li>
	<li style="user-select: auto;">There is at least one path between any two nodes.</li>
</ul>
</div>
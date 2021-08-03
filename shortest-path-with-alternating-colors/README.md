<h2>1129. Shortest Path with Alternating Colors</h2><h3>Medium</h3><hr><div style="user-select: auto;"><p style="user-select: auto;">Consider a directed graph, with nodes labelled <code style="user-select: auto;">0, 1, ..., n-1</code>.&nbsp; In this graph, each edge is either red or blue, and there could&nbsp;be self-edges or parallel edges.</p>

<p style="user-select: auto;">Each <code style="user-select: auto;">[i, j]</code> in <code style="user-select: auto;">red_edges</code> denotes a red directed edge from node <code style="user-select: auto;">i</code> to node <code style="user-select: auto;">j</code>.&nbsp; Similarly, each <code style="user-select: auto;">[i, j]</code> in <code style="user-select: auto;">blue_edges</code> denotes a blue directed edge from node <code style="user-select: auto;">i</code> to node <code style="user-select: auto;">j</code>.</p>

<p style="user-select: auto;">Return an array <code style="user-select: auto;">answer</code>&nbsp;of length <code style="user-select: auto;">n</code>,&nbsp;where each&nbsp;<code style="user-select: auto;">answer[X]</code>&nbsp;is&nbsp;the length of the shortest path from node <code style="user-select: auto;">0</code>&nbsp;to node <code style="user-select: auto;">X</code>&nbsp;such that the edge colors alternate along the path (or <code style="user-select: auto;">-1</code> if such a path doesn't exist).</p>

<p style="user-select: auto;">&nbsp;</p>
<p style="user-select: auto;"><strong style="user-select: auto;">Example 1:</strong></p>
<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> n = 3, red_edges = [[0,1],[1,2]], blue_edges = []
<strong style="user-select: auto;">Output:</strong> [0,1,-1]
</pre><p style="user-select: auto;"><strong style="user-select: auto;">Example 2:</strong></p>
<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> n = 3, red_edges = [[0,1]], blue_edges = [[2,1]]
<strong style="user-select: auto;">Output:</strong> [0,1,-1]
</pre><p style="user-select: auto;"><strong style="user-select: auto;">Example 3:</strong></p>
<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> n = 3, red_edges = [[1,0]], blue_edges = [[2,1]]
<strong style="user-select: auto;">Output:</strong> [0,-1,-1]
</pre><p style="user-select: auto;"><strong style="user-select: auto;">Example 4:</strong></p>
<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> n = 3, red_edges = [[0,1]], blue_edges = [[1,2]]
<strong style="user-select: auto;">Output:</strong> [0,1,2]
</pre><p style="user-select: auto;"><strong style="user-select: auto;">Example 5:</strong></p>
<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> n = 3, red_edges = [[0,1],[0,2]], blue_edges = [[1,0]]
<strong style="user-select: auto;">Output:</strong> [0,1,1]
</pre>
<p style="user-select: auto;">&nbsp;</p>
<p style="user-select: auto;"><strong style="user-select: auto;">Constraints:</strong></p>

<ul style="user-select: auto;">
	<li style="user-select: auto;"><code style="user-select: auto;">1 &lt;= n &lt;= 100</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">red_edges.length &lt;= 400</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">blue_edges.length &lt;= 400</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">red_edges[i].length == blue_edges[i].length == 2</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">0 &lt;= red_edges[i][j], blue_edges[i][j] &lt; n</code></li>
</ul></div>
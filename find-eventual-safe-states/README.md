<h2>802. Find Eventual Safe States</h2><h3>Medium</h3><hr><div style="user-select: auto;"><p style="user-select: auto;">We start at some node in a directed graph, and every turn, we walk along a directed edge of the graph. If we reach a terminal node (that is, it has no outgoing directed edges), we stop.</p>

<p style="user-select: auto;">We define a starting node to be <strong style="user-select: auto;">safe</strong> if we must eventually walk to a terminal node. More specifically, there is a natural number <code style="user-select: auto;">k</code>, so that we must have stopped at a terminal node in less than <code style="user-select: auto;">k</code> steps for <strong style="user-select: auto;">any choice of where to walk</strong>.</p>

<p style="user-select: auto;">Return <em style="user-select: auto;">an array containing all the safe nodes of the graph</em>. The answer should be sorted in <strong style="user-select: auto;">ascending</strong> order.</p>

<p style="user-select: auto;">The directed graph has <code style="user-select: auto;">n</code> nodes with labels from <code style="user-select: auto;">0</code> to <code style="user-select: auto;">n - 1</code>, where <code style="user-select: auto;">n</code> is the length of <code style="user-select: auto;">graph</code>. The graph is given in the following form: <code style="user-select: auto;">graph[i]</code> is a list of labels <code style="user-select: auto;">j</code> such that <code style="user-select: auto;">(i, j)</code> is a directed edge of the graph, going from node <code style="user-select: auto;">i</code> to node <code style="user-select: auto;">j</code>.</p>

<p style="user-select: auto;">&nbsp;</p>
<p style="user-select: auto;"><strong style="user-select: auto;">Example 1:</strong></p>
<img alt="Illustration of graph" src="https://s3-lc-upload.s3.amazonaws.com/uploads/2018/03/17/picture1.png" style="height: 171px; width: 600px; user-select: auto;">
<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> graph = [[1,2],[2,3],[5],[0],[5],[],[]]
<strong style="user-select: auto;">Output:</strong> [2,4,5,6]
<strong style="user-select: auto;">Explanation:</strong> The given graph is shown above.
</pre>

<p style="user-select: auto;"><strong style="user-select: auto;">Example 2:</strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> graph = [[1,2,3,4],[1,2],[3,4],[0,4],[]]
<strong style="user-select: auto;">Output:</strong> [4]
</pre>

<p style="user-select: auto;">&nbsp;</p>
<p style="user-select: auto;"><strong style="user-select: auto;">Constraints:</strong></p>

<ul style="user-select: auto;">
	<li style="user-select: auto;"><code style="user-select: auto;">n == graph.length</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">1 &lt;= n &lt;= 10<sup style="user-select: auto;">4</sup></code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">0 &lt;= graph[i].length &lt;= n</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">graph[i]</code> is sorted in a strictly increasing order.</li>
	<li style="user-select: auto;">The graph may contain self-loops.</li>
	<li style="user-select: auto;">The number of edges in the graph will be in the range <code style="user-select: auto;">[1, 4 * 10<sup style="user-select: auto;">4</sup>]</code>.</li>
</ul>
</div>
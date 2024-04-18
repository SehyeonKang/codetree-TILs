import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Main {
	
	static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	static StringBuilder sb = new StringBuilder();
	static StringTokenizer st;
	static int N, M;
	static ArrayList<ArrayList<Node>> adjList;
	static boolean[] visited;
	static int[] minDistance;
	static PriorityQueue<TourPackage> activatedPacakges;
	static HashMap<Integer, TourPackage> inactivatedPackages;
	
	static class Node implements Comparable<Node> {
		int end, weight;

		public Node(int end, int weight) {
			this.end = end;
			this.weight = weight;
		}

		@Override
		public int compareTo(Node o) {
			return Integer.compare(this.weight, o.weight);
		}
	}
	
	static class TourPackage implements Comparable<TourPackage> {
		int id, revenue, dest, cost, benefit;

		public TourPackage(int id, int revenue, int dest, int cost) {
			this.id = id;
			this.revenue = revenue;
			this.dest = dest;
			this.cost = cost;
			calculate();
		}
		
		private void calculate() {
			benefit = revenue - cost;
		}

		@Override
		public int compareTo(TourPackage o) {
			return this.benefit != o.benefit ? o.benefit - this.benefit : this.id - o.id;
		}
		
		@Override
		public boolean equals(Object o) {
			TourPackage oPackage = (TourPackage) o;
			return this.id == oPackage.id;
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		int Q = Integer.parseInt(br.readLine());
		activatedPacakges = new PriorityQueue<>();
		inactivatedPackages = new HashMap<>();
		
		for (int i = 0; i < Q; i++) {
			st = new StringTokenizer(br.readLine());
			int action = Integer.parseInt(st.nextToken());
			int id = -1;
			switch (action) {
			case 100 :
				buildCodeTreeLand(st);
				break;
			case 200 :
				id = Integer.parseInt(st.nextToken());
				int revenue = Integer.parseInt(st.nextToken());
				int dest = Integer.parseInt(st.nextToken());
				createTourPackage(id, revenue, dest);
				break;
			case 300 :
				id = Integer.parseInt(st.nextToken());
				cancelTourPackage(id);
				break;
			case 400 :
				sellTourPackage();
				break;
			case 500 :
				int start = Integer.parseInt(st.nextToken());
				changeTourPackageStart(start);
				break;
			}
		}
		
		System.out.println(sb.toString());
	}
	
	// 코드트리 랜드 건설
	private static void buildCodeTreeLand(StringTokenizer st) throws Exception{
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		
		adjList = new ArrayList<>();
		for (int i = 0; i < N; i++) {
			adjList.add(new ArrayList<>());
		}
		
		for (int i = 0; i < M; i++) {
			int v = Integer.parseInt(st.nextToken());
			int u = Integer.parseInt(st.nextToken());
			int w = Integer.parseInt(st.nextToken());
			adjList.get(v).add(new Node(u, w));
			adjList.get(u).add(new Node(v, w));
		}
		
		dijkstra(0);
	}
	
	// 최단경로 생성
	private static void dijkstra(int start) {
		visited = new boolean[N];
		minDistance = new int[N];
		Arrays.fill(minDistance, Integer.MAX_VALUE);
		minDistance[start] = 0;
		
		PriorityQueue<Node> pq = new PriorityQueue<>();
		pq.offer(new Node(start, 0));
		
		while (!pq.isEmpty()) {
			Node curNode = pq.poll();
			int cur = curNode.end;
			
			if (!visited[cur]) {
				visited[cur] = true;
				
				for (Node node : adjList.get(cur)) {
					if (!visited[node.end] && minDistance[node.end] > minDistance[cur] + node.weight) {
						minDistance[node.end] = minDistance[cur] + node.weight;
						pq.offer(new Node(node.end, minDistance[node.end]));
					}
				}
			}
		}
	}
	
	// 여행 상품 생성
	private static void createTourPackage(int id, int revenue, int dest) {
		TourPackage tourPackage = new TourPackage(id, revenue, dest, minDistance[dest]);
		if (minDistance[dest] == Integer.MAX_VALUE || minDistance[dest] > revenue) {
			inactivatedPackages.put(id, tourPackage);
		} else {
			activatedPacakges.offer(tourPackage);
		}
	}
	
	// 여행 상품 취소
	private static void cancelTourPackage(int id) {
		if (inactivatedPackages.containsKey(id)) {
			inactivatedPackages.remove(id);
		} else if (activatedPacakges.contains(new TourPackage(id, 0, 0, 0))) {
			activatedPacakges.remove(new TourPackage(id, 0, 0, 0));
		}
	}
	
	// 최적의 여행 상품 판매
	private static void sellTourPackage() {
		if (activatedPacakges.isEmpty()) {
			sb.append("-1\n");
		} else {
			sb.append(activatedPacakges.poll().id + "\n");
		}
	}
	
	// 여행 상품의 출발지 변경
	private static void changeTourPackageStart(int start) {
		dijkstra(start);
		TourPackage[] tmp = new TourPackage[activatedPacakges.size() + inactivatedPackages.size()];
		
		int idx = 0;
		while (!activatedPacakges.isEmpty()) {
			tmp[idx++] = activatedPacakges.poll();
		}
		for (int id : inactivatedPackages.keySet()) {
			tmp[idx++] = inactivatedPackages.get(id);
		}
		inactivatedPackages.clear();
		
		for (TourPackage tourPackage : tmp) {
			int id = tourPackage.id;
			int revenue = tourPackage.revenue;
			int dest = tourPackage.dest;
			createTourPackage(id, revenue, dest);
		}
	}
}
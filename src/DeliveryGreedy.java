import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by Aksenov239 on 11.02.2016.
 */
public class DeliveryGreedy {
    BufferedReader br;
    StringTokenizer st;
    PrintWriter out;


    public String nextToken() throws IOException {
        while (st == null || !st.hasMoreTokens()) {
            st = new StringTokenizer(br.readLine());
        }
        return st.nextToken();
    }

    public int nextInt() throws IOException {
        return Integer.parseInt(nextToken());
    }

    int p;
    int[] w;
    int load;
    Place dist_to;

    public class Place implements Comparable<Place> {
        int x, y;
        int id;
        int[] cnt;

        public Place(int x, int y, int id) {
            this.x = x;
            this.y = y;
            this.id = id;
            cnt = new int[p];
        }

        public int compareTo(Place place) {
            return dist(this, dist_to) - dist(place, dist_to);
        }
    }

    public int dist(Place a, Place b) {
        return (int) Math.ceil(Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y)));
    }

    public class Drone implements Comparable<Drone> {
        int t, id;
        Place pos;

        ArrayList<String> operations = new ArrayList<>();

        Queue<Product> load_order;
        Queue<Product> drop_order;

        public Drone(Place place, int id) {
            pos = place;
            t = 0;
            this.id = id;
        }

        public boolean free() {
            return load_order.size() + drop_order.size() == 0;
        }

        public void operate() {
            if (free())
                return;
            if (load_order.size() != 0) {
                Product product = load_order.peek();
                operations.add(id + " L " + product.place.id + " " + product.type + " " + product.cnt);
                int dist = dist(product.place, pos);
                t += dist + 1;
                pos = product.place;
            } else {
                Product product = drop_order.peek();
                operations.add(id + " D " + product.place.id + " " + product.type + " " + product.cnt);
                int dist = dist(product.place, pos);
                t += dist + 1;
                pos = product.place;
            }
        }

        public int compareTo(Drone drone) {
            return this.t == drone.t ? this.id - drone.id : this.t - drone.t;
        }
    }

    static int id_gen = 0;

    public class Product implements Comparable<Product> {
        int type, cnt, dist;
        Place place;
        int id;

        public Product(int type, int cnt, Place place, int dist) {
            this.type = type;
            this.cnt = cnt;
            this.id = id_gen++;
            this.place = place;
            this.dist = dist;
        }

        public int compareTo(Product prod) {
            return this.dist == prod.dist ? this.id - prod.id : this.dist - prod.dist;
        }
    }

    int[] total;
    Place[] warehouses, clients;

    Drone[] drones;
    int turns;

    public class Edge implements Comparable<Edge> {
        int u, v;
        int type, cnt;

        public Edge(int u, int v, int type, int cnt) {
            this.u = u;
            this.v = v;
            this.type = type;
            this.cnt = cnt;
        }

        public int compareTo(Edge edge) {
            return v == edge.v ? type - edge.type : v - edge.v;
        }

        public int length() {
            return dist(warehouses[u], clients[v]);
        }
    }

    Comparator<Edge> by_dist = new Comparator<Edge>() {
        @Override
        public int compare(Edge o1, Edge o2) {
            return o1.length() - o2.length();
        }
    };

    public void emulate(ArrayList<Edge>[] edges) {
        for (ArrayList<Edge> e : edges) {
            Collections.sort(e);
        }

        ArrayList<Edge> all = new ArrayList<>();
        for (ArrayList<Edge> e : edges) {
            all.addAll(e);
        }
        Collections.sort(all, by_dist);

        Queue<Edge> order = new ArrayDeque<>();
        order.addAll(all);

        TreeSet<Drone> set = new TreeSet<>();
        for (int i = 0; i < drones.length; i++) {
            set.add(drones[i]);
        }

        for (int t = 0; t < turns; t++) {
            while (set.first().t == t) {
                Drone droid = set.pollFirst();
                if (droid.free()) {
                    Queue<Product> load = new ArrayDeque<>();
                    Queue<Product> drop = new ArrayDeque<>();
                    int left = this.load;
                    Edge last = null;
                    while (last == null || (order.peek().u == last.u && order.peek().v == last.v)) {
                        last = order.peek();
                        int could = Math.min(left / w[last.type], last.cnt);
                        if (could == 0)
                            break;
                        if (could == last.cnt) {
                            order.poll();
                        }
                        load.add(new Product(last.type, last.cnt, warehouses[last.u], 0));
                        drop.add(new Product(last.type, last.cnt, clients[last.v], 0));
                        last.cnt -= could;
                        left -= could * w[last.type];
                    }
                    droid.load_order = load;
                    droid.drop_order = drop;
                }
                if (droid.free())
                    continue;
                droid.operate();
                set.add(droid);
            }
        }
    }

    public void solve() throws IOException {
        int n = nextInt();
        int m = nextInt();

        int drones = nextInt();

        turns = nextInt();

        load = nextInt();

        p = nextInt();
        w = new int[p];
        for (int i = 0; i < p; i++) {
            w[i] = nextInt();
        }

        int nw = nextInt();
        warehouses = new Place[nw];
        for (int i = 0; i < nw; i++) {
            warehouses[i] = new Place(nextInt(), nextInt(), i);

            for (int j = 0; j < p; j++) {
                warehouses[i].cnt[j] = nextInt();
            }
        }

        int nc = nextInt();
        clients = new Place[nc];
        int[] total = new int[p];
        for (int i = 0; i < nc; i++) {
            clients[i] = new Place(nextInt(), nextInt(), i);

            int k = nextInt();
            for (int j = 0; j < k; j++) {
                int type = nextInt();
                clients[i].cnt[type]++;
            }
        }

        this.drones = new Drone[drones];
        for (int i = 0; i < drones; i++) {
            this.drones[i] = new Drone(warehouses[0], i);
        }

        br = new BufferedReader(new FileReader("busy_day_out.txt"));

        ArrayList<Edge>[] edges = new ArrayList[warehouses.length];
        for (int i = 0; i < warehouses.length; i++) {
            edges[i] = new ArrayList<>();
        }

        m = nextInt();
        for (int i = 0; i < m; i++) {
            Edge e = new Edge(nextInt(), nextInt(), nextInt(), nextInt());
            edges[e.u].add(e);
        }

        emulate(edges);

        for (int i = 0; i < drones; i++) {
            for (String commands : this.drones[i].operations) {
                out.println(commands);
            }
        }
    }

    public void run() {
        try {
            br = new BufferedReader(new FileReader("busy_day.in"));
            out = new PrintWriter("output.txt");

            solve();

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new DeliveryGreedy().run();
    }
}

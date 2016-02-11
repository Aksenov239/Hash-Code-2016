import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

/**
 * Created by Aksenov239 on 11.02.2016.
 */
public class Delivery {
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

    public class Place {
        int x, y;
        int[] cnt;

        public Place() {
            cnt = new int[p];
        }
    }

    public int dist(Place a, Place b) {
        return (int)Math.ceil(Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y)));
    }

    public class Drone implements Comparable<Drone>{
        int t;
        Place pos;
        int[] carry;
        int now;
        int id;

        public Drone(Place place, int id) {
            pos = place;
            carry = new int[p];
            now = 0;
            t = 0;
            this.id = id;
        }

        public void load(Place place, int kind, int cnt) {
            t += dist(pos, place) + 1;
            now += w[kind] * cnt;
            carry[kind] += cnt;
            pos = place;
        }

        public void unload(Place place, int kind, int cnt) {
            t += dist(pos, place) + 1;
            now -= w[kind] * cnt;
            carry[kind] -= cnt;
            pos = place;
        }

        public int compareTo(Drone drone) {
            return this.t - drone.t;
        }
    }

    int[] total;
    Place[] warehouses, clients;

    public long totalTime(int x) {


        for (int i = 0; i < warehouses.length; i++) {

        }
        return 0L;
    }

    public void solve() throws IOException {
        int n = nextInt();
        int m = nextInt();

        int drones = nextInt();

        int turns = nextInt();

        p = nextInt();
        w = new int[p];
        for (int i = 0; i < p; i++) {
            w[i] = nextInt();
        }

        int nw = nextInt();
        warehouses = new Place[nw];
        for (int i = 0; i < nw; i++) {
            warehouses[i] = new Place();
            warehouses[i].x = nextInt();
            warehouses[i].y = nextInt();

            for (int j = 0; j < p;j++) {
                warehouses[i].cnt[j] = nextInt();
            }
        }

        int nc = nextInt();
        clients = new Place[nc];
        int[] total = new int[p];
        for (int i = 0; i < nc; i++) {
            clients[i] = new Place();
            clients[i].x = nextInt();
            clients[i].y = nextInt();

            for (int j = 0; j < p;j++) {
                clients[i].cnt[j] = nextInt();
                total[j] += clients[i].cnt[j];
            }
        }
    }

    public void run() {
        try {
            br = new BufferedReader(new FileReader(""));
            out = new PrintWriter("output.txt");

            solve();

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

import scpsolver.problems.LPSolution;
import scpsolver.problems.LPWizard;
import scpsolver.problems.LPWizardConstraint;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by Aksenov239 on 11.02.2016.
 */
public class DeliveryLP {
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

    public class Edge {
        int u, v;
        int type;
        long cnt;

        public Edge(int u, int v, int type, long cnt) {
            this.u = u;
            this.v = v;
            this.type = type;
            this.cnt = cnt;
        }
    }

    public void solve() throws IOException {
        int n = nextInt();
        int m = nextInt();

        int drones = nextInt();

        int turns = nextInt();
        int load = nextInt();

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
        for (int i = 0; i < nc; i++) {
            clients[i] = new Place(nextInt(), nextInt(), i);

            int k = nextInt();
            for (int j = 0; j < k; j++) {
                int type = nextInt();
                clients[i].cnt[type]++;
            }
        }


        LPWizard lpw = new LPWizard();
        for (int i = 0; i < nw; i++) {
            for (int j = 0; j < nc; j++) {
                for (int k = 0; k < p; k++) {
                    // from warehouse x to client j number of items of type k
                    String varName = "x_" + i + "," + j + "," + k;
                    if (warehouses[i].cnt[k] * clients[i].cnt[k] == 0)
                        continue;
                    double dist = dist(warehouses[i], clients[j]);
                    lpw.plus(varName, dist * w[k]);
                    String constrName = "gz_" + i + "," + j + "," + k;
                    lpw.addConstraint(constrName, 0, "<=").plus(varName);
                }
            }
        }

        System.err.println("Wizard has built!");

        for (int i = 0; i < nw; i++) {
            for (int k = 0; k < p; k++) {
                // Summary number of items of type k delivered from warehouse i should be less than
                // warehouse[i].cnt[k]
                if (warehouses[i].cnt[k] == 0)
                    continue;
                String constrName = "b" + i + "," + k;
                LPWizardConstraint constraint = lpw.addConstraint(constrName, warehouses[i].cnt[k], ">=");
                for (int j = 0; j < nc; j++) {
                    if (clients[j].cnt[k] == 0)
                        continue;
                    String varname = "x_" + i + "," + j + "," + k;
                    constraint.plus(varname);
                }
                //constraint.setAllVariablesInteger();
            }
        }

        for (int j = 0; j < nc; j++) {
            for (int k = 0; k < p; k++) {
                // Summary number of items of type k delivered to client j should be at least than
                // client[j].cnt[k]
                String constrName = "c" + j + "," + k;
                if (clients[j].cnt[k] == 0)
                    continue;
                LPWizardConstraint constraint = lpw.addConstraint(constrName, clients[j].cnt[k], "<=");
                for (int i = 0; i < nw; i++) {
                    if (warehouses[i].cnt[k] == 0)
                        continue;
                    String varname = "x_" + i + "," + j + "," + k;
                    constraint.plus(varname);
                }
                //constraint.setAllVariablesInteger();
            }
        }

        lpw.setMinProblem(true);
        System.err.println("finish construct lp");
        LPSolution solution = lpw.solve();

        System.err.println("finish solve lp");
        ArrayList<Edge> tasks = new ArrayList<>();
        for (int i = 0; i < nw; i++) {
            for (int j = 0; j < nc; j++) {
                for (int k = 0; k < p; k++) {
                    // from warehouse x to client j number of items of type k
                    String varName = "x_" + i + "," + j + "," + k;
                    if (warehouses[i].cnt[k] * clients[i].cnt[k] == 0)
                        continue;
                    long cnt = solution.getInteger(varName);
                    if (cnt > 0) {
                        tasks.add(new Edge(i, j, k, cnt));
                        out.println(i + " " + j + " " + k + " " + cnt);
                    }
                }
            }
        }

        // out.println(solution.toString());
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
        new DeliveryLP().run();
    }
}

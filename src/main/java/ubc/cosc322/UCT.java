package ubc.cosc322;

import java.util.Collections;
import java.util.Comparator;

public class UCT {
    public static double uctValue(int totalVisit, double nodeWinScore, int nodeVisit) {
            if (nodeVisit == 0) {
                return Integer.MAX_VALUE;
            }
            return ((double) nodeWinScore / (double) nodeVisit)
                + 1.41 * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
    }

    public static Node findBestNodeWithUCT(Node node) {
        int parentVisit = node.getVisitCount();
        return Collections.max(node.getChildArray(), // list of nodes
                Comparator.comparing( c -> uctValue(parentVisit, c.getWinScore(), c.getVisitCount())));
    }
}

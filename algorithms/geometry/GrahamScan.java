package algorithms.geometry;

import java.util.*;

/**
 * Created by Rene Argento on 17/11/17.
 */
// Computes the convex hull taking into account collinear points.
public class GrahamScan {

    private static class Point {
        int x;
        int y;

        Point(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    public static List<Point> getConvexHull(Point[] points) {
        if (points.length < 2) {
            return Arrays.asList(points);
        }

        // 1- Find the lowest point in the plane. If there are multiple lowest points then pick the leftmost one.
        Point pivot = points[0];
        for (int i = 1; i < points.length; i++) {
            if (points[i].y < pivot.y) {
                pivot = points[i];
            } else if (points[i].y == pivot.y && points[i].x < pivot.x) {
                pivot = points[i];
            }
        }

        // 2 - Sort points according to the angle formed with the pivot
        sortToHandleCollinear(points, pivot);

        // 3- Find points in convex hull
        Stack<Point> stack = new Stack<>();
        stack.push(points[0]);
        stack.push(points[1]);

        for (int i = 2; i < points.length; i++) {
            Point top = stack.pop();
            // Second point will always be in answer so this will never cause empty stack exception.
            // As long as points[i] is on the right of vector stack.peek() -> top, keep getting rid of top of stack.
            while (crossProduct(stack.peek(), top, points[i]) < 0) {
                top = stack.pop();
            }

            stack.push(top);
            stack.push(points[i]);
        }

        return new ArrayList<>(stack);
    }

    private static void sortToHandleCollinear(Point[] points, final Point pivot) {
        Arrays.sort(points, new Comparator<Point>() {
            @Override
            public int compare(Point p1, Point p2) {
                if (p1 == pivot) {
                    return -1;
                }
                if (p2 == pivot) {
                    return 1;
                }

                int crossProduct = crossProduct(pivot, p1, p2);
                if (crossProduct == 0) {
                    return distance(pivot, p1, p2);
                } else {
                    return -crossProduct;
                }
            }
        });

        // Make sure last collinear points are in reverse order of distance.
        Point p = points[0];
        Point q = points[points.length - 1];
        int i = points.length - 2;

        while (i >= 0 && crossProduct(p, q, points[i]) == 0) {
            i--;
        }

        // Reverse sort order of collinear points in the end positions
        for (int left = i + 1, right = points.length - 1; left < right; left++, right--) {
            Point temp = points[left];
            points[left] = points[right];
            points[right] = temp;
        }
    }

    /**
     * Returns < 0 if 'b' is closer to 'a' compared to 'c', == 0 if 'b' and 'c' are same distance from 'a'
     * or > 0 if 'c' is closer to 'a' compared to 'b'.
     */
    private static int distance(Point a, Point b, Point c) {
        int y1 = a.y - b.y;
        int y2 = a.y - c.y;
        int x1 = a.x - b.x;
        int x2 = a.x - c.x;

        return Integer.compare(y1 * y1 + x1 * x1, y2 * y2 + x2 * x2);
    }

    /**
     * Cross product to find where c belongs in reference to vector ab.
     * If result > 0 it means 'c' is on left of ab
     *    result == 0 it means 'a','b' and 'c' are collinear
     *    result < 0  it means 'c' is on right of ab
     */
    private static int crossProduct(Point a, Point b, Point c) {
        int y1 = a.y - b.y;
        int y2 = a.y - c.y;
        int x1 = a.x - b.x;
        int x2 = a.x - c.x;

        return y2 * x1 - y1 * x2;
    }

}

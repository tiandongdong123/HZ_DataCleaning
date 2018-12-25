package com.wanfang.datacleaning.handler.util;

import java.awt.geom.Point2D;
import java.util.List;

/**
 * @author yifei
 * @date 2018/12/16
 */
public class PolygonUtils {

    private PolygonUtils() {
    }

    /**
     * 判断点是否在多边形内，如果点位于多边形的顶点或边上，也算做点在多边形内，直接返回true
     *
     * @param point 检测点
     * @param pts   多边形的顶点
     * @return 点在多边形内返回true, 否则返回false
     */
    public static boolean isPtInPoly(Point2D.Double point, List<Point2D.Double> pts) {
        int ptSize = pts.size();
        // 如果点位于多边形的顶点或边上，也算做点在多边形内，直接返回true
        boolean boundOrVertex = true;
        // cross points count of x
        int intersectCount = 0;
        // 浮点类型计算时候与0比较时候的容差
        double precision = 2e-10;
        // neighbour bound vertices
        Point2D.Double p1, p2;
        // 当前点
        Point2D.Double p = point;
        // left vertex
        p1 = pts.get(0);
        // check all rays
        for (int i = 1; i <= ptSize; ++i) {
            if (p.equals(p1)) {
                //  p is an vertex
                return boundOrVertex;
            }
            // right vertex
            p2 = pts.get(i % ptSize);
            // ray is outside of our interests
            if (p.x < Math.min(p1.x, p2.x) || p.x > Math.max(p1.x, p2.x)) {
                p1 = p2;
                // next ray left point
                continue;
            }
            // ray is crossing over by the algorithm (common part of)
            if (p.x > Math.min(p1.x, p2.x) && p.x < Math.max(p1.x, p2.x)) {
                // x is before of ray
                if (p.y <= Math.max(p1.y, p2.y)) {
                    // overlies on a horizontal ray
                    if (p1.x == p2.x && p.y >= Math.min(p1.y, p2.y)) {
                        return boundOrVertex;
                    }
                    // ray is vertical
                    if (p1.y == p2.y) {
                        // overlies on a vertical ray
                        if (p1.y == p.y) {
                            return boundOrVertex;
                            // before ray
                        } else {
                            ++intersectCount;
                        }
                        // cross point on the left side
                    } else {
                        // cross point of y
                        double xInters = (p.x - p1.x) * (p2.y - p1.y) / (p2.x - p1.x) + p1.y;
                        // overlies on a ray
                        if (Math.abs(p.y - xInters) < precision) {
                            return boundOrVertex;
                        }
                        // before ray
                        if (p.y < xInters) {
                            ++intersectCount;
                        }
                    }
                }
                // special case when ray is crossing through the vertex
            } else {
                // p crossing over p2
                if (p.x == p2.x && p.y <= p2.y) {
                    // next vertex
                    Point2D.Double p3 = pts.get((i + 1) % ptSize);
                    // p.x lies between p1.x & p3.x
                    if (p.x >= Math.min(p1.x, p3.x) && p.x <= Math.max(p1.x, p3.x)) {
                        ++intersectCount;
                    } else {
                        intersectCount += 2;
                    }
                }
            }
            // next ray left point
            p1 = p2;
        }

        // 若偶数，则在多边形外；若奇数，则在多边形内
        if (intersectCount % 2 == 0) {
            return false;
        } else {
            return true;
        }
    }
}

package com.example.imagingnavigator.function;
import java.util.*;

/**
 * Created by kangkang on 11/28/15.
 * This class will provide util functions for navigation
 * 1. Calculate next step location according to current position
 *  and pre-calculated path
 * 2. Calculate next step orientation according to current position
 *  and next step position
 */
public class Navigator {
    private static final String TAG = Navigator.class.getSimpleName();

    //Compute the dot product AB . AC
    // A->B stands for the segment, C stands for current location
    static private double dotProduct(double[] pointA, double[] pointB, double[] pointC)
    {
        double[] AB = new double[2];
        double[] BC = new double[2];
        AB[0] = pointB[0] - pointA[0];
        AB[1] = pointB[1] - pointA[1];
        BC[0] = pointC[0] - pointB[0];
        BC[1] = pointC[1] - pointB[1];
        double dot = AB[0] * BC[0] + AB[1] * BC[1];

        return dot;
    }

    //Compute the cross product AB x AC
    // A->B stands for the segment, C stands for current location
    static private double CrossProduct(double[] pointA, double[] pointB, double[] pointC)
    {
        double[] AB = new double[2];
        double[] AC = new double[2];
        AB[0] = pointB[0] - pointA[0];
        AB[1] = pointB[1] - pointA[1];
        AC[0] = pointC[0] - pointA[0];
        AC[1] = pointC[1] - pointA[1];
        double cross = AB[0] * AC[1] - AB[1] * AC[0];

        return cross;
    }

    //Compute the distance from A to B
    static double distance(double[] pointA, double[] pointB)
    {
        double d1 = pointA[0] - pointB[0];
        double d2 = pointA[1] - pointB[1];

        return Math.sqrt(d1 * d1 + d2 * d2);
    }

    //Compute the distance from AB to C
    // A->B stands for the segment, C stands for current location
    static private double pointToSegmentDistance(double[] pointA, double[] pointB, double[] pointC)
    {
        double dist = CrossProduct(pointA, pointB, pointC) / distance(pointA, pointB);

        double dot1 = dotProduct(pointA, pointB, pointC);
        if (dot1 > 0)
            return distance(pointB, pointC);

        double dot2 = dotProduct(pointB, pointA, pointC);
        if (dot2 > 0)
            return distance(pointA, pointC);

        return Math.abs(dist);
    }

    /*
     * 1. First we find the nearest segment among all steps in the path, we take this is the current
     *  segment user belongs to according to current position
     * 2. Then we take the second point of that segment to be that target location
     */
    static public double[] getTargetPoint(List<double[]> path,
                                   double[] curPosition){
        double min = Double.MAX_VALUE;
        double dist;
        double[] target = path.get(0);
        for(int i = 1; i < path.size(); i++){
            // For each segement [path[i-1], path[i]], calculate distance from current position.
            // Keep recording the min distance, as well as the ending point of that segment.
            dist = pointToSegmentDistance(path.get(i-1), path.get(i), curPosition);
            if(dist < min){
                min = dist;
                target = path.get(i);
            }
        }

        return target;
    }

    static public Step getCurrentStep(List<Step> steps, double[] curPosition){
        double min = Double.MAX_VALUE;
        double dist;
        Step result = steps.get(0);
        Step cur;

        for(int i = 0; i < steps.size(); i++){
            cur = steps.get(i);
            dist = pointToSegmentDistance(cur.getStart(), cur.getEnd(), curPosition);
            if(dist < min){
                min = dist;
                result = cur;
            }
        }

        return result;
    }


    /*
     * This function will return the angle from given position to the target position
     * The angle is North based and in the direction of clockwise
     */
    static public double getTargetAngle(double[] cur, double[] target){
        // Get angle from current position to target position
        // Based on the orientation of x axis
        // It's anticlockwise
        double angle = (double) Math.toDegrees(Math.atan2(
                target[1] - cur[1], target[0] - cur[0]));

        // Convert the angle from x axis based to north based (y axis based)
        // Still anticolckwise
        angle -= 90.0D;
        angle = angle<0 ? 360+angle : angle;

        // Convert the angle to be clockwise
        // Which is used by ImageView.rotate()
        return 360-angle;
    }

    /**
     * This function will return remaining duration time for current step
     * according to given current step and current location information.
     * */
    static public int getRemainingDuration(Step step, double[] curLoc){
        int duration = step.getDuration();
        if(duration <= 60 ){
            // for short step, do not calculate
            return duration;
        }

        double percentage = distance(curLoc, step.getEnd()) /
                distance(step.getStart(), step.getEnd());

        int result = (int)(step.getDuration() * percentage);

        return result;
    }

    /**
     * This function will return the estimate time to arriving
     * with given current step, current location
     * */
    static public int getETA(List<Step> steps, Step curStep, double[] curLoc){
        int eta = getRemainingDuration(curStep, curLoc);

        int index = steps.indexOf(curStep);
        for(int i = index+1; i < steps.size(); i++){
            eta += steps.get(i).getDuration();
        }
        return eta;
    }
}

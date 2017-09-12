package com.example.xigu.testopencv001.utils;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/11.
 */

public class OpenCVUtils {
    public static final  int VERTICAL =0;
    public static final  int HORITICAL = 1;
    public static boolean verifySizes(Mat r, int x, int y){
        //Char sizes 155 x 60
        int dirct = VERTICAL;
        float aspect = (float) (155.0 / 60.0);
        float charAspect = dirct==1 ? ((float)r.cols() / r.rows()) : ((float)r.rows() / r.cols());
        float error = (float) 0.5;
        float minHeight = 15,  maxHeight = y;
        float minWidth = 15,  maxWidth = x;
        //We have a different aspect ratio for number 1, and it can be ~0.2
        float minAspect = (float) 0.6;
        float maxAspect = aspect + aspect*error;

        //bb area
        float bbArea = r.cols()*r.rows();

        if (bbArea > 100 && charAspect > minAspect && charAspect < maxAspect
                && r.rows() >= minHeight && r.rows() <= maxHeight
                && r.cols() >= minWidth && r.cols() <= maxWidth)
            return true;
        else
            return false;
    }


    public void sss(){
        Mat outImg = new Mat(500,1000, CvType.CV_32FC3,new Scalar(0,0,0));

        //make obj test points
        List<Point> p1 = new ArrayList<Point>();
        for(int i=0;i<50;i++){
            //p1.add(new OpenCVTemplateMatcher().generateRandom2DPoint(50,50,450,450));

        }
        //make scene test points
        List<Point> p2 = new ArrayList<Point>();
        for(int i=0;i<50;i++){
            //p2.add(new OpenCVTemplateMatcher().generateRandom2DPoint(550,50,950,450));
        }

        System.out.println(p1.size());
        //draw the points
        for(Point p:p1){
            Core.circle(outImg, p, 1, new Scalar(255,0,255),2);
        }
        for(Point p:p2){
            Core.circle(outImg, p, 1, new Scalar(0,255,0),2);
        }

        //find bounding boxes on points and draw them
        MatOfPoint2f mp1 = new MatOfPoint2f(); mp1.fromList(p1);
        MatOfPoint2f mp2 = new MatOfPoint2f(); mp2.fromList(p2);
        RotatedRect r1 = Imgproc.minAreaRect(mp1);
        RotatedRect r2 = Imgproc.minAreaRect(mp2);
        Point[] v1 = new Point[4]; r1.points(v1);
        Point[] v2 = new Point[4]; r2.points(v2);

        Core.line(outImg, v1[0], v1[1], new Scalar(0, 255, 0),1);
        Core.line(outImg, v1[1], v1[2], new Scalar(0, 255, 0),1);
        Core.line(outImg, v1[2], v1[3], new Scalar(0, 255, 0),1);
        Core.line(outImg, v1[3], v1[0], new Scalar(0, 255, 0),1);

        Core.line(outImg, v2[0], v2[1], new Scalar(255, 255, 0),1);
        Core.line(outImg, v2[1], v2[2], new Scalar(255, 255, 0),1);
        Core.line(outImg, v2[2], v2[3], new Scalar(255, 255, 0),1);
        Core.line(outImg, v2[3], v2[0], new Scalar(255, 255, 0),1);

        //show the corners
        for(int i=0;i<4;i++){
            Core.circle(outImg, v1[i], 3, new Scalar(200,250,50),2);
            Core.circle(outImg, v2[i], 3, new Scalar(0,238,250),2);
        }

        MatOfPoint2f p1Corners = new MatOfPoint2f(); p1Corners.fromArray(v1);
        MatOfPoint2f p2Corners = new MatOfPoint2f(); p2Corners.fromArray(v2);
        //find transform as H
        //Mat H = Calib3d.findHomography(p1Corners, p2Corners,Calib3d.RANSAC, 5);
        Mat H = Calib3d.findHomography(mp1, mp2,0, 5);
        //H = findTransform(mp1,mp2);

        //find the transform of H from 1'(original)s corners
        Mat orig_corners = new Mat(4,1,CvType.CV_32FC2);
        Mat transformed_corners = new Mat(4,1,CvType.CV_32FC2);
        orig_corners.put(0, 0, new double[] {v1[0].x,v1[0].y});
        orig_corners.put(1, 0, new double[] {v1[1].x,v1[1].y});
        orig_corners.put(2, 0, new double[] {v1[2].x,v1[2].y});
        orig_corners.put(3, 0, new double[] {v1[3].x,v1[3].y});

        Core.perspectiveTransform(orig_corners,transformed_corners,H);

        Core.line(outImg, new Point(transformed_corners.get(0,0)), new Point(transformed_corners.get(1,0)), new Scalar(0, 255, 255),4);
        Core.line(outImg, new Point(transformed_corners.get(1,0)), new Point(transformed_corners.get(2,0)), new Scalar(0, 255, 255),4);
        Core.line(outImg, new Point(transformed_corners.get(2,0)), new Point(transformed_corners.get(3,0)), new Scalar(0, 255, 255),4);
        Core.line(outImg, new Point(transformed_corners.get(3,0)), new Point(transformed_corners.get(0,0)), new Scalar(0, 255, 255),4);

    }

}

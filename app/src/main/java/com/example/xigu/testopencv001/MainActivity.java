package com.example.xigu.testopencv001;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    static {
        System.loadLibrary("opencv_java");
        System.loadLibrary("nonfree");
    }
    private ImageView imageView;
    private Bitmap inputImage; // make bitmap from image resource
    private FeatureDetector detector = FeatureDetector.create(FeatureDetector.SIFT);
    TextView mTimeTv;
    ImageView imageView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputImage = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) this.findViewById(R.id.imageView);
        mTimeTv = (TextView) findViewById(R.id.m_time_tv);
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        Bitmap bitmap1 = inputImage.copy(Bitmap.Config.ARGB_4444,true);
        imageView.setImageBitmap(bitmap1);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void sift() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                final long startTime = SystemClock.currentThreadTimeMillis();
                Mat rgba = new Mat();
                Utils.bitmapToMat(inputImage, rgba);
                MatOfKeyPoint keyPoints = new MatOfKeyPoint();
                Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_RGBA2GRAY);
                detector.detect(rgba, keyPoints);
                Features2d.drawKeypoints(rgba, keyPoints, rgba);
                Utils.matToBitmap(rgba, inputImage);
                final long endTime = SystemClock.currentThreadTimeMillis();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTimeTv.setText(endTime-startTime+"");
                        imageView1.setImageBitmap(inputImage);
                    }
                });
            }
        }.start();
    }

    public void start(View view) {
        sift();
//
    }
    public  void main() {
        try{
            //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

            Mat src=new Mat();
            Utils.bitmapToMat(inputImage, src);
            //读取图像到矩阵中
            if(src.empty()){
                throw new Exception("no file");
            }

            int xMargin,yMargin;
            int x0=src.cols()/4;
            int x1=(src.cols()/4)*3;
            int y0=src.cols()/4;
            int y1=(src.cols()/4)*3;
            Mat dst=new Mat();

            List<Point> listSrcs=java.util.Arrays.asList(new Point(x0,y0),new Point(x0,y1),new Point(x1,y1),new Point(x1,y0));
            Mat srcPoints= Converters.vector_Point_to_Mat(listSrcs, CvType.CV_32F);

            xMargin=src.cols()/10;
            yMargin=src.rows()/10;
            List<Point> listDsts=java.util.Arrays.asList(new Point(x0+xMargin,y0+yMargin),listSrcs.get(1),listSrcs.get(2),new Point(x1-xMargin,y0+yMargin));
            Mat dstPoints=Converters.vector_Point_to_Mat(listDsts,CvType.CV_32F);

            Mat perspectiveMmat=Imgproc.getPerspectiveTransform(srcPoints, dstPoints);
            Imgproc.warpPerspective(src, dst, perspectiveMmat, src.size(),Imgproc.INTER_LINEAR);
            final Bitmap bitmap =Bitmap.createBitmap(inputImage);
            Utils.matToBitmap(dst,bitmap);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageView1.setImageBitmap(bitmap);
                }
            });

//            xMargin=src.cols()/8;
//            yMargin=src.cols()/8;
//            listDsts.set(0, listSrcs.get(0));
//            listDsts.set(1, listSrcs.get(1));
//            listDsts.set(2, new Point(x1-xMargin,y1-yMargin));
//            listDsts.set(3, new Point(x1-xMargin,y0-yMargin));
//            dstPoints=Converters.vector_Point_to_Mat(listDsts,CvType.CV_32F);
//
//            perspectiveMmat=Imgproc.getPerspectiveTransform(srcPoints, dstPoints);
//            Imgproc.warpPerspective(src, dst, perspectiveMmat, src.size(),Imgproc.INTER_LINEAR);
//            Imgcodecs.imwrite("./images/dst1.jpg", dst);
//
//            xMargin=src.cols()/6;
//            yMargin=src.cols()/6;
//            listDsts.set(0, new Point(x0+xMargin,y0+yMargin));
//            listDsts.set(1, listSrcs.get(1));
//            listDsts.set(2, new Point(x1-xMargin,y1-yMargin));
//            listDsts.set(3, listSrcs.get(3));
//            dstPoints=Converters.vector_Point_to_Mat(listDsts,CvType.CV_32F);
//
//            perspectiveMmat=Imgproc.getPerspectiveTransform(srcPoints, dstPoints);
//            Imgproc.warpPerspective(src, dst, perspectiveMmat, src.size(),Imgproc.INTER_LINEAR);
//
//            Imgcodecs.imwrite("./images/dst2.jpg", dst);
        }catch(Exception e){
            System.out.println("例外：" + e);
        }

    }

    public void start1(View view) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                main();
            }
        }.start();
    }


    static Mat drawMatches(Mat img1, MatOfKeyPoint key1, Mat img2, MatOfKeyPoint key2, MatOfDMatch matches, boolean imageOnly) {
        Mat out = new Mat();
        Mat im1 = new Mat();
        Mat im2 = new Mat();
        Imgproc.cvtColor(img1, im1, Imgproc.COLOR_BGR2RGB);
        Imgproc.cvtColor(img2, im2, Imgproc.COLOR_BGR2RGB);
        if (imageOnly) {
            MatOfDMatch emptyMatch = new MatOfDMatch();
            MatOfKeyPoint emptyKey1 = new MatOfKeyPoint();
            MatOfKeyPoint emptyKey2 = new MatOfKeyPoint();
            Features2d.drawMatches(im1, emptyKey1, im2, emptyKey2, emptyMatch, out);
        } else {
            Features2d.drawMatches(im1, key1, im2, key2, matches, out);
        }
        Bitmap bmp = Bitmap.createBitmap(out.cols(), out.rows(), Bitmap.Config.ARGB_8888);
        Imgproc.cvtColor(out, out, Imgproc.COLOR_BGR2RGB);
        Core.putText(out, "FRAME", new org.opencv.core.Point(im1.width()/2,30),Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255, 255), 3);
        Core.putText(out, "MATCHED", new org.opencv.core.Point(im1.width()+im2.width()/2,30),Core.FONT_HERSHEY_PLAIN, 2, new Scalar(255, 0, 0), 3);
        return out;
    }


    public void tiaozhuan(View view) {
        Intent intent = new Intent(this,Main2Activity.class);
        startActivity(intent);
    }
}

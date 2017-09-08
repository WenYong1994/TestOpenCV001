package com.example.xigu.testopencv001;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.List;

public class Main2Activity extends AppCompatActivity {


    private ImageView imageView;
    private Bitmap inputImage1;
    private Bitmap inputImage2;
    private Bitmap bitmap;
    private FeatureDetector detector = FeatureDetector.create(FeatureDetector.SIFT);
    ImageView imageView2;
    ImageView imageView1;
    //特征匹配算法
    DescriptorExtractor descriptorExtractor= DescriptorExtractor.create(DescriptorExtractor.SIFT);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        imageView = (ImageView) findViewById(R.id.m_image_view);
        imageView1 = (ImageView) findViewById(R.id.m_image_view1);
        imageView2 = (ImageView) findViewById(R.id.m_image_view2);
        inputImage1 = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        inputImage2 = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        //bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        imageView1.setImageBitmap(inputImage1);
        bianHuanImage();
    }

    private void bianHuanImage() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try{
                    //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

                    Mat src=new Mat();
                    Utils.bitmapToMat(inputImage2, src);
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

                    Mat perspectiveMmat= Imgproc.getPerspectiveTransform(srcPoints, dstPoints);
                    Imgproc.warpPerspective(src, dst, perspectiveMmat, src.size(),Imgproc.INTER_LINEAR);
                    Utils.matToBitmap(dst,inputImage2);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView2.setImageBitmap(inputImage2);
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
        }.start();

    }


    static Mat drawMatches(Mat img1, MatOfKeyPoint key1, Mat img2, MatOfKeyPoint key2, MatOfDMatch matches, boolean imageOnly) {
        Mat out = new Mat();
        Mat im1 = new Mat();
        Mat im2 = new Mat();
        Imgproc.cvtColor(img1, im1, Imgproc.COLOR_GRAY2BGR);
        Imgproc.cvtColor(img2, im2, Imgproc.COLOR_GRAY2BGR);
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


    public void start(View view) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                //先提取特征点
                Mat mat1 = new Mat();
                Utils.bitmapToMat(inputImage1, mat1);
                MatOfKeyPoint keyPoints1 = new MatOfKeyPoint();
                Imgproc.cvtColor(mat1, mat1, Imgproc.COLOR_RGBA2GRAY);

                Mat mat2 = new Mat();
                Utils.bitmapToMat(inputImage2, mat2);
                MatOfKeyPoint keyPoints2 = new MatOfKeyPoint();
                Imgproc.cvtColor(mat2, mat2, Imgproc.COLOR_RGBA2GRAY);
                //提取关键点
                detector.detect(mat2, keyPoints2);
                detector.detect(mat1, keyPoints1);

                Mat descriptors1 = new Mat(), descriptors2 = new Mat();
                DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_SL2);//暴力匹配器

                //计算描述子
                descriptorExtractor.compute(mat1, keyPoints1, descriptors1);
                descriptorExtractor.compute(mat2, keyPoints2, descriptors2);
                MatOfDMatch matches = new MatOfDMatch();
                //进行特征点匹配
                descriptorMatcher.match(descriptors1, descriptors2, matches);
                mat1.channels();
                mat2.channels();
                try {
                    Mat mat = drawMatches(mat1,keyPoints1,mat2,keyPoints2,matches,false);
                    int width = mat.width();
                    int height = mat.height();
                    bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(mat,bitmap);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
}

package com.example.xigu.testopencv001;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.xigu.testopencv001.utils.OpenCVUtils;

import org.opencv.android.Utils;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.Arrays;
import java.util.Comparator;
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
    DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
    ProgressDialog mPb;
    private final int TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        imageView = (ImageView) findViewById(R.id.m_image_view);
        imageView1 = (ImageView) findViewById(R.id.m_image_view1);
        imageView2 = (ImageView) findViewById(R.id.m_image_view2);
        inputImage1 = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        inputImage2 = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        mPb =new ProgressDialog(this);
        mPb.setMessage("识别中。。");
        mPb.setCancelable(false);
        //bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        imageView1.setImageBitmap(inputImage1);
        //bianHuanImage();
    }

    private void bianHuanImage() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

                    Mat src = new Mat();
                    Utils.bitmapToMat(inputImage2, src);
                    //读取图像到矩阵中
                    if (src.empty()) {
                        throw new Exception("no file");
                    }

                    int xMargin, yMargin;
                    int x0 = src.cols() / 4;
                    int x1 = (src.cols() / 4) * 3;
                    int y0 = src.cols() / 4;
                    int y1 = (src.cols() / 4) * 3;
                    Mat dst = new Mat();

                    List<Point> listSrcs = java.util.Arrays.asList(new Point(x0, y0), new Point(x0, y1), new Point(x1, y1), new Point(x1, y0));
                    Mat srcPoints = Converters.vector_Point_to_Mat(listSrcs, CvType.CV_32F);

                    xMargin = src.cols() / 10;
                    yMargin = src.rows() / 10;
                    List<Point> listDsts = java.util.Arrays.asList(new Point(x0 + xMargin, y0 + yMargin), listSrcs.get(1), listSrcs.get(2), new Point(x1 - xMargin, y0 + yMargin));
                    Mat dstPoints = Converters.vector_Point_to_Mat(listDsts, CvType.CV_32F);

                    Mat perspectiveMmat = Imgproc.getPerspectiveTransform(srcPoints, dstPoints);
                    Imgproc.warpPerspective(src, dst, perspectiveMmat, src.size(), Imgproc.INTER_LINEAR);
                    Utils.matToBitmap(dst, inputImage2);
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
                } catch (Exception e) {
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
        Core.putText(out, "FRAME", new org.opencv.core.Point(im1.width() / 2, 30), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255, 255), 3);
        Core.putText(out, "MATCHED", new org.opencv.core.Point(im1.width() + im2.width() / 2, 30), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(255, 0, 0), 3);
        return out;
    }


    public void start(View view) {
        //这里进行拍照
//        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//        startActivityForResult(intent,TAKE_PHOTO);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test3);

        try {
            startSift(bitmap,view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                Bitmap bitmap = data.getParcelableExtra("data");
                //startSift(bitmap);
                break;
        }
    }

    private void startSift(final Bitmap bitmap2, final View view) {
        view.setEnabled(false);
        mPb.show();
        new Thread() {
            @Override
            public void run() {
                super.run();
                Mat frame = new Mat();
                Utils.bitmapToMat(bitmap2, frame);

                //先提取特征点
                final long startTime = SystemClock.currentThreadTimeMillis();
                Mat mat1 = new Mat();
                Utils.bitmapToMat(inputImage1, mat1);
                MatOfKeyPoint keyPoints1 = new MatOfKeyPoint();
                Imgproc.cvtColor(mat1, mat1, Imgproc.COLOR_RGBA2GRAY);

                Mat mat2 = new Mat();
                Utils.bitmapToMat(bitmap2, mat2);
                MatOfKeyPoint keyPoints2 = new MatOfKeyPoint();
                Imgproc.cvtColor(mat2, mat2, Imgproc.COLOR_RGBA2GRAY);
                //提取关键点
                detector.detect(mat2, keyPoints2);
                detector.detect(mat1, keyPoints1);


                Mat descriptors1 = new Mat(), descriptors2 = new Mat();

                DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);//暴力匹配器


                //计算描述子
                descriptorExtractor.compute(mat1, keyPoints1, descriptors1);
                descriptorExtractor.compute(mat2, keyPoints2, descriptors2);
                MatOfDMatch matches = new MatOfDMatch();
                //进行特征点匹配
                descriptorMatcher.match(descriptors1, descriptors2, matches);
                Size size = matches.size();
                double max_dist = 100;
                Log.e("openCV", "matchSize:" + size.area());
                Log.e("openCV", "keyPoints1:" + keyPoints1.size().area());
                Log.e("openCV", "keyPoints2:" + keyPoints2.size().area());
                DMatch[] dMatches = matches.toArray();
                Arrays.sort(dMatches, new Comparator<DMatch>() {
                    @Override
                    public int compare(DMatch o1, DMatch o2) {
                        if (o1.distance <= o2.distance) {
                            return -1;
                        }
                        return 1;
                    }
                });
                //找到最匹配的30个点
                DMatch[] arrDmatchs = new DMatch[30];
                KeyPoint[] keyPoints1Arr = keyPoints1.toArray();
                KeyPoint[] keyPoints2Arr = keyPoints2.toArray();
                Point[] objArr = new Point[30];
                Point[] sceneArr = new Point[30];
                MatOfPoint2f obj = new MatOfPoint2f();
                MatOfPoint2f scene = new MatOfPoint2f();
                for (int i = 0; i < 30; i++) {
                    arrDmatchs[i] = dMatches[i];
                    objArr[i] = keyPoints1Arr[dMatches[i].queryIdx].pt;
                    sceneArr[i] = keyPoints2Arr[dMatches[i].trainIdx].pt;
                }
                obj.fromArray(objArr);
                scene.fromArray(sceneArr);
                //这里去除错误配的的点
                Mat H = Calib3d.findHomography(obj, scene,Calib3d.RANSAC, 5);


                Mat obj_corners = new Mat(4, 1, CvType.CV_32FC2);
                Mat scene_corners = new Mat(4, 1, CvType.CV_32FC2);
                Rect r0 = null;
                r0 = new Rect(0, 0, mat1.cols(), mat1.rows());
//                obj_corners[0] = cvPoint(r0.x, r0.y);
//                obj_corners[1] = cvPoint(r0.x + r0.width, r0.y);
//                obj_corners[2] = cvPoint(r0.x + r0.width, r0.y + r0.height);
//                obj_corners[3] = cvPoint(r0.x, r0.y + r0.height);
                obj_corners.put(0, 0, new double[]{r0.x, r0.y});
                obj_corners.put(1, 0, new double[]{r0.x + r0.width, r0.y});
                obj_corners.put(2, 0, new double[]{r0.x + r0.width, r0.y + r0.height});
                obj_corners.put(3, 0, new double[]{r0.x, r0.y + r0.height});
                Core.perspectiveTransform(obj_corners, scene_corners, H);
                if (scene_corners.size().area() != 4) {
                    return;
                }
                Mat eValuesMat = new Mat();
                Mat eVectorsMat = new Mat();
                Core.eigen(H, false, eValuesMat, eVectorsMat);
                double[] doubles = eValuesMat.get(1, 0);
                Log.e("openCV", eValuesMat.toString());
                Log.e("openCV", eVectorsMat.toString());
                Log.e("openCV", H.toString());
                Log.e("openCV", doubles[0] + "");
                Log.e("openCV","("+H.get(0,0)[0]+","+H.get(0,1)[0]+","+H.get(0,2)[0]+")\n"
                              +"("+H.get(1,0)[0]+","+H.get(1,1)[0]+","+H.get(1,2)[0]+")\n"
                              +"("+H.get(2,0)[0]+","+H.get(2,1)[0]+","+H.get(2,2)[0]+")\n");

                if (Math.abs(Math.abs(eValuesMat.get(1, 0)[0]) - 1) < 0.9 &&
                        (((scene_corners.get(0, 0)[0] > 0) && (scene_corners.get(0, 0)[0] < frame.cols()))) &&
                        ((scene_corners.get(0, 0)[1] > 0) && (scene_corners.get(0, 0)[1] < frame.rows())) &&
                        (((scene_corners.get(1, 0)[0] > 0) && (scene_corners.get(1, 0)[0] < frame.cols()))) &&
                        ((scene_corners.get(1, 0)[1] > 0) && (scene_corners.get(1, 0)[1] < frame.rows())) &&
                        (((scene_corners.get(2, 0)[0] > 0) && (scene_corners.get(2, 0)[0] < frame.cols())) &&
                        ((scene_corners.get(2, 0)[1] > 0) && (scene_corners.get(2, 0)[1] < frame.rows())) &&
                        (((scene_corners.get(3, 0)[0] > 0) && (scene_corners.get(3, 0)[0] < frame.cols())) &&
                        ((scene_corners.get(3, 0)[1] > 0) && (scene_corners.get(3, 0)[1] < frame.rows()))))) {
                    Core.line(frame, new Point(scene_corners.get(0, 0)), new Point(scene_corners.get(1, 0)), new Scalar(0, 225, 255), 4);
                    Core.line(frame, new Point(scene_corners.get(1, 0)), new Point(scene_corners.get(2, 0)), new Scalar(0, 225, 255), 4);
                    Core.line(frame, new Point(scene_corners.get(2, 0)), new Point(scene_corners.get(3, 0)), new Scalar(0, 225, 255), 4);
                    Core.line(frame, new Point(scene_corners.get(3, 0)), new Point(scene_corners.get(0, 0)), new Scalar(0, 225, 255), 4);
//
//                    Core.circle(frame, new Point(scene_corners.get(0, 0)), 3, new Scalar(0, 225, 255));
//                    Core.circle(frame, new Point(scene_corners.get(1, 0)), 3, new Scalar(0, 225, 255));
//                    Core.circle(frame, new Point(scene_corners.get(2, 0)), 3, new Scalar(0, 225, 255));
//                    Core.circle(frame, new Point(scene_corners.get(4, 0)), 3, new Scalar(0, 225, 255));
                    final Bitmap bitmap4 = Bitmap.createBitmap(frame.width(), frame.height(), Bitmap.Config.RGB_565);

                    //寻找外接四变形
                    double minX=0,minY=0,maxX=0,maxY=0;
//                    for(Point dMatch:sceneArr){
//                        Log.e("openCV","("+dMatch.x+","+dMatch.y+")");
//                        Core.circle(frame,dMatch,2,new Scalar(0,255,255));
//                        if(dMatch.x>maxX){
//                            maxX = dMatch.x;
//                        }
//                        if(dMatch.y>maxY){
//                            maxY = dMatch.y;
//                        }
//                        if(dMatch.x<minX){
//                            minX = dMatch.x;
//                        }
//                        if(dMatch.y<minY){
//                            minY = dMatch.y;
//                        }
//                    }
//                    Point point1 = new Point(minX,maxY);
//                    Point point2 = new Point(maxX,maxY);
//                    Point point3 = new Point(maxX,minY);
//                    Point point4 = new Point(minX,minY);
//                    Core.line(frame,point1,point2,new Scalar(150,150,150),6);
//                    Core.line(frame,point2,point3,new Scalar(150,150,150),6);
//                    Core.line(frame,point3,point4,new Scalar(150,150,150),6);
//                    Core.line(frame,point4,point1,new Scalar(150,150,150),6);

                    Utils.matToBitmap(frame, bitmap4);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            imageView.setImageBitmap(bitmap4);
//                        }
//                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap4);
                            view.setEnabled(true);
                            if(mPb!=null&&mPb.isShowing()){
                                mPb.dismiss();
                            }
                        }
                    });
                    if (OpenCVUtils.verifySizes(frame, frame.cols(), frame.rows())) {

                    }
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap2);
                            view.setEnabled(true);
                            if(mPb!=null&&mPb.isShowing()){
                                mPb.dismiss();
                            }
                        }
                    });
                }
//                if ((fabs(fabs(eValuesMat.at<double>(1, 0)) - 1) < 0.9)&&
//                (((scene_corners[0].x>0) && (scene_corners[0].x < frame.cols)) &&
//                        ((scene_corners[0].y>0) && (scene_corners[0].y < frame.rows))) &&
//                        (((scene_corners[1].x>0) && (scene_corners[1].x < frame.cols)) &&
//                                ((scene_corners[1].y>0) && (scene_corners[1].y < frame.rows))) &&

//                        (((scene_corners[2].x>0) && (scene_corners[2].x < frame.cols)) &&
//                                ((scene_corners[2].y>0) && (scene_corners[2].y < frame.rows))) &&
//                        (((scene_corners[3].x>0) && (scene_corners[3].x < frame.cols)) &&
//                                ((scene_corners[3].y>0) && (scene_corners[3].y < frame.rows))))
//                {
//
//                    cout << "41" << endl;
//                    line(frame, scene_corners[0], scene_corners[1], Scalar(0, 0, 255), 1);
//                    line(frame, scene_corners[1], scene_corners[2], Scalar(0, 0, 255), 1);
//                    line(frame, scene_corners[2], scene_corners[3], Scalar(0, 0, 255), 1);
//                    line(frame, scene_corners[3], scene_corners[0], Scalar(0, 0, 255), 1);
//
//                    cv::circle(frame, scene_corners[0], 3, CV_RGB(255, 0, 0), 2);
//                    cv::circle(frame, scene_corners[1], 3, CV_RGB(0, 255, 0), 2);
//                    cv::circle(frame, scene_corners[2], 3, CV_RGB(0, 255, 255), 2);
//                    cv::circle(frame, scene_corners[3], 3, CV_RGB(255, 255, 255), 2);
//
//                    // get the area croped by four points
//                    RotatedRect rRect = minAreaRect(scene_corners);
//                    Rect brect = rRect.boundingRect();
//                    imshow("frame(brect)", frame(brect));
//                    // waitKey();
//
//                    if (verifySizes(frame(brect), frame.cols, frame.rows))
//                        rectangle(frame, brect, Scalar(255, 0, 0));
//
//                    cout << "42" << endl;
//                }

//                mat1.channels();
//                mat2.channels();
//                try {
//                    Mat mat = drawMatches(mat1,keyPoints1,mat2,keyPoints2,matches,false);
//                    int width = mat.width();
//                    int height = mat.height();
//                    bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
//                    final long endTime = SystemClock.currentThreadTimeMillis();
//                    Utils.matToBitmap(mat,bitmap);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            imageView.setImageBitmap(bitmap);
//                            Toast.makeText(Main2Activity.this, endTime-startTime+"", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
            }
        }.start();
    }

    public void test4(View view) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test4);

        try {
            startSift(bitmap,view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test2(View view){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test2);
        try {
            startSift(bitmap,view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
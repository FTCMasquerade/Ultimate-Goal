package Library4997.MasqVision;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.*;

import Library4997.MasqVision.filters.MasqCVColorFilter;

/**
 * Created by Keval Kataria on 6/1/2020.
 */

public abstract class MasqCVDetector extends OpenCvPipeline {
    protected int minimumArea = 1;
    protected int imageWidth = 1280;
    protected int imageHeight = 960;

    protected Rect foundRect = new Rect();
    protected Rect secondRect = new Rect();
    protected Mat workingMat;
    protected Mat displayMat;

    protected boolean found = false;
    protected boolean found2 = false;

    protected Point tl, br;
    public int offset = 0;

    protected void cropMat(Mat input, Point tl, Point br)  {
        this.tl = tl;
        this.br = br;
        input.submat(new Rect(tl,br));
    }
    protected List<MatOfPoint> findContours(MasqCVColorFilter filter, Mat mask) {
        filter.process(workingMat,mask);
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(mask, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        return contours;
    }
    protected List<Rect> contoursToRects(List<MatOfPoint> contours) {
        List<Rect> rects = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            rects.add(Imgproc.boundingRect(contour));
        }
        return rects;
    }
    protected List<List<Rect>> groupIntoBlobs(List<Rect> rects, int blobDistanceThreshold) {
        List<List<Rect>> listOfBlobs = new ArrayList<>();
        List<Rect> unusedRects = new ArrayList<>(rects);

        while (!unusedRects.isEmpty()) {
            LinkedList<Rect> toProcess = new LinkedList<>();
            toProcess.add(unusedRects.remove(0));
            List<Rect> currentBlob = new ArrayList<>();
            while (!toProcess.isEmpty()) {
                Rect currentRect = toProcess.poll();
                currentBlob.add(currentRect);

                for (int i = 0; i < unusedRects.size(); i++) {
                    if (distance(getCenterPoint(currentRect), getCenterPoint(unusedRects.get(i))) < blobDistanceThreshold) {
                        toProcess.add(unusedRects.remove(i));
                        i--;
                    }
                }
            }
            listOfBlobs.add(currentBlob);
        }

        return listOfBlobs;
    }
    protected Rect chooseBestRect(List<List<Rect>> listOfBlobs) {
        Rect bestRect = new Rect();
        try {
            bestRect = boundingRect(listOfBlobs.get(0));
        }catch(Exception e) {
            e.printStackTrace();
        }
        for (List<Rect> blob : listOfBlobs) {
            Rect blobBound = boundingRect(blob);
            drawRect(blobBound, new Scalar(0, 150, 0), false);

            if (blobBound.area() > bestRect.area()) {
                bestRect = blobBound;
            }
        }
        return bestRect;
    }
    protected Rect[] chooseTwoRects(List<List<Rect>> listOfBlobs) {
        Rect bestRect = new Rect();
        Rect secondRect = new Rect();
        try {
            bestRect = boundingRect(listOfBlobs.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (List<Rect> blob : listOfBlobs) {
            Rect blobBound = boundingRect(blob);
            drawRect(blobBound, new Scalar(0, 150, 0), false);

            if (blobBound.area() > bestRect.area()) {
                secondRect = bestRect;
                bestRect = blobBound;
            }
        }
        return new Rect[] {bestRect, secondRect};
    }
    protected void drawContours(List<MatOfPoint> contours, Scalar color) {
        Imgproc.drawContours(displayMat, contours, -1, color, 1);
    }
    protected void drawRect(Rect rect, Scalar color, boolean fill) {
        if(fill) Imgproc.rectangle(displayMat, rect.tl(), rect.br(), color, -1);
        else Imgproc.rectangle(displayMat, rect.tl(), rect.br(), color, 2);
        }
    protected void drawCenterPoint(Point point, Scalar color) {
            Imgproc.circle(displayMat, point, 2, color);
        }

    protected List<Rect> filterByBound(List<Rect> rects, Rect boundingRect) {
        List<Rect> rectsInsideBound = new ArrayList<>();
        for (Rect rect : rects) {
            if (boundingRect.contains(getCenterPoint(rect))) {
                rectsInsideBound.add(rect);
            }
        }
        return rectsInsideBound;
    }
    public Point getCenterPoint(Rect rect) {
        return new Point(rect.x + rect.width / 2.0, rect.y + rect.height / 2.0);
    }
    public Rect getFoundRect() {return foundRect;}
    public Rect getSecondRect() {return secondRect;}

    public void setClippingMargins(Point tl, Point br) {
        this.tl = tl;
        this.br = br;
        imageWidth = (int) (br.x - tl.x);
        imageHeight = (int) (tl.y - br.y);
        offset = (int) tl.x;
    }
    public void setClippingMargins(int top, int left, int bottom, int right) {
        tl = new Point(left, top);
        br = new Point(1280 - right,960 - bottom);
        imageWidth = 1280 - right - left;
        imageHeight = 960 - top - bottom;
        offset = left;
    }

    public int getImageWidth() {
        return imageWidth;
    }
    public int getImageHeight() {
        return imageHeight;
    }
    public boolean isFound() {return found;}

    public boolean isFound2() {return found2;}


    private Rect boundingRect(List<Rect> rects) {
        int minX = 999;
        int minY = 999;
        int maxX = 0;
        int maxY = 0;

        for (Rect rect : rects) {
            minX = Math.min(rect.x, minX);
            minY = Math.min(rect.y, minY);
            maxX = Math.max(rect.x + rect.width, maxX);
            maxY = Math.max(rect.y + rect.height, maxY);
        }

        return new Rect(minX, minY, maxX - minX, maxY - minY);
    }
    private double distance(Point a, Point b) {
        return Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
    }
    @Override
    public Mat processFrame(Mat input) {return input;}
    @Override
    public void onViewportTapped() {}
}

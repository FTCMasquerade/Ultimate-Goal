package Library4997.MasqVision.filters;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class GrayscaleFilter extends MasqCVColorFilter {
    private Scalar lower;
    private Scalar upper;

    public GrayscaleFilter(int lower, int upper) {
        this.lower = new Scalar(lower);
        this.upper = new Scalar(upper);
    }

    @Override
    public void process(Mat input, Mat mask) {
        // Convert the input to grayscale
        Imgproc.cvtColor(input,input,Imgproc.COLOR_RGB2GRAY);

        // Blur it
        Imgproc.GaussianBlur(input,input,new Size(5,5),0);
        // Run in range check
        Core.inRange(input, lower, upper, mask);
        input.release();
    }

    public void setValues(int lower, int upper) {
        this.lower = new Scalar(lower);
        this.upper = new Scalar(upper);
    }
}

import processing.core.PApplet;
import processing.core.PImage;
import processing.video.Capture;

import java.util.HashMap;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

//https://docs.opencv.org/2.4/doc/tutorials/imgproc/imgtrans/canny_detector/canny_detector.html
public class Main extends PApplet {

    PImage img;  // Stores our current frame, or static image
    boolean usingWebcam = true;  // If we should use webcam (otherwise use a static image)
    String imageFilePath = "baby.jpg";  // The file path of static image (leave blank if using webcam)
    PImage edgeImg;

    Capture cap = new Capture(this);

    int windowWidth = 1280;
    int windowHeight = 800;

    float lowPassThreasshold = 20;
    float boostThreashold = 50;

    public void settings(){ size(windowWidth, windowHeight);}

    public void setup(){

        img = loadImage("baby.jpg");
        img.resize(windowWidth, windowHeight);
        img.filter(BLUR,1);

        edgeImg = img.copy();
        //SortedSet<Double> angles = new TreeSet<Double>();
        for (int x = 0; x < edgeImg.width; x++){
            for (int y = 0; y < edgeImg.height; y++){
                float gx = gx(img, x, y);
                float gy = gy(img, x, y);
                double magnitude = Math.sqrt(gx*gx + gy*gy);
                magnitude = magnitude*255/4;
                if (magnitude < lowPassThreasshold) { magnitude = 0; }
                if (magnitude != 0) { magnitude += boostThreashold; }
                double angle = getAngle(gy, gx);
                //angles.add(angle);
                edgeImg.set(x,y,getRGBFromAngle((float)angle, (float)magnitude));
            }
        }
        //System.out.println(angles.toString());
        System.out.println("Done");
        surface.setResizable(true);
    }

    public void draw(){
        if (usingWebcam){
            if (cap.isModified()) {
                img = cap.get();
            }
        }


        background(0,0,0);
        image(edgeImg, 0, 0);

    }

    public void mousePressed(){ // Gives debug information at the mouse position
        float gx = gx(img, mouseX, mouseY);
        float gy = gy(img, mouseX, mouseY);
        double magnitude = Math.sqrt(gx*gx + gy*gy);
        magnitude = magnitude*255/4;
        if (magnitude < lowPassThreasshold) { magnitude = 0; }
        if (magnitude != 0) { magnitude += boostThreashold; }
        double angle = getAngle(gy, gx);
        int color = getRGBFromAngle((float)angle, (float)magnitude);
        System.out.println("###############\ngx: " + gx);
        System.out.println("gy: " + gy);
        System.out.println("magnitude: " + magnitude);
        System.out.println("angle: " + (angle));
        System.out.println("Red: " + red(color) + "    Green: " + green(color) + "    Blue: " + blue(color));
    }
    private float gx(PImage image, int x, int y){
        float totalBrightness = -brightness(image.get(x-1,y-1)) -2*brightness(image.get(x-1,y+0)) -brightness(image.get(x-1,y+1));
              totalBrightness += brightness(image.get(x+1,y-1)) +2*brightness(image.get(x+1,y+0)) +brightness(image.get(x+1,y+1));
        return totalBrightness / 255; // Convert value to 0 to 1.0
    }

    private float gy(PImage image, int x, int y){
        float totalBrightness = -brightness(image.get(x-1,y-1)) -2*brightness(image.get(x+0,y-1)) -brightness(image.get(x+1,y-1));
              totalBrightness += brightness(image.get(x-1,y+1)) +2*brightness(image.get(x+0,y+1)) +brightness(image.get(x+1,y+1));
        return totalBrightness / 255; // Convert value to 0 to 1.0
    }

    private double getAngle(float gy, float gx){
        if (0.00001 > gx && gx > -0.00001){ // Because we divide by gx, if it is near 0, we can say gy/gx is infinity,
            return Math.PI / 2;             // so Arctan(infinity) = PI/2
        }
        double theta = Math.atan(gy/gx);
        theta = theta * 180 / Math.PI + 90;
        //if (theta < 0) { theta = 180 - theta; } //if (theta < 0) { theta += 360; }
        return theta;
    }

    private int getRGBFromAngle(float angle, float magnitude){
        float red = 0f;
        float green = 0f;
        float blue = 0f;
        if (angle < 60) {
            red = (60 - angle) / 60;
        }
        if (angle > 120){
            red = Math.abs(120 - angle) / 60;
        }
        if (0 < angle && angle <= 60){
            green = angle / 60;
        }
        if (60 < angle && angle < 120){
            green = Math.abs(angle - 120) / 60;
        }
        if (60 < angle && angle <= 120){
            blue = (angle - 60) / 60;
        }
        if (120 < angle && angle < 180){
            blue = Math.abs(180 - angle) / 60;
        }


        return color(red*255, green*255, blue*255, magnitude);
    }

    public static void main(String[] args){
        String[] processingArgs = {"MySketch"};
        Main mySketch = new Main();
        PApplet.runSketch(processingArgs, mySketch);
    }
}

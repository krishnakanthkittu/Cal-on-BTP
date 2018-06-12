package cal_on.usbterminal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Base64;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import cal_on.usbterminal.printer.ImageAlignment;
import cal_on.usbterminal.printer.ImageScale;
import cal_on.usbterminal.printer.PrtGraphics2T;

public class Bluetooth_Img_ThermalAPI {
    static double minThresh = 70.0;
    static double maxThresh = 255.0;
    static String Hexstr = "";
    static String morphImgSave = "";
    static String imagesDir =Environment.getExternalStorageDirectory().getPath();
    static int imgheight = 250;
    static int imgwidth = 384;
    static int splitStrLength = 24000;
    static String capturedImgSave = "";
    static boolean flag = false;
    public byte[] prepare2InchImageData(String address, String hexStr, int height) {
        byte[] barray = null;
        try {
            String b = Integer.toHexString(height);
            byte[] bytes = new byte[]{Byte.valueOf(String.valueOf(height)).byteValue()};
            // byte[] rf22 = new byte[]{27, 30, bytes[0], 43};
            byte[] rf22 = new byte[]{27, 30, bytes[0], 43};
            // String ss = "1B236430" + hexStr;
            String ss = "1A236430" + hexStr;
            int t = 0;
            int t1 = 0;
            barray = new byte[ss.length()];
            int count = 0;
            int a = 0;
            while (a < ss.length() - 1) {
                t = Integer.parseInt(ss.substring(a, a + 1), 16);
                t1 = Integer.parseInt(ss.substring(a + 1, a + 2), 16);
                barray[count] = (byte)(t * 16 + t1);
                ++count;
                a += 2;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        byte[] encodedBytes = Base64.encode((byte[])barray, (int)1);
        String as = new String(encodedBytes);
        return barray;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        String reSizeSave = null;
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = (float)newWidth / (float)width;
        float scaleHeight = (float)newHeight / (float)height;
        org.opencv.core.Size imgSize = new org.opencv.core.Size(newHeight, newWidth);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap((Bitmap)bm, (int)0, (int)0, (int)width, (int)height, (Matrix)matrix, (boolean)false);
        Mat resizeMat = new Mat();
        Utils.bitmapToMat((Bitmap)resizedBitmap, (Mat)resizeMat);
        File resizeFile = new File(imagesDir, "001.jpg");
        reSizeSave = resizeFile.getPath();
        Highgui.imwrite((String)reSizeSave, (Mat)resizeMat);
        int a=0;
        Mat grey = new Mat(imgSize,0);
        Mat morphologyImgMat = new Mat(imgSize,0);
        Bitmap greyBitmap = Bitmap.createBitmap((int)resizeMat.width(), (int)resizeMat.height(), (Bitmap.Config)Bitmap.Config.ARGB_8888);
        Imgproc.cvtColor((Mat)resizeMat, (Mat)grey, (int)7);
        Bitmap morpBitmap = Bitmap.createBitmap((int)resizeMat.width(), (int)resizeMat.height(), (Bitmap.Config)Bitmap.Config.ARGB_8888);
        Imgproc.equalizeHist((Mat)grey, (Mat)grey);
        Imgproc.threshold((Mat)grey, (Mat)morphologyImgMat, (double)minThresh, (double)maxThresh, (int)1);
        Utils.matToBitmap((Mat)morphologyImgMat, (Bitmap)morpBitmap);
        Highgui.imwrite((String)morphImgSave, (Mat)morphologyImgMat);
        StringBuffer sb = new StringBuffer();
        StringBuffer sbHex = new StringBuffer();
        int b = 0;
        int len = imgwidth / 8 * imgheight;
        byte[] bArry = new byte[len];
        int hexInt = 0;
        int x = 0;
        while (x < imgheight) {
            int y = 0;
            while (y < imgwidth) {
                int clr = morpBitmap.getPixel(y, x) & 255;
                if (clr == 0) {
                    sb.append("1");
                    b = (byte)(b << 1);
                } else if (clr == 255) {
                    sb.append("0");
                    b = (byte)((b << 1) + 1);
                } else {
                    sb.append("x");
                }
                if (y % 8 == 7) {
                    bArry[hexInt] =(byte)b;
                    ++hexInt;
                    b = 0;
                }
                ++y;
            }
            ++x;
        }
        ConvertBinary2Hexadecimal b2hex = new ConvertBinary2Hexadecimal();
        Hexstr = ConvertBinary2Hexadecimal.convertBinary2Hexadecimal(bArry);
        flag = true;
        return resizedBitmap;
    }

    public byte[] prepareImageToPrint(String imagepath) throws InterruptedException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Bitmap bmp = BitmapFactory.decodeFile((String)imagepath);
            Bitmap resizebmp = this.getResizedBitmapImage(bmp, 255, 384);
            File resizeFile = new File(imagesDir, "001.jpg");
            FileOutputStream fOut = new FileOutputStream(resizeFile);
            resizebmp.compress(Bitmap.CompressFormat.JPEG, 90, (OutputStream)fOut);
            fOut.flush();
            fOut.close();
            FileInputStream in = null;
            try {
                in = new FileInputStream(String.valueOf(imagesDir) + "/001" +
                        ".jpg");
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BufferedInputStream buf = new BufferedInputStream(in);
            byte[] bMapArray = null;
            try {
                bMapArray = new byte[buf.available()];
                buf.read((byte[])bMapArray);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap bMap = BitmapFactory.decodeByteArray(bMapArray, (int)0, bMapArray.length);
            PrtGraphics2T pg = new PrtGraphics2T();
            ImageScale ImageScaleret = ImageScale.valueOf("SCALE_ONE_TO_ONE");
            ImageAlignment ImageAlignmentret = ImageAlignment.valueOf("IMAGE_CENTER");
            byte[] data1 = PrtGraphics2T.printImage(bMap, ImageScaleret, ImageAlignmentret);
            byte[] cmddata = PrtGraphics2T.printImageCommand(bMap, ImageScaleret, ImageAlignmentret);
            outputStream.write(cmddata);
            outputStream.write(data1);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        byte[] imagedata = outputStream.toByteArray();
        return imagedata;
    }

    public Bitmap getResizedBitmapImage(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = (float)newWidth / (float)width;
        float scaleHeight = (float)newHeight / (float)height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap((Bitmap)bm, (int)0, (int)0, (int)width, (int)height, (Matrix)matrix, (boolean)false);
        return resizedBitmap;
    }
}

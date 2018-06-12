
package cal_on.usbterminal.printer;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class


PrtGraphics3T {
    private static final int MAX_WIDTH = 576;
    private static final int MAX_ROWS_PER_BLOCK = 255;
    private static final byte ESC = 27;
  
    public static byte[] printGraphic(int rows, int cols, byte[] data) {
        if (rows >= 1) {
            // empty if block
        }
        if (cols >= 1) {
            // empty if block
        }
        byte[] graphicCmd = new byte[]{27, 73, (byte)rows, (byte)(cols / 8)};
        return graphicCmd;
    }

    @SuppressLint(value={"NewApi"})
    public static byte[] printImage(Bitmap image, ImageScale scale, ImageAlignment align) {
        byte[] data = null;
        int orgWidth = image.getWidth();
        int orgHeight = image.getHeight();
        int scaleFactor = PrtGraphics3T.chooseScaleFactor(scale, orgWidth);
        int cols = orgWidth / scaleFactor;
        int rows = orgHeight / scaleFactor;
        cols = cols < 576 ? cols : 576;
        byte[][] grayImage = PrtGraphics3T.grayScale(image, scaleFactor, cols, rows);
        byte[][] ditheredImage = PrtGraphics3T.dither(grayImage);
        int imageOffset = PrtGraphics3T.computeAlignment(cols, align);
        byte[] graphic = PrtGraphics3T.alignAndPack(ditheredImage, imageOffset);
        int rIdx = 0;
        int dIdx = 0;
        int pixelsPerRow = cols + imageOffset;
        while (rIdx < rows) {
            int rowsLeft = rows - rIdx;
            int rowsSent = rowsLeft < 255 ? rowsLeft : 255;
            int dSize = rowsSent * pixelsPerRow / 8;
            //data = Arrays.copyOfRange(graphic, dIdx, dIdx + dSize);
            PrtGraphics3T.printGraphic(rowsSent, pixelsPerRow, (byte[])data);
            dIdx += dSize;
            rIdx += rowsSent;
        }
        return data;
    }

    @SuppressLint(value={"NewApi"})
    public static byte[] printImageCommand(Bitmap image, ImageScale scale, ImageAlignment align) {
        byte[] cmd = null;
        byte[] data = null;
        int orgWidth = image.getWidth();
        int orgHeight = image.getHeight();
        int scaleFactor = PrtGraphics3T.chooseScaleFactor(scale, orgWidth);
        int cols = orgWidth / scaleFactor;
        int rows = orgHeight / scaleFactor;
        cols = cols < 576 ? cols : 576;
        byte[][] grayImage = PrtGraphics3T.grayScale(image, scaleFactor, cols, rows);
        byte[][] ditheredImage = PrtGraphics3T.dither(grayImage);
        int imageOffset = PrtGraphics3T.computeAlignment(cols, align);
        byte[] graphic = PrtGraphics3T.alignAndPack(ditheredImage, imageOffset);
        int rIdx = 0;
        int dIdx = 0;
        int pixelsPerRow = cols + imageOffset;
        while (rIdx < rows) {
            int rowsLeft = rows - rIdx;
            int rowsSent = rowsLeft < 255 ? rowsLeft : 255;
            int dSize = rowsSent * pixelsPerRow / 8;
            //data = Arrays.copyOfRange(graphic, dIdx, dIdx + dSize);
            cmd = PrtGraphics3T.printGraphic(rowsSent, pixelsPerRow, (byte[])data);
            dIdx += dSize;
            rIdx += rowsSent;
        }
        return cmd;
    }

    private static byte[][] grayScale(Bitmap image, int scaleFactor, int cols, int rows) {
        int orgWidth = image.getWidth();
        byte[][] grayImage = new byte[rows][cols];
        int y = 0;
        while (y < rows) {
            byte[] imrow = grayImage[y];
            int[] pixelrow = new int[orgWidth];
            image.getPixels(pixelrow, 0, orgWidth, 0, y * scaleFactor, orgWidth, 1);
            int x = 0;
            while (x < cols) {
                int pixel = pixelrow[x * scaleFactor];
                int red = ((pixel & 16711680) >> 16) * 22;
                int green = ((pixel & 65280) >> 8) * 72;
                int blue = ((pixel & 255) >> 0) * 7;
                imrow[x] = (byte)((red + green + blue) / 100);
                ++x;
            }
            ++y;
        }
        return grayImage;
    }

    private static byte[][] dither(byte[][] image) {
        int rows = image.length;
        int cols = image[0].length;
        int y = 0;
        while (y < rows) {
            byte[] thisRow = image[y];
            byte[] nextRow = y < rows - 1 ? (byte[])image[y + 1] : null;
            int x = 0;
            while (x < cols) {
                int newpixel;
                int orgPixel = thisRow[x] & 255;
                int newPixel = orgPixel >= 128 ? 255 : 0;
                thisRow[x] = (byte)newPixel;
                int error = orgPixel - newPixel;
                int error_7_16 = error * 7 >> 4;
                int error_5_16 = error * 5 >> 4;
                int error_3_16 = error * 3 >> 4;
                int error_1_16 = error * 1 >> 4;
                if (x < cols - 1) {
                    newpixel = (thisRow[x + 1] & 255) + error_7_16;
                    newpixel = newpixel < 0 ? 0 : newpixel;
                    newpixel = newpixel > 255 ? 255 : newpixel;
                    thisRow[x + 1] = (byte)newpixel;
                }
                if (nextRow != null) {
                    if (x > 0) {
                        newpixel = (nextRow[x - 1] & 255) + error_3_16;
                        newpixel = newpixel < 0 ? 0 : newpixel;
                        newpixel = newpixel > 255 ? 255 : newpixel;
                        nextRow[x - 1] = (byte)newpixel;
                    }
                    newpixel = (newpixel = (nextRow[x + 0] & 255) + error_5_16) < 0 ? 0 : newpixel;
                    newpixel = newpixel > 255 ? 255 : newpixel;
                    nextRow[x + 0] = (byte)newpixel;
                    if (x < cols - 1) {
                        newpixel = (nextRow[x + 1] & 255) + error_1_16;
                        newpixel = newpixel < 0 ? 0 : newpixel;
                        newpixel = newpixel > 255 ? 255 : newpixel;
                        nextRow[x + 1] = (byte)newpixel;
                    }
                }
                ++x;
            }
            ++y;
        }
        return image;
    }

    private static int computeAlignment(int cols, ImageAlignment align) {
        if (cols >= 576) {
            return 0;
        }
        switch (PrtGraphics3T.ImageAlignment()[align.ordinal()]) {
            case 1: {
                return (576 - cols) / 2;
            }
            case 3: {
                return 576 - cols;
            }
        }
        return 0;
    }

    private static byte[] alignAndPack(byte[][] image, int offset) {
        int rows = image.length;
        int cols = image[0].length;
        byte[] data = new byte[rows * (cols + offset) / 8];
        int dIdx = 0;
        int y = 0;
        while (y < rows) {
            dIdx += offset / 8;
            int bit = offset % 8;
            int x = 0;
            while (x < cols) {
                byte[] arrby = data;
                int n = dIdx;
                arrby[n] = (byte)(arrby[n] << 1);
                int pixel = image[y][x] < 0 ? 0 : 1;
                byte[] arrby2 = data;
                int n2 = dIdx++;
                arrby2[n2] = (byte)(arrby2[n2] | pixel);
                if (++bit >= 8) {
                    bit = 0;
                }
                ++x;
            }
            ++y;
        }
        return data;
    }

    private static int chooseScaleFactor(ImageScale scaling, int width) {
        if (scaling == ImageScale.SCALE_FULL_IMAGE) {
            scaling = width < 576 ? ImageScale.SCALE_ONE_TO_ONE : (width / ImageScale.SCALE_TWO_TO_ONE.getValue() < 576 ? ImageScale.SCALE_TWO_TO_ONE : ImageScale.SCALE_FOUR_TO_ONE);
        }
        return scaling.getValue();
    }

    private static void dumpImageArray(byte[][] image, String filename) {
        File filepath = Environment.getExternalStoragePublicDirectory((String)Environment.DIRECTORY_PICTURES);
        File binFile = new File(filepath, filename);
        try {
            FileOutputStream outStream = new FileOutputStream(binFile);
            int idx = 0;
            while (idx < image.length) {
                outStream.write(image[idx]);
                ++idx;
            }
            outStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    static /* synthetic */ int[] ImageAlignment() {
    	 int[] arrn;
         int[] arrn2=ImageAlignment();
         if (arrn2 != null) {
             return arrn2;
         }
         arrn = new int[ImageAlignment.values().length];
         try {
             arrn[ImageAlignment.IMAGE_CENTER.ordinal()] = 1;
             arrn[ImageAlignment.IMAGE_LEFT.ordinal()] = 2;
             arrn[ImageAlignment.IMAGE_RIGHT.ordinal()] = 3;
         }
         catch (NoSuchFieldError v3) {
         	
         }
         
         return ImageAlignment();
    }
}



package cal_on.usbterminal.printer;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class PrtGraphics2T {

	private static final int MAX_WIDTH = 384;
	  private static final int MAX_ROWS_PER_BLOCK = 255;
	  private static final byte ESC = 27;

	  public static byte[] printGraphic(int rows, int cols, byte[] data)
	  {
	    if ((rows < 1) || (cols >= 1));
	   // data.length; 
	   // int a=(rows * cols / 8);

	    byte[] graphicCmd = { 27, 73, (byte)rows, (byte)(cols / 8) };

	    return graphicCmd;
	  }

	  @SuppressLint({"NewApi"})
	  public static byte[] printImage(Bitmap image, ImageScale scale, ImageAlignment align)
	  {
	    byte[] data = null;

	    int orgWidth = image.getWidth();
	    int orgHeight = image.getHeight();

	    int scaleFactor = chooseScaleFactor(scale, orgWidth);

	    int cols = orgWidth / scaleFactor;
	    int rows = orgHeight / scaleFactor;

	    cols = cols < 384 ? cols : 384;

	    byte[][] grayImage = grayScale(image, scaleFactor, cols, rows);

	    byte[][] ditheredImage = dither(grayImage);

	    int imageOffset = computeAlignment(cols, align);
	    byte[] graphic = alignAndPack(ditheredImage, imageOffset);

	    int rIdx = 0;
	    int dIdx = 0;
	    int pixelsPerRow = cols + imageOffset;
	    while (rIdx < rows)
	    {
	      int rowsLeft = rows - rIdx;

	      int rowsSent = 
	        rowsLeft < 255 ? 
	        rowsLeft : 
	        255;

	      int dSize = rowsSent * pixelsPerRow / 8;
	      data = Arrays.copyOfRange(graphic, dIdx, dIdx + dSize);

	      printGraphic(rowsSent, pixelsPerRow, data);

	      dIdx += dSize;

	      rIdx += rowsSent;
	    }

	    return data;
	  }

	  @SuppressLint({"NewApi"})
	  public static byte[] printImageCommand(Bitmap image, ImageScale scale, ImageAlignment align)
	  {
	    byte[] cmd = null;
	    byte[] data = null;

	    int orgWidth = image.getWidth();
	    int orgHeight = image.getHeight();

	    int scaleFactor = chooseScaleFactor(scale, orgWidth);

	    int cols = orgWidth / scaleFactor;
	    int rows = orgHeight / scaleFactor;

	    cols = cols < 384 ? cols : 384;

	    byte[][] grayImage = grayScale(image, scaleFactor, cols, rows);

	    byte[][] ditheredImage = dither(grayImage);

	    int imageOffset = computeAlignment(cols, align);
	    System.out.println("di  "+ditheredImage.length+"offset "+imageOffset);
	    byte[] graphic = alignAndPack(ditheredImage, imageOffset);

	    int rIdx = 0;
	    int dIdx = 0;
	    int pixelsPerRow = cols + imageOffset;
	    while (rIdx < rows)
	    {
	      int rowsLeft = rows - rIdx;

	      int rowsSent = 
	        rowsLeft < 255 ? 
	        rowsLeft : 
	        255;

	      int dSize = rowsSent * pixelsPerRow / 8;
	      data = Arrays.copyOfRange(graphic, dIdx, dIdx + dSize);

	      cmd = printGraphic(rowsSent, pixelsPerRow, data);

	      dIdx += dSize;

	      rIdx += rowsSent;
	    }

	    return cmd;
	  }

	  private static byte[][] grayScale(Bitmap image, int scaleFactor, int cols, int rows)
	  {
	    int orgWidth = image.getWidth();

	    byte[][] grayImage = new byte[rows][cols];

	    for (int y = 0; y < rows; y++)
	    {
	      byte[] imrow = grayImage[y];

	      int[] pixelrow = new int[orgWidth];
	      image.getPixels(pixelrow, 
	        0, 
	        orgWidth, 
	        0, 
	        y * scaleFactor, 
	        orgWidth, 
	        1);

	      for (int x = 0; x < cols; x++)
	      {
	        int pixel = pixelrow[(x * scaleFactor)];

	        int red = ((pixel & 0xFF0000) >> 16) * 22;
	        int green = ((pixel & 0xFF00) >> 8) * 72;
	        int blue = ((pixel & 0xFF) >> 0) * 7;

	        imrow[x] = (byte)((red + green + blue) / 100);
	      }
	    }

	    return grayImage;
	  }

	  private static byte[][] dither(byte[][] image)
	  {
	    int rows = image.length;
	    int cols = image[0].length;

	    for (int y = 0; y < rows; y++)
	    {
	      byte[] thisRow = image[y];
	      byte[] nextRow = y < rows - 1 ? image[(y + 1)] : null;

	      for (int x = 0; x < cols; x++)
	      {
	        int orgPixel = thisRow[x] & 0xFF;

	        int newPixel = orgPixel >= 128 ? 255 : 0;
	        thisRow[x] = (byte)newPixel;

	        int error = orgPixel - newPixel;

	        int error_7_16 = error * 7 >> 4;
	        int error_5_16 = error * 5 >> 4;
	        int error_3_16 = error * 3 >> 4;
	        int error_1_16 = error * 1 >> 4;

	        if (x < cols - 1)
	        {
	          int newpixel = (thisRow[(x + 1)] & 0xFF) + error_7_16;
	          newpixel = newpixel < 0 ? 0 : newpixel;
	          newpixel = newpixel > 255 ? 255 : newpixel;
	          thisRow[(x + 1)] = (byte)newpixel;
	        }

	        if (nextRow == null)
	          continue;
	        if (x > 0)
	        {
	          int newpixel = 
	            (nextRow[(x - 1)] & 0xFF) + error_3_16;
	          newpixel = newpixel < 0 ? 0 : newpixel;
	          newpixel = newpixel > 255 ? 255 : newpixel;
	          nextRow[(x - 1)] = (byte)newpixel;
	        }

	        int newpixel = 
	          (nextRow[(x + 0)] & 0xFF) + error_5_16;
	        newpixel = newpixel < 0 ? 0 : newpixel;
	        newpixel = newpixel > 255 ? 255 : newpixel;
	        nextRow[(x + 0)] = (byte)newpixel;

	        if (x >= cols - 1) {
	          continue;
	        }
	        int newpixel1 = 
	          (nextRow[(x + 1)] & 0xFF) + error_1_16;
	        newpixel1 = newpixel1 < 0 ? 0 : newpixel1;
	        newpixel1 = newpixel1 > 255 ? 255 : newpixel1;
	        nextRow[(x + 1)] = (byte)newpixel1;
	      }

	    }

	    return image;
	  }

	  private static int computeAlignment(int cols, ImageAlignment align)
	  {
	    if (cols >= 384) return 0;

	    switch (align)
	    {
	    case IMAGE_CENTER:
	      return (384 - cols) / 2;
	    case IMAGE_RIGHT:
	      return 384 - cols;
	    case IMAGE_LEFT:
	    }

	    return 0;
	  }

	  private static byte[] alignAndPack(byte[][] image, int offset)
	  {
	    int rows = image.length;
	    int cols = image[0].length;
	    System.out.println(""+rows+"   "+cols );
	    byte[] data = new byte[rows * (cols + offset) / 8];

	    int dIdx = 0;

	    for (int y = 0; y < rows; y++)
	    {
	      dIdx += offset / 8;

	      int bit = offset % 8;

	      for (int x = 0; x < cols; x++)
	      {
	        int tmp54_52 = dIdx;
	        byte[] tmp54_50 = data; tmp54_50[tmp54_52] = (byte)(tmp54_50[tmp54_52] << 1);

	        byte pixel = (byte)(image[y][x] < 0 ? 0 : 1);
	        int tmp82_80 = dIdx;
	        byte[] tmp82_78 = data; tmp82_78[tmp82_80] = (byte)(tmp82_78[tmp82_80] | pixel);

	        bit++;

	        if (bit < 8) {
	          continue;
	        }
	        bit = 0;

	        dIdx++;
	      }

	    }

	    return data;
	  }

	  private static int chooseScaleFactor(ImageScale scaling, int width)
	  {
	    if (scaling == ImageScale.SCALE_FULL_IMAGE)
	    {
	      if (width < 384)
	      {
	        scaling = ImageScale.SCALE_ONE_TO_ONE;
	      }
	      else if (width / ImageScale.SCALE_TWO_TO_ONE.getValue() < 384)
	      {
	        scaling = ImageScale.SCALE_TWO_TO_ONE;
	      }
	      else
	      {
	        scaling = ImageScale.SCALE_FOUR_TO_ONE;
	      }
	    }

	    return scaling.getValue();
	  }

	  @SuppressWarnings("unused")
	private static void dumpImageArray(byte[][] image, String filename)
	  {
	    File filepath = 
	      Environment.getExternalStoragePublicDirectory(
	      Environment.DIRECTORY_PICTURES);

	    File binFile = new File(filepath, filename);
	    try
	    {
	    	  OutputStream outStream = new FileOutputStream(binFile);

	      for (int idx = 0; idx < image.length; idx++)
	      {
	        outStream.write(image[idx]);
	      }

	      outStream.close();
	    }
	    catch (IOException e)
	    {
	      e.printStackTrace();
	    }
	  }

}


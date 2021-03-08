package me.felix.pgm.sample;

import android.graphics.Bitmap;

import java.io.IOException;
import java.io.OutputStream;

public class PGMImage {

    private final static String TAG = "PGMImage";

    public final static String MAGIC_NUMBER = "P5";

    // 16 KB
    public final static int MAX_BUFFER_SIZE = 4096;

    private int width, height;

    private byte[] grays;

    PGMImage() {

    }

    PGMImage(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void getGrays(byte[] grays, int offset, int stride, int x, int y, int w, int h) {
        if (w <= 0 || h <= 0) {
            throw new IllegalArgumentException("w or h can not less than 1.");
        }
        if (x + w >= width) {
            throw new IllegalArgumentException("x + w can not big than width.");
        }
        if (y + h >= height) {
            throw new IllegalArgumentException("y + h can not big than height.");
        }
        for (int j = 0; j < h; j++) {
            System.arraycopy(this.grays, x + (y + j) * height, grays, offset + stride * j, w);
        }
    }

    public boolean save(OutputStream os) {
        try {
            os.write(MAGIC_NUMBER.getBytes());
            os.write(0x0A);
            os.write(String.valueOf(width).getBytes());
            os.write(0x20);
            os.write(String.valueOf(height).getBytes());
            os.write(0x0A);
            os.write("255".getBytes());
            os.write(0x0A);
            os.write(grays);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private static byte toGray(int color) {
        int R = (color & 0x00FF0000) >> 16;
        int G = (color & 0x0000FF00) >> 8;
        int B = color & 0x000000FF;
        return (byte) ((299 * R + 587 * G + 114 * B) / 1000);
    }

    public static PGMImage fromBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            throw new IllegalArgumentException("Argument cannot be null.");
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        PGMImage image = new PGMImage(width, height);

        // Calculate appropriate lines.
        int LINES = Math.max(MAX_BUFFER_SIZE / width, 1);

        // Calculate appropriate buffer size.
        int BUFFER_SIZE = LINES * width;

        int[] pixels = new int[BUFFER_SIZE];
        byte[] values = new byte[width * height];

        int i = 0, y = 0, h = height - height % LINES;
        for (; y < h; y += LINES) {
            bitmap.getPixels(pixels, 0, width, 0, y, width, LINES);
            for (int j = 0; j < BUFFER_SIZE; j++) {
                values[i++] = toGray(pixels[j]);
            }
        }

        // Left pixels convert.
        if (h < height) {
            bitmap.getPixels(pixels, 0, width, 0, y, width, height - h);
            for (int j = 0; j < (height - h) * width; j++) {
                values[i++] = toGray(pixels[j]);
            }
        }

        image.grays = values;

        return image;
    }

}

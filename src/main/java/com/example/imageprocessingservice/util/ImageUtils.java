package com.example.imageprocessingservice.util;

import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

public class ImageUtils {
    public static BufferedImage resize (BufferedImage original, int width, int height) {
        return Scalr.resize (original, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, width, height);
    }

    public static BufferedImage crop (BufferedImage original, int x, int y, int width, int height) {
        return Scalr.crop (original, x, y, width, height);
    }

    public static BufferedImage rotate (BufferedImage original, double angle) {
        return Scalr.rotate (original, Scalr.Rotation.valueOf(getRotation(angle)));
    }

    private static String getRotation (double angle) {
        if (angle == 90) return "CW_90";
        if (angle == 180) return "CW_180";
        if (angle == 270) return "CW_270";
        return "NONE";
    }

    public static BufferedImage flip (BufferedImage original) {
        return Scalr.rotate (original, Scalr.Rotation.FLIP_HORZ);
    }

    public static BufferedImage mirror (BufferedImage original) {
        return Scalr.rotate (original, Scalr.Rotation.FLIP_VERT);
    }

    public static BufferedImage addWatermark (BufferedImage original, String watermarkText) {
        BufferedImage watermarked = new BufferedImage (original.getWidth (), original.getHeight (), original.getType ());
        Graphics2D g2d = watermarked.createGraphics ();

        g2d.drawImage (original, 0, 0, null);
        g2d.setFont (new Font ("Arial", Font.BOLD, 30));
        g2d.setColor (new Color (255, 255, 255, 128));
        g2d.drawString (watermarkText, original.getWidth () / 5, original.getHeight () / 2);
        g2d.dispose ();

        return watermarked;
    }

    public static BufferedImage grayscale (BufferedImage original) {
        return Scalr.apply (original, Scalr.OP_GRAYSCALE);
    }

    public static BufferedImage sepia (BufferedImage original) {
        BufferedImage sepia = new BufferedImage (original.getWidth (), original.getHeight (), BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < original.getHeight (); y++) {
            for (int x = 0; x < original.getWidth (); x++) {
                Color c = new Color (original.getRGB (x, y));

                int tr = (int) (0.393 * c.getRed () + 0.769 * c.getGreen () + 0.189 * c.getBlue ());
                int tg = (int) (0.349 * c.getRed () + 0.686 * c.getGreen () + 0.168 * c.getBlue ());
                int tb = (int) (0.272 * c.getRed () + 0.534 * c.getGreen () + 0.131 * c.getBlue ());

                tr = Math.min (255, tr);
                tg = Math.min (255, tg);
                tb = Math.min (255, tb);

                sepia.setRGB (x, y, new Color (tr, tg, tb).getRGB ());
            }
        }

        return sepia;
    }

    public static void writeImage (BufferedImage image, String format, File output) throws IOException {
        ImageIO.write (image, format, output);
    }

    public static BufferedImage readImage(File file) throws IOException {
        return ImageIO.read(file);
    }
}
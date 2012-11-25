/*
 * ImageProcessor.java
 * An image processor to generate frammed images with copyright messages
 * Copyright (C) 2006  Paulo Pinto
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.progtools;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

/**
 * A class that processes images to be placed in a
 * Website photo gallery, with frames and copyright
 * messages.
 */
public class ImageProcessor {
    private static final int FRAME_WIDTH = 20;
    
    private String destinationDir;
    private String copyrightMsg;
    
    private List<ImageProcessorListener> listeners;
    
    /**
     * Creates an image processor.
     * @param destinationDir A pathname to the directory where to place the generated images
     * @param copyrightMsg The copyright message to place in the images
     */
    public ImageProcessor(String destinationDir, String copyrightMsg) {
        this.destinationDir = destinationDir;
        this.copyrightMsg = copyrightMsg;
        this.listeners = new ArrayList<ImageProcessorListener>(10);
    }
    
    /**
     * Converts the given image to the dimensions specified in the constructor.
     * An image frame is added, as well as the copyright message.
     * @param sourceImage The image to convert.
     * @param size A value between 0 and 100 to say how much we need to reduce the image
     */
    public void convertImage(File sourceImage, int size) {
        assert size >= 0 && size <= 100;
        try {
            // Load the images
            BufferedImage originalImg = ImageIO.read(sourceImage);
            
            
            // Calculate the size for the new image
            int width = originalImg.getWidth();
            int height = originalImg.getHeight();
            if (width > height) {
                float ratio = (float) height / width;
                width *= ((float)size / 100);
                height = (int) (ratio * width);
            } else {
                float ratio = (float) width / height;
                height *= ((float)size / 100);
                width = (int) (ratio * height);
            }
            BufferedImage convertedImg = new BufferedImage(width + FRAME_WIDTH * 2,
                    height + FRAME_WIDTH * 2, originalImg
                    .getType());
            
            // Convert into a new one with frames
            Graphics2D g = (Graphics2D) convertedImg.getGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, convertedImg.getWidth(), convertedImg.getHeight());
            
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, convertedImg.getWidth(), convertedImg.getHeight());
            
            g.drawImage(originalImg, FRAME_WIDTH, FRAME_WIDTH, convertedImg
                    .getWidth() - (FRAME_WIDTH * 2), convertedImg.getHeight() - (FRAME_WIDTH * 2), null);
            
            
            // Add the copyright message
            // In the future I might change this font for a font effect like
            // embossed text.
            Font fnt = new Font("Arial", Font.BOLD, 16);
            g.setFont(fnt);
            FontMetrics metrics = g.getFontMetrics(fnt);
            int hgt = metrics.getHeight();
            int adv = metrics.stringWidth(copyrightMsg) + 5; // The 5 is a prefix padding for the text
            
            //g.setXORMode(Color.WHITE);
            g.setColor(Color.WHITE);
            g.drawString(copyrightMsg, convertedImg.getWidth() - FRAME_WIDTH - adv, convertedImg.getHeight() - FRAME_WIDTH - hgt);
            
            // Save the converted image
            String destPath = destinationDir + File.separator + sourceImage.getName();
            ImageIO.write(convertedImg, "jpg", new File(destPath));
            notifyListeners(sourceImage);
        } catch (IOException e) {
            System.out.println("An error occurred while converting the image: " +  e.getMessage());
        }
    }
    
    /**
     * Processes all the image files from the given directory.
     * The images will be generated with the same name in the destination
     * directory given in the constructor.
     * @param pathname A pathname for a directory
     * @param size A value between 0 and 100 to say how much we need to reduce the images
     */
    public void processFiles(String pathname, int size) {
        File imagesDir = new File(pathname);
        for (File image: imagesDir.listFiles()) {
            if (image.getName().toLowerCase().endsWith(".jpg")) {
                convertImage(image, size);
            }
        }
    }
    
    /**
     * Notifies all listeners of the processed image.
     * It makes use of SwingUtilities so that Swing code can
     * also be notified of the progress.
     * @param imageFile The image that was just processed.
     */
    private void notifyListeners(final File imageFile) {
        // Make sure all listeners are updated in a thread safe way
        try {
            Runnable notifier = new Runnable() {
                public void run() {
                    for (ImageProcessorListener listener : listeners)
                        listener.processedImage(imageFile);
                }
            };
            SwingUtilities.invokeAndWait(notifier);
        } catch (Exception e) {
            System.out.println("Error while invonking listeners: " + e.getMessage());
        }
    }
    
    /**
     * Adds a new listener client to this class
     * @param listener The listener to add.
     */
    public void addImageProcessorListener(ImageProcessorListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Removes a listener client to this class
     * @param listener The listener to add.
     */
    public void removeImageProcessorListener(ImageProcessorListener listener) {
        listeners.remove(listener);
    }
    
}


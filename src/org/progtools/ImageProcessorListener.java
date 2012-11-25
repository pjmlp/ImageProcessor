/*
 * ImageProcessorListener.java
 * Listener interface for the image processor
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

import java.io.File;

/**
 * Listener interface for classes interested in image processing
 * events.
 * @author Paulo
 */
public interface ImageProcessorListener {
    
    /**
     * Invoked when an imaged gets processed by the image processor
     * @param pathname Pathname to the processed image.
     */
    void processedImage(File pathname);
}

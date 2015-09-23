/*
 *  Copyright (C) 2011 Axel Morgner, structr <structr@structr.org>
 * 
 *  This file is part of structr <http://structr.org>.
 * 
 *  structr is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  structr is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with structr.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author axel
 */
public class ImageHelper {

   

    /**
     * Let ImageIO read and write a JPEG image. This should normalize all types of weird
     * image sub formats, e.g. when extracting images from a flash file.
     *
     * Some images can not be read by ImageIO (or the browser) because they
     * have an JPEG EOI and SOI marker at the beginning of the file.
     *
     * This method detects and removes the bytes, so that the image
     * can be read again.
     *
     * @param original
     * @return normalized image
     */
    public static byte[] normalizeJpegImage(final byte[] original) {

        if (original == null) {
            return new byte[]{};
        }

        ByteArrayInputStream in = new ByteArrayInputStream(original);

        // If JPEG image starts with ff d9 ffd8, strip this sequence from the beginning

        // FF D9 = EOI (end of image)
        // FF D8 = SOI (start of image)

        if (original[0] == (byte) 0xff && original[1] == (byte) 0xd9 && original[2] == (byte) 0xff && original[3] == (byte) 0xd8) {
            in.skip(4);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedImage source;

        try {

            source = ImageIO.read(in);

            // If ImageIO cannot read it, return original
            if (source == null) {
                return original;
            }

            ImageIO.write(source, "jpeg", out);

        } catch (IOException ex) {
        }

        return out.toByteArray();
    }
}

package edu.artic.datagrinder;

import javax.servlet.ServletException;

import magick.DrawInfo;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ImageController {

  @RequestMapping(value="/image/resize", method = RequestMethod.GET)
  public String resizeForm() {
    return "imageresize";
  }

  @RequestMapping(value="/image/resize", method = RequestMethod.POST, produces = "image/jpeg")
  @ResponseBody
  public byte[] resizeSubmit(@RequestParam("file") CommonsMultipartFile file,
                             @RequestParam("width") int pWidth,
                             @RequestParam("height") int pHeight) throws ServletException {

    try {
      if (file.isEmpty()) {
        String size = "200x200";
        if (pWidth == 0 || pHeight == 0) {
          size = pWidth + "x" + pHeight;
        }

        ImageInfo info = new ImageInfo();
        info.setSize(size);
        DrawInfo drawInfo = new DrawInfo(info);
        drawInfo.setText("No image was sent");
        MagickImage image = new MagickImage(info);
        image.setImageFormat("JPEG");
        image.setCompression(85);
        image.drawImage(drawInfo);

        byte[] returnBytes = image.imageToBlob(info);

        image.destroyImages();
        image = null;

        return returnBytes;
      }
      else {
        byte[] tiff = file.getBytes();
        ImageInfo info = new ImageInfo();
        MagickImage image = new MagickImage(info, tiff);

        // If no dimensions were passed, use the original image's dimensions
        double origW = image.getDimension().getWidth();
        double origH = image.getDimension().getHeight();
        if (pWidth == 0) {
          pWidth = new Double(origW).intValue();
        }
        if (pHeight == 0) {
          pHeight = new Double(origH).intValue();
        }

        // Determine the scale ration to size the image to
        double scale = 1.0f;
        if (pWidth > 0 && origW > pWidth) {
          scale = (float) pWidth / origW;
        }
        if (pHeight > 0 && origH > pHeight) {
          double scaleAlt = (float) pHeight / origH;
          if (scaleAlt < scale) {
            scale = scaleAlt;
          }
        }

        // Now resize the image
        MagickImage derivative = image.scaleImage(new Double(origW * scale).intValue(), new Double(origH * scale).intValue());

        derivative.setImageFormat("JPEG");
        derivative.setCompression(85);

        // Copy to the output stream
        byte[] returnBytes = derivative.imageToBlob(info);

        derivative.destroyImages();
        derivative = null;
        image = null;

        // Set content type to image/jpeg
        return returnBytes;
      }
    }
    catch (MagickException e) {
      throw new ServletException("Problem creating an ImageMagick instance.");
    }
  }
}

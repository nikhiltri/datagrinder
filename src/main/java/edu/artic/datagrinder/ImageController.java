package edu.artic.datagrinder;

import javax.servlet.ServletException;

import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Controller
public class ImageController {

  protected final Log logger = LogFactory.getLog(getClass());

  @RequestMapping(value="/image/resize", method = RequestMethod.GET)
  public String resizeForm() {
    return "imageresize";
  }

  @RequestMapping(value="/image/resize", method = RequestMethod.POST, produces = "image/jpeg")
  @ResponseBody
  public byte[] resizeSubmit(@RequestParam("file") CommonsMultipartFile file,
                             @RequestParam("width") Integer pWidth,
                             @RequestParam("height") Integer pHeight) throws ServletException {

    try {
      if (file.isEmpty()) {
        throw new ServletException("No image uploaded.");
      }
      else {
        byte[] tiff = file.getBytes();
        ImageInfo info = new ImageInfo();
        MagickImage image = new MagickImage(info, tiff);

        // If no dimensions were passed, use the original image's dimensions
        double origW = image.getDimension().getWidth();
        double origH = image.getDimension().getHeight();
        int pW = (pWidth == null ? 0 : pWidth.intValue());
        int pH = (pHeight == null ? 0 : pHeight.intValue());
        if (pW == 0) {
          pW = new Double(origW).intValue();
        }
        if (pH == 0) {
          pH = new Double(origH).intValue();
        }

        // Determine the scale ration to size the image to
        double scale = 1.0f;
        if (pW > 0 && origW > pW) {
          scale = (float) pW / origW;
        }
        if (pH > 0 && origH > pH) {
          double scaleAlt = (float) pH / origH;
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
      logger.error("Problem creating an ImageMagick instance", e);
      throw new ServletException("Problem creating an ImageMagick instance: " + e.getMessage());
    }
  }
}

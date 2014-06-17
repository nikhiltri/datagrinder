package edu.artic.datagrinder;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;

import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingClientConnectionManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import static java.lang.Integer.MAX_VALUE;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.http.HttpStatus.SC_OK;

@Controller
public class ImageResizeController {

  protected final Log logger = LogFactory.getLog(getClass());

  private DefaultHttpClient httpClient;

  private String fcrepoHost;

  private int fcrepoPort;

  private String fcrepoUsername;

  private String fcrepoPassword;

  @PostConstruct
  public void init() {
    fcrepoHost = System.getProperty("fcrepo.host", "localhost");
    fcrepoPort = Integer.parseInt(System.getProperty("fcrepo.host", "8080"));
    fcrepoUsername = System.getProperty("fcrepo.username", "");
    fcrepoPassword = System.getProperty("fcrepo.password", "");

    final PoolingClientConnectionManager connMann = new PoolingClientConnectionManager();
    connMann.setMaxTotal(MAX_VALUE);
    connMann.setDefaultMaxPerRoute(MAX_VALUE);

    httpClient = new DefaultHttpClient(connMann);
    httpClient.setRedirectStrategy(new DefaultRedirectStrategy());
    httpClient.setHttpRequestRetryHandler(new StandardHttpRequestRetryHandler(0, false));

    // If the Fedora instance requires authentication, set it up here
    if (!isBlank(fcrepoUsername) && !isBlank(fcrepoPassword)) {
      logger.debug("Adding BASIC credentials to client for repo requests.");

      CredentialsProvider credsProvider = new BasicCredentialsProvider();
      credsProvider.setCredentials(new AuthScope(fcrepoHost, fcrepoPort),
                                   new UsernamePasswordCredentials(fcrepoUsername, fcrepoPassword));

      httpClient.setCredentialsProvider(credsProvider);
    }
  }

  @RequestMapping(value="/image/resize", method = RequestMethod.GET)
  public String resizeForm() {
    return "imageresize";
  }

  @RequestMapping(value="/image/resize.jpg", method = RequestMethod.POST, produces = "image/jpeg")
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

  @RequestMapping(value="/image/resize.jpg", method = RequestMethod.GET, produces = "image/jpeg")
  @ResponseBody
  public byte[] resizeSubmit(@RequestParam("file") String path,
                             @RequestParam("width") Integer pWidth,
                             @RequestParam("height") Integer pHeight) throws ServletException {

    try {
      if ("".equals(path)) {
        throw new ServletException("No image specified.");
      }
      else {
        logger.debug("Trying to get " + path);

        InputStream is;

        // Get image content with apache httpclient library
        final HttpUriRequest request = new HttpGet(path);
        final HttpResponse response = httpClient.execute(request);
        if (response.getStatusLine().getStatusCode() == SC_OK) {
          is = response.getEntity().getContent();
        }
        else {
          throw new ServletException(response.getStatusLine().toString());
        }

        // Get image content with default Java libraries
        /*
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        InputStream is = conn.getInputStream();
        */

        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);

        byte[] buffer = new byte[1024];
        int len;

        while((len = is.read(buffer)) >= 0) {
          out.write(buffer, 0, len);
        }

        is.close();
        out.close();
        is = null;
        buffer = null;

        byte[] tiff = out.toByteArray();

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
    catch (MalformedURLException e) {
      logger.error("Problem fetching image by URL", e);
      throw new ServletException("Problem fetching image by URL: " + e.getMessage());
    }
    catch (IOException e) {
      logger.error("Problem reading/writing tiff image", e);
      throw new ServletException("Problem reading/writing tiff image: " + e.getMessage());
    }
  }
}

package edu.artic.datagrinder;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ImageController {
  
  @RequestMapping(value="/image/resize", method = RequestMethod.GET)
  public String setupForm() {
    return "imageresize";
  }
}

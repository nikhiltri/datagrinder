<html>
<head><title>Image Resize - Datagrinder</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<body>
<h1>Image Resize POST</h1>
<p>Use this form to upload an image to be resized to the dimensions of your choosing.</p>

      <form method="post" action="resize.jpg" autocomplete="off" enctype='multipart/form-data'>
      <label for"imagedata">Image:</label>
      <input type="file" id="file" name="file" /><br/>

      <label for"dimsw">Width:</label>
      <input type="text" id="width" name="width" /><br/>

      <label for"dimsw">Height:</label>
      <input type="text" id="height" name="height" /><br/>

      <input type="submit" value="Submit"/>
      </form>



<h1>Image Resize GET</h1>
<p>Use this formt to send an image URL to be resized to the dimensions of your choosing.</p>

      <form method="get" action="resize.jpg" autocomplete="off">
      <label for"imagedata">Image URL:</label>
      <input type="text" id="file" name="file" /><br/>

      <label for"dimsw">Width:</label>
      <input type="text" id="width" name="width" /><br/>

      <label for"dimsw">Height:</label>
      <input type="text" id="height" name="height" /><br/>

      <input type="submit" value="Submit"/>
      </form>

</body>
</html>

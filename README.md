thumbnailer
===========

Thumbnail generation library that provides media detection, conversion 
(as necessary), and thumbnail generation.

API
---

Thumbnailer handles the media type detection and uses a fluid API design to 
keep things simple, so for any supported file type (ie png, jpg, gif, pdf, ppt...)
you use the same API:

```java
Thumbnailer.thumb(in).height(400).create(outFile);

```

Check out Thumbnailer.Builder API to see what you can do.

Extending Thumbnailer
---------------------

Thumbnailer comes with several media converters (PDF, PPT, PTTX, and ImageIO) 
uses java.util.ServiceLoader to discover and load media converters, however 
you can provide your own converters by simply implementing the 
org.geoint.thumbnailer.converter.MediaConverter interface and adding your 
converters class name to a service file, as defined by the 
[ServiceLoader](http://docs.oracle.com/javase/7/docs/api/java/util/ServiceLoader.html) API.
Thumbnailer will pick up the plugin and will call it for any matching media 
type.

# petsciiator
A converter tool to convert JPG/PNG images into Commodore PETSCII.

![source image](https://github.com/EgonOlsen71/petsciiator/blob/master/examples/ninja.png)  ==>  ![source image](https://github.com/EgonOlsen71/petsciiator/blob/master/examples/petscii/ninja_petscii.png)


Input can by any JPG/PNG file of any (reasonable) size. The image will be scaled down to 320*200, so make sure that the aspect ratio of the source image fits this more ore less.
There are several output formats to choose from:

* image: a 320*200 PNG file
* basic: a BASIC program in ASCII format for the C64 that displays the image
* bbs: a SEQ file containing the image for usage in a BBS (you might want to use the /lowercase=true switch in combination with this one)
* bin: two SEQ files containing the raw screen and color ram data

Everything needed to start the converter can be found in the dist-directory. There, you'll find the JAR file and two batch files (one for Windows, one for Linux) to start the converter.
When you start the converter using one of these batch files without specifying any addional options (but at least with a source file), it will take the source file (or, if it's a source directory, all fitting images in that directory) and convert them into PETSCII using the default settings. It will create one image and one basic output file for each source image (see above). You can change this behaviour by using...

...command line options (either with / or - as prefix):

/target=<target folder> - the target folder in which the generated files will be written. Default is the current work directory.

/format=<image,basic,bbs,bin> - the output format(s). Multiple formats can be specified by separating them by kommas. Default is image,basic

/prescale=<1-4> - scales the image down before generating the PETSCII from it. This can help to reduce artifacts in some cases. A value of 4 basically results in a 80*50 image. Default is 1.

/exclude=<code1,code2,code3...> - excludes additional characters from the conversion. The codes have to be a valid PETSCII char codes.

/noalpha=<true|false> - if true, all alphanumerical characters will be excluded from the conversion. Default is false.

/colormode=<0|1|2> - sets the mode used for color conversions. Usually, the impact of changing this isn't very huge. Default is 0.

/lowercase=<true|false> - if true, the lower case PETSCII characters will be used for the conversion. Default is false.


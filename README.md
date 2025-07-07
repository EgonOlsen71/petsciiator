# petsciiator
A converter tool to convert JPG/PNG images into Commodore PETSCII. There is an online version of it available here: https://jpct.de/petscii

In addition, you can also convert into Koala Painter format.

![source image](https://github.com/EgonOlsen71/petsciiator/blob/master/examples/ninja.png)  ==>  ![petscii image](https://github.com/EgonOlsen71/petsciiator/blob/master/examples/petscii/ninja_petscii.png)


Input can be any JPG/PNG file of any (reasonable) size. The image will be scaled down to 320*200, so make sure that the aspect ratio of the source image fits this more or less.
There are several output formats to choose from:

* **image**: a 320*200 PNG file
* **basic**: a BASIC program in ASCII format for the C64 that displays the image
* **bbs**: a SEQ file containing the image for usage in a BBS (you might want to use the /lowercase=true switch in combination with this one)
* **bin**: two SEQ files containing the raw screen and color ram data
* **koala**: not a PETSCII variant, but a multi-color image in Koala Painter format
* **hires**: not a PETSCII variant, but a hires image in Hi-Eddi+ format

<img src="https://github.com/EgonOlsen71/petsciiator/blob/master/examples/pet4032-12.jpg" width="320" height="200">  ==>  ![petscii image](https://github.com/EgonOlsen71/petsciiator/blob/master/examples/petscii/pet4032-12_petscii.png)


Everything needed to start the converter can be found in the dist-directory. There, you'll find the JAR file and two batch files (one for Windows, one for Linux) to start the converter.
When you start the converter using one of these batch files without specifying any addional options (but at least with a source file), it will take the source file (or, if it's a source directory, all fitting images in that directory) and convert them into PETSCII using the default settings. It will create one image and one basic output file for each source image (see above). You can change this behaviour by using...

...command line options (either with / or - as prefix):

**/target=<target folder>** - the target folder in which the generated files will be written. Default is the current work directory.

**/format=<image,basic,bbs,bin,koala,hires>** - the output format(s). Multiple formats can be specified by separating them by kommas. Default is image,basic

**/prescale=<1-4>** - scales the image down before generating the PETSCII from it. This can help to reduce artifacts in some cases. A value of 4 basically results in a 80*50 image. Default is 1.

**/exclude=<code1,code2,code3...>** - excludes additional characters from the conversion. The codes have to be valid PETSCII char codes.

**/noalpha=<true|false>** - if true, all alphanumerical characters will be excluded from the conversion. Default is false.

**/colormapper=<colorful|soft|dither>** - sets the mapper that maps the source image's colors to the VIC II colors. Default is 'colorful'

**/colormode=<0|1|2>** - sets the color conversion mode when using the soft color mapper. Usually, the impact of changing this isn't very huge. Default is 0.

**/lowercase=<true|false>** - if true, the lower case PETSCII characters will be used for the conversion. Default is false.

**/background=<0-15>** - overrides the auto detected background color. Can be useful to get more details in regions of the image, where the auto detected color isn't used much. Default is auto detect.

**/platform=<C64|264>** - specifies the target platform, default is C64. The 264 platform (C16/C116/Plus4) offers more colors.

**/dither=<0-100>** - dithering strength for the koala painter and hires conversion. This has no influence on the PETSCII conversion. 


# /colormapper and /background explained

In this section, I'll explain the **/colormapper** and the **/background** settings in a little more detail, because it might not be obvious what they do but they can have a huge impact on image quality. Let's take this image of a joystick as an example:

![joystick](https://jpct.de/pix/joystick/stick.jpg)

By default, the colorful color mapper will be used and the background color will be auto detected. In this case, color 15 is taken as the background color. This will provide the most detail in regions of the image which contain at least one pixel in this color. You can clearly see this on the fabric in the background and the stick's edges. The call to generate this image is simple: *petscii stick.jpg*

![joystick color 15](https://jpct.de/pix/joystick/stick_15.png)

But maybe, the auto detected color isn't the one that should show the most details. In this case, you might want the actual stick to have more details while the fabric in the background doesn't really matter. Because the stick is black (=color 0 on the Commodore 64), you can force the background color to be 0. The call looks like this: *petscii stick.jpg -background=0* and this is the result:

![joystick color 0](https://jpct.de/pix/joystick/stick_0.png)

As you can see, the body of the stick shows more details now while the background is more or less a solid gray area with some white blocks here and there. However, one might think that it should be possible to show even more details on the stick's body. And that's actually true in this case. You can use a different color mapper, one that applies dithering when reducing the color depth of the image. For some images, the result might look a bit too chaotic when using this option, but in this case, it actually adds some detail. The call looks like this: *petscii stick.jpg -background=0 -colormapper=dither* and the result like this:

![joystick color 0](https://jpct.de/pix/joystick/stick_dither_0.png)

As you can see, **/colormapper** and **/background** can have a huge impact on the outcome. However, whether they improve the petscii image or not highly depends on the source image itself. For example, you can apply the dithering-color mapper without setting the background color to black on the source image as well. If the result is better with or without it, is a matter of taste (call: *petscii stick.jpg -colormapper=dither*):

![joystick color 0](https://jpct.de/pix/joystick/stick_dither.png)

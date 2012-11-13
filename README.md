Augie
=====
Augmented reality Tools, Libraries, and Applications
----------------------------------------------------

### TODO
1. ~~Draw~~
  * ~~Etchasketch-like~~
  * ~~Shake Clear~~
1. ~~Draw Horizons~~
  * ~~Horizon and Vertical~~
  * ~~Level~~
  * ~~Drag Delete~~
1. ~~Touch Shutter~~
1. ~~RAW~~
1. Touch Focus
1. ~~Create factory for pre-ics and post-ics~~
1. Rect Object
  * a 'point is within boards' function
  * a 'move' function(from_point, to_point)
1. Gesture Object
  * recognize clockwise / couner-clockwise
  * convert to rect
1. TouchFocusShutterFeature
  * on 'up' redraw scrible as rectangle
      * unless moving rect
      * blue for focus
      * green for meter
  * adjust position of rect to camera movement
  * adjust position of rect to faces (snap to nearby face)
  * rect should follow faces
1. Settings Activity
  * ~~basic legacy prefs activity~~
  * support ice cream fancy stuff
  * add camera settings stored by name
1. Properties Activity
1. Feature Dependancy Manager
  * unregister features when they are turned off
  * register features
      * when they are turned on 
      * with a set of required features

### BUGS
1. release camera on suspend better, why does prev freeze?
1. detect boarders more reliably
1. horizon gets confussed if device is oriented diff



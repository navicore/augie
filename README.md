Augie
=====
Augmented reality Tools, Libraries, and Applications
----------------------------------------------------

### TODO
1. Splash Screen
1. ~~Draw~~
  * ~~Etchasketch-like~~
  * ~~Shake Clear~~
1. ~~Draw Horizons~~
  * ~~Horizon and Vertical~~
  * ~~Level~~
  * ~~Drag Delete~~
  * calibration
1. ~~Touch Shutter~~
1. ~~RAW~~
1. ~~Create factory for pre-ics and post-ics~~
1. Rect Object
  * a 'point is within bounds' function
  * a 'move' function(from_point, to_point)
1. ~~Gesture Object~~
  * ~~recognize clockwise / couner-clockwise~~
  * ~~convert to rect~~
1. TouchFocusShutterFeature
  * ~~take picture on 'up'~~
  * ~~on scrible 'up' redraw scrible as rectangle~~
      * ~~unless moving rect~~
      * ~~blue for focus~~
      * ~~green for meter~~
  * adjust position of rect to camera movement
  * adjust position of rect to faces (snap to nearby face)
  * rect should follow faces
1. Pano Helper
  * a begin/end
      * button
      * gesture
  * remember horizon and adjust line as panning
  * remember frame borders
    * with marks on horizon line (easy)
    * with frame (harder)
  * stitcher
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
1. GPS Tagging
1. Fire sharing Intents
  * email
  * g+
  * instagram
  * facebook
1. Fire processing Intents
  * instagram
  * photoshop
1. Processing
  * Edit
      1. crop
      1. rotate
      1. brightness
      1. sharpness
      1. noise reduction
      1. saturation
  * Effects
1. Flat (or angle!) Compass Overlay with moon and sun path info
1. Text Overlay for local info modules
  * Sun (rise set compass angle) module
  * Moon (rise set compass angle and size) module
  * Tide info module
  * Surf info module
  * Temp

### BUGS
1. ~~camera does not update preview when resumed after 
   power button (but does if 'home' is hit first~~
1. detect boarders more reliably
1. horizon gets confused if device is oriented diff
1. when action bar is pulled down preview resizes


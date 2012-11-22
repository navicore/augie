Augie
=====
Augmented reality Tools, Libraries, and Applications
----------------------------------------------------

### TODO
1. Splash Screen
1. ~~Draw Horizons~~
  * ~~Horizon and Vertical~~
  * ~~Level~~
  * ~~Drag Delete~~
  * calibration
1. Social features of 'these are mine'
  * these are mine (no idea)
  * annotations of real objects
  * share configs
  * augie friend finder
      * augielay
      * map
      * an expiring "let them find me for the next 2 hours" feature
      * avitars
1. Shutter
  * ~~Touch Shutter~~
  * Shutter Button Augiement
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
1. Feature Dependancy Manager
  * unregister features 
      * when they are turned off
      * and unreg dependent features
  * ~~register features~~
      * ~~when they are turned on~~
      * ~~with a set of required features~~
1. GPS Tagging
1. Photo Sharing
1. Photo Post Editing
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
1. when action bar is pulled down preview resizes (only on 2.3)


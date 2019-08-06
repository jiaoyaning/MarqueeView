# MarqueeView
可以调节间距的TextView跑马灯

#### GIF
![Screenshot](screenshot/screenshot.gif)  

##Gradle  
Step 1. Add it in your root build.gradle at the end of repositories:

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
Step 2. Add the dependency 


```
 dependencies {
     compile 'com.jyn.marqueeview:marqueetextview:1.0.0'
}
 ```
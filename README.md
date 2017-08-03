# Album
这是一个相册，满足一般图片选择需求，并演示了三种常用的图片处理场景。
<br>场景一：多张图片的选择，提供预览功能。
<br>场景二：头像选择编辑，并演示了一种以头像模糊作为工具栏背景的场景。
<br>场景三：多张图片的点击预览的场景。

# demo
动态图太卡了，真机看效果吧 https://github.com/Carpten/Album/blob/master/demo.apk

# Dependency

Add this in your root build.gradle file (not your module build.gradle file):

allprojects {
	repositories {
        maven { url "https://jitpack.io" }
    }
}
Then, add the library to your module build.gradle

dependencies {
    compile 'com.github.Carpten:Album:1.0'
}

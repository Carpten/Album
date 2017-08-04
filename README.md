# Album
这是一个相册，满足一般图片选择需求，并演示了三种常用的图片处理场景:
<br>一、多张图片的选择，提供预览功能。
<br>二、头像选择编辑，并演示了一种以头像模糊作为工具栏背景的场景。
<br>三、多张图片的点击预览的场景。

# Demo
https://github.com/Carpten/Album/blob/master/demo.apk

# Dependency
首先在工程build.gradle中添加:

 allprojects {
     repositories {
         ...
         maven { url 'https://www.jitpack.io' }
     }
 }
 
 添加依赖：
 compile 'com.github.Carpten:Album:1.0'

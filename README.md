# Album
这是一个相册，满足一般图片选择需求，并演示了三种常用的图片处理场景:
<br>一、多张图片的选择，提供预览功能。
<br>二、头像选择编辑，并演示了一种以头像模糊作为工具栏背景的场景。
<br>三、多张图片的点击放大展示的场景。

# Demo
https://github.com/Carpten/Album/blob/master/demo.apk

# Dependency
首先在工程build.gradle中添加:

```gradle
allprojects {
	repositories {
        maven { url "https://jitpack.io" }
    }
}
```
然后添加依赖：

```gradle
dependencies {
    compile 'com.github.Carpten:Album:2.2'
}
```

# Usage
选择头像：
```gradle
Intent intent = new Intent(thisactivity, AlbumActivity.class);
intent.putExtra(AlbumActivity.ARG_MODE, AlbumActivity.MODE_PORTRAIT);
startActivityForResult(intent, INTENT_CODE);
```

```gradle
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (requestCode == INTENT_CODE && resultCode == RESULT_OK) {
		String path = data.getStringExtra(AlbumActivity.ARG_PATH);
		...
	}
}
```

多张图片选择：
```gradle
Intent intent = new Intent(thisactivity, AlbumActivity.class);
intent.putExtra(AlbumActivity.ARG_MODE, AlbumActivity.MODE_MULTI_SELECT);
intent.putExtra(AlbumActivity.ARG_MAX_COUNT, 8);//图片最大数量
Bundle bundle = new Bundle();
bundle.putSerializable(AlbumActivity.ARG_DATA, mImageBeen);//传入已选择的图片
intent.putExtras(bundle);
startActivityForResult(intent, INTENT_CODE);
```

```gradle
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (requestCode == INTENT_CODE && resultCode == RESULT_OK) {
		ArrayList<ImageBean> imageBeen = (ArrayList<ImageBean>) data.getSerializableExtra(AlbumActivity.ARG_DATA);
		...
	}
}
```

单张图片选择：
```gradle
Intent intent = new Intent(thisactivity, AlbumActivity.class);
intent.putExtra(AlbumActivity.ARG_MODE, AlbumActivity.MODE_SINGLE_SELECT);
startActivityForResult(intent, INTENT_CODE);
```

```gradle
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (requestCode == INTENT_CODE && resultCode == RESULT_OK) {
		ImageBean imageBean = (ImageBean) data.getSerializableExtra(AlbumActivity.ARG_DATA);
		...
	}
}
```

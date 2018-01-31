#觀看這個READ ME的時候，請點進來用RAW來看
#這是我為了方便寫出來的一些工具類

*********************Android ImageZoom*************************
ImageView 放大滑動的方法：
1.先到https://github.com/chrisbanes/PhotoView將
allprojects {
	repositories {
        maven { url "https://jitpack.io" }
    }
}
中的maven {url “https://jitpack.io”}複製到Android studio 的build.gradle(專案)內。
2.dependencies {
    implementation 'com.github.chrisbanes:PhotoView:2.1.3'
}
之後把這行複製到build.gradle (app)內，2.1.3是版本，需要看網頁中JitPack顯示的版本。
3.設定ImageView 元件<ImageView
    android:id="@+id/image"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_centerHorizontal="true"
    android:layout_centerVertical="true"
    android:layout_gravity="center"
    android:scaleType="matrix"
    app:srcCompat="@mipmap/ic_launcher" />
以上是範例，scaleType似乎只能設定matrix，不然不會執行。

PhotoViewAttacher mAttacher;
imageuri = Uri.parse(get.getStringExtra("image"));
        imageView.setImageBitmap(new ImageTools(this).imageBitmap(imageuri));
        mAttacher = new PhotoViewAttacher(imageView);
***********************StringTools**************************

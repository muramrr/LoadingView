# LoadingView

Custom loading view based on dribbble shot

<img src="/untitled.gif" alt="sample" title="sample" align="right" vspace="52" height="500"/>

Originally based on this [Dribbble shot](https://dribbble.com/shots/5095383-Loader-Animation).

<a href="https://dribbble.com/shots/8154883-Pizza-order-system">
  <img alt="Design on Dribbble" src="https://github.com/muramrr/misc/blob/master/dribbble.png" width="200" height="80" hspace="15" />
</a>

## Installation

Add jitpack repo in your project.gradle
```gradle
allprojects {
    repositories {
    ...
      maven { url 'https://jitpack.io' }
    }
}
 ```

Add implementation in your app.gradle
```gradle
dependencies {
   implementation 'com.github.muramrr:LoadingView:1.0'
}
```

## Usage

```xml
<com.mmdev.loadingviewlib.LoadingView
      android:id="@+id/loadingView"
      android:layout_height="100dp"
      android:layout_width="100dp"
      
      app:loadStrokeColor="@android:color/white"
      app:loadStrokeWidth="6dp"
      
      app:layout_constraintBottom_toBottomOf="parent"
      app:layout_constraintLeft_toLeftOf="parent"
      app:layout_constraintRight_toRightOf="parent"
      app:layout_constraintTop_toTopOf="parent"
      
      />

```
**By default animation is running automatically**

You can pause and resume animation in your activity/fragment:

```kotlin
loadingView.setOnClickListener {
    loadingView.toggleAnimation()
}

```



## License

[GitHub license](https://github.com/muramrr/LoadingView/blob/master/LICENSE)


```
Copyright (c) 2020 Andrii Kovalchuk
```
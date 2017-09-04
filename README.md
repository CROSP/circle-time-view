# Circle Time View
[![Android Arsenal](http://img.shields.io/badge/Android%20Arsenal-CircleTimeView-blue.svg?style=flat)](https://android-arsenal.com/details/1/6151)

<img src="https://crosp.net/wp-content/uploads/2017/09/CircleTimeViewTitle.jpg">

**Circle Time View** is an android custom view for displaying time in a pretty format. The View is highly customizable, so you can adapt the appearance according your needs. 

You can use this view to create countdown timers, alarms or just allow user to set time manually.

This library requires [API level 8](http://developer.android.com/guide/topics/manifest/uses-sdk-element.html#ApiLevels) or higher.

### Screenshots
<img src="https://crosp.net/wp-content/uploads/2017/09/Screenshot_1504370428.png" width="400">
<img src="https://crosp.net/wp-content/uploads/2017/09/Screenshot_1504370421.png" width="400">
<img src="https://crosp.net/wp-content/uploads/2017/09/Screenshot_1504370357.png" width="400">
<img src="https://crosp.net/wp-content/uploads/2017/09/Screenshot_1504370365.png" width="400">
<img src="https://crosp.net/wp-content/uploads/2017/09/Screenshot_1504370362.png" width="400">

### Demo application
<a href="https://play.google.com/store/apps/details?id=net.crosp.android.circletimeviewexample">
<img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" width="300"  />
</a>

## Requirements

Target Sdk Version : **24** 

Min Sdk Version : **8**

## Integration
Using Gradle:

```gradle
dependencies {
  compile 'net.crosp.libs.android:circle-time-view:1.0.6'
}
```

Or using Maven:

```xml
<dependency>
  <groupId>net.crosp.libs.android</groupId>
  <artifactId>circle-time-view</artifactId>
  <version>1.0.6</version>
  <type>pom</type>
</dependency>
```

## Usage
Use it in your XML layouts:
```xml
    <net.crosp.libs.android.circletimeview.CircleTimeView
        android:id="@+id/circle_timer_view"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="0.9"
        app:ctvCircleButtonColor="#099ef4"
        app:ctvCircleColor="#00d9ff"
        app:ctvCircleHandButtonRadius="10dp"
        app:ctvCirclePressedButtonColor="#08bfdf"
        app:ctvCircleStrokeWidth="1dp"
        app:ctvCurrentTimeInSeconds="160"
        app:ctvHighlightMarkLineColor="#00f7ff"
        app:ctvLabelText="The сlock is ticking"
        app:ctvLabelTextColor="#fff"
        app:ctvLabelTextSize="18sp"
        app:ctvLapBackgroundColor="#9adf1b"
        app:ctvLapLabelMarginTop="15dp"
        app:ctvLapLabelTextSize="12sp"
        app:ctvLapTextColor="#fff"
        app:ctvMarkLineColor="#ffffff"
        app:ctvMarginTopLabel="22dp"
        app:ctvMarkLineWidth="1dp"
        app:ctvMarkSize="10dp"
        app:ctvMinutesMarkCount="120"
        app:ctvMultiLapRotation="true"
        app:ctvPaddingInnerRadius="15dp"
        app:ctvPaddingQuarterNumber="5dp"
        app:ctvQuarterMarkSize="20dp"
        app:ctvQuarterNumberColor="#08ff00"
        app:ctvQuarterNumberTextSize="16sp"
        app:ctvShowLaps="true"
        app:ctvTimeFormat="seconds_minutes"
        app:ctvTimeMode="manual"
        app:ctvTimeNumberColor="#00d0ff"
        app:ctvTimeNumbersTextSize="55sp"
        />
```
You can also set all attributes programmatically:
```java
private void changeAppearance() {
        circleTimeView.setHighlightMarkLineColor(Color.GREEN);
        circleTimeView.setCircleButtonColor(Color.GREEN);
        circleTimeView.setCircleButtonPressedColor(Color.GREEN);
        circleTimeView.setCircleColor(Color.CYAN);
        circleTimeView.setCircleStrokeWidth(10);
        circleTimeView.setCircleButtonRadius(20);
        circleTimeView.setInnerRadius(370);
        circleTimeView.setLabelText(R.string.app_name);
        circleTimeView.setLapBackgroundColor(Color.CYAN);
        circleTimeView.setLabelTextSize(28);
        circleTimeView.setLapLabelTextColor(Color.BLACK);
        circleTimeView.setLapLabelMarginTop(12);
        circleTimeView.setLapLabelTextSize(20);
        circleTimeView.setMarkLineColor(Color.CYAN);
        circleTimeView.setMarginTopLabel(10);
        circleTimeView.setMinuteMarkCount(60);
        circleTimeView.setMarkLineWidth(3);
        circleTimeView.setOuterRadius(400);
        circleTimeView.setMarkSize(40);
        circleTimeView.setPaddingInnerRadius(20);
        circleTimeView.setQuarterMarkSize(80);
        circleTimeView.setPaddingQuarterNumber(85);
        circleTimeView.setQuarterNumberColor(Color.GREEN);
        circleTimeView.setLabelTextColor(Color.GREEN);
        circleTimeView.setTimeNumberColor(Color.CYAN);
        circleTimeView.setQuarterNumbersTextSize(35);
        circleTimeView.setTimeNumbersTextSize(120);
    }
```

In case of using the lap label you should provide your own implementation of the **LapDataProvider** interface. For example:

```java
    circleTimeView.setLapLabelDataProvider(new CircleTimeView.LapDataProvider() {
            @Override
            public String getLapLabelText(long currentTimeInSeconds) {
                // Count only hours
                return String.valueOf(currentTimeInSeconds % 3600);
            }
        });
```
You can also listen to timer and time events. You need to implement **CircleTimerListener** and **CircleTimeListener** respectively.

```java
circleTimeView.setCircleTimeListener(new CircleTimeView.CircleTimeListener() {
            @Override
            public void onTimeManuallySet(long time) {
                Log.d("TIME LISTENER", "onTimeManuallySet " + time);
            }

            @Override
            public void onTimeManuallyChanged(long time) {
                Log.d("TIME LISTENER", "onTimeManuallyChanged " + time);
            }

            @Override
            public void onTimeUpdated(long time) {
                Log.d("TIME LISTENER", "onTimeUpdated " + time);
            }
        });
        circleTimeView.setCircleTimerListener(new CircleTimeView.CircleTimerListener() {
            @Override
            public void onTimerStop() {
                Log.d("TIMER LISTENER", "onTimerStop ");

            }
            @Override
            public void onTimerStart(long time) {
                Log.d("TIMER LISTENER", "onTimerStart " + time);
            }

            @Override
            public void onTimerTimeValueChanged(long time) {
                Log.d("TIMER LISTENER", "onTimerTimeValueChanged " + time);
            }
        });
```


### Attributes

| Attribute                                                          | Description
| --------------------------------------------------------------- | -----------
| ctvCircleColor         | Color of the circle line (circle face line)
| ctvCircleStrokeWidth    | Width of the circle line
| ctvCircleButtonColor             | Color of the hand circle button (movable hand)
| ctvCircleHandButtonRadius    | Radius of the hand circle button
| ctvCirclePressedButtonColor    | Color of the hand circle button in the pressed state (Manual setup)
| ctvCurrentTimeInSeconds    | Preset time in secodns (the initial time to be displayed)
| ctvHighlightLineColor    | Color of the highlighted mark line (minute mark by default)
| ctvMarkLineColor    | Color of the marks (minute by default)
| ctvMarkLineWidth    | Width of the mark line
| ctvLabelText    | Text for the main label (in the example it is "The clock is ticking")
| ctvMarginTopLabel    | Top Margin of the main label
| ctvLabelTextColor    | Text color of the main label
| ctvLabelTextSize    | Text size of the main label
| ctvLapLabelTextSize    | Text size of the lap label
| ctvShowLaps    | Show the laps label
| ctvLapTextColor    | Text color of the lap label
| ctvLapBackgroundColor    | Background color of the lap label (sometimes used for displaying days or counting laps in case of stopwatch)
| ctvLapLabelMarginTop    | Top Margin of the lap label
| ctvMarkSize    | Size of a single mark (height/width)
| ctvMinutesMarkCount    | Quantity of marks, for example set this attribute to 60 to have a mark/second
| ctvMultiLapRotation    | Allow to pass multiple lap/revolution
| ctvPaddingInnerRadius    | Padding for inner radius
| ctvPaddingQuarterNumber    | Padding for the quarter numbers (15,30,45,60)
| ctvQuarterMarkSize    | Size of the quarter number mark (15,30,45,60)
| ctvQuarterNumberColor    | Color of the quarter numbers
| ctvQuarterNumberTextSize    | Text size of the quarter numbers
| ctvTimeFormat    | Time format. Allowed values are **seconds_minutes** or **minutes_hours**. Depending on this attribute the time will be displaye in specific format. For instance, **seconds_minutes** first two numbers before colon will be used for minutes and two numbers after for seconds. Time will be converted according the selected format
| ctvTimeMode    | Time mode. There are three possible options. **Normal** manual set up is not allowed. **Manual** manual set up is allowed, this mode could be used for getting time from a user. **Timer** the same as the **Normal**.
| ctvTimeNumberColor    | Color of the main time numbers
| ctvTimeNumbersTextSize    | Text size of the main time numbers


# Thanks

- [jiahuanyu](https://github.com/jiahuanyu/CircleTimerView) For the idea and some math

## License

```
Copyright 2017 CROSP

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

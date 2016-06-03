# ClockPie
Clock divided into parts looking like pie chart. That's the reason for the name, looks like pie chart, but it's for clock.

## Description and use

There was a request for visualizing the division of day into few parts where there are different prices for service during the day. While the clock animation runs, for each part of day the price at that part of the day shhould be displayed. And also there are 2 clocks, one for AM, one for PM, so as the first one's animation ends, the second one's starts. There are 2 listeners for this - one that is activated when part of day changes, another one when the animation ends.

In my case I needed to use only hours -> 12 parts, but it's easy to be modified to use also minutes (60 parts) or even seconds (360 parts).

## Preview

![clear clock](https://github.com/lukash-lha/clockpie-android/blob/master/Screenshots/Screenshot_2.png)
![screen 1](https://github.com/lukash-lha/clockpie-android/blob/master/Screenshots/Screenshot_3.png)
![screen 1](https://github.com/lukash-lha/clockpie-android/blob/master/Screenshots/Screenshot_4.png)
![screen 1](https://github.com/lukash-lha/clockpie-android/blob/master/Screenshots/Screenshot_5.png)
![screen 2](https://github.com/lukash-lha/clockpie-android/blob/master/Screenshots/Screenshot_6.png)
![full clock](https://github.com/lukash-lha/clockpie-android/blob/master/Screenshots/Screenshot_7.png)

## Implementation

There is one transparent picture on top with the clock (numbers, dial) and one one view below. It has the same size as the image and it is implemented as
- ClockPie - which is static and can draw the clock divided into parts (pies)
- AnimatedClockPie - extension of the static class that is able to animate the division

### The ImageView

This is very important part of the visualisation as it shows the actual clock - the dial (numbers). In the first version I put this under the animation of circle parts and used transparent colors for the animation. But it didn't look very well as the color of dial was changed. So I moved it to the top. In this case it's important to make the image transparent, so the animation in background can be seen. Obviously. 
There are images included in the example. Those are made for the specific example. But I included  also the original source of the images (clock_transparent_1200x1200.pdn) - it's in resolution 1200 x 1200 pixels, that should be enough for any other use you can need. Also you can change the "title" in the clock (like I used my lha.dev). You can open and edit it in Paint.net (maybe also some other, but natively it's for this one), it's free software.

### The View



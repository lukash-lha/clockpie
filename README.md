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

### The View



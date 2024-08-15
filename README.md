This is a simple app to demonstrate an animation bug.

The bug seems to be caused by `tween` not respecting the animation duration for a few seconds after the reduced/removed animations setting is toggled off; all we're doing is telling `tween` where the end position is and how long it should take to get there, yet based on the log statements, the starting and ending scroll points are correct while the actual animation duration is ~10ms instead of the expected 5000:

```
2024-08-14 15:01:20.794  START scrollState.value: 393  rightPosition: 393  leftPosition: 131  animationDuration: 5000
2024-08-14 15:01:20.806  END scrollState.value: 131  rightPosition: 393  leftPosition: 131  animationDuration: 5000
2024-08-14 15:01:20.806  scroll finished, photo index: 0
```

Although this is a bit of an edge case, it's a poor user experience. Please let me know if there's any additional information I could provide to help with fixing it!

## Repro steps

1. Open the Settings app. Enable reduced/removed animations.
2. Open the sample app. Click the "Show me dogs" button.
3. Swipe through the images a few times.
4. Return to the Settings app. Disable reduced/removed animations.
5. Return to the sample app. Hit the back button to return to the main screen.
6. Click the "Show me dogs" button again. Observe the images are now scrolling by rapidly, although they go back to normal after a few seconds.

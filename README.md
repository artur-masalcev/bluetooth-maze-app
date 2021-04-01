# App for "Bluetooth maze" project
This app is made as a part of my school project. The app controls a robotic toy labyrinth by sending signals to Bluetooth module connected to Arduino.

More details and video: https://drive.google.com/drive/folders/1IZUI9pRBLh7USqu8pj6IVL6xD14iVned?usp=sharing

The project itself was inspired by Alex Gyver Bluetooth labyrinth https://www.youtube.com/watch?v=j5V1ooMSuUU. This application was made using Android Studio (not Thunkable IDE as in the Alex version). But it still should be compatipable with the Alex's version of the labyrinth.

The application is translated in Russian language as been demanded at my school.

## Functions
The application has two control modes and a setting screen.

![Screenshot_1](https://user-images.githubusercontent.com/76208684/112892023-7f236700-90e1-11eb-82f9-4ac28f441597.png)

### Manual(joystick) mode
This mode allows user to control the construction with virtual joystick.

### Accelerometer mode
This mode synchronizes the position of the smartphone and the labyrinth in real time.

![Screenshot_2](https://user-images.githubusercontent.com/76208684/112892093-93fffa80-90e1-11eb-9801-999a4a37845d.png)

### Setting screen
The setting screen allows user to modify level of sensitivity in both control modes.

![Screemshot_3](https://user-images.githubusercontent.com/76208684/112892156-a5490700-90e1-11eb-8d8d-4616ab884dce.png)

## Interface
On the main screen there are three main components: timer, joystick (which is not available in accelerometer mode) and connection indicator.

### Timer
The first window of the timer shows the current game time. It nullifies when ball is touching the start position of the labyrinth and stops when it touches the finish position. The second window represents the best time.

![Screenshot_4](https://user-images.githubusercontent.com/76208684/112892370-e4775800-90e1-11eb-9c6e-05de641824db.png)

### Connection indicator
Connection indicator represents the state and stability of the connection. The indicator can be in three colors. 

![Screenshot_5](https://user-images.githubusercontent.com/76208684/112892272-ca3d7a00-90e1-11eb-9f05-c123bcdb85e3.png)

**red** - device is not connected to the labyrinth.

**yellow** - the connection is bad, or smartphone is currently connecting to the construction.

**green** - the connection is stable and both labyrinth and smartphone are paired.

## License
Project itself is inspired by Alex Gyver: https://www.youtube.com/channel/UCgtAOyEQdAyjvm9ATCi_Aig

You may freely use the source code of this app and modify it.

# Custom Chatroom with Google-Dialogflow

## Introduction
This project is based on Google Dialogflow to create an Artificial Intelligence Helper in Android App. That is, it is not built on any platform recommended by Dialogflow.

## Requirement
* Google Dialogflow
* Android Studio

## Procedure
1. Create a project on Google Dialogflow
2. Customize your own ChatRoom in Android Studio
3. Connection between API.api and your chatroom interface.
    - Input: User
    - Output: Bot 
4. Use your own data to have different application

#### Problem Solving
* Q: Text response can not add hyperlink 
    * A: Use **Linkify** and remember to add **android:autoLink** in layout attribute (or html tag)
* Q: How to customize picture reponse? 
    * A: Use SpannableStringBuilder to add Imagespan into Textview

## Contact
Feel free to [contact me](jyunyan.lu@gmail.com) if there's any problem.

## Links

Reference from [Android Developer Guide](https://developer.android.com/guide/) , including [Dialogflow](https://dialogflow.com/)

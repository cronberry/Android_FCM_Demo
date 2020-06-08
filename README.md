# Cronberry - Android Firebase Push Notification Handler

Sample code for Google firebase cloud messaging integration with Cronberry.

The application contains sample code for handling firebase push notifications sent from cronberry in any android application and 
suport/demostrate below features :--

1. Support All Android Version 4.1 + (API Level 16)

2. Handles Notification title, body, image and action URL in all below states of the host application.
  
    a. App in ForeGround.
  
    b. App in BackGround.
  
    c. App Not open and not in background.
  
3. The field ActionUrl is used for either website link routing (sent in data payload body as actionUrl) or for calling some activity of your android app (sent in notification body as click_to_action).
  
    * For activity routing : write your android activity name.
    * For web/url routing: write complete url with http/https prefix.
  
Note :
1. For image display, Device must be on Android 4.1+ (API Level 16)
2. Android images require an aspect ratio of 2:1 (consider trying sizes 512x256, 960x480, or 1024x512). 





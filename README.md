# Business App Sample Application

This is an open-source project to display how you can create your own custom Business App
application within a few minutes. I have already created an Business
App [click to see Sample AppUi on your browser](https://app.formaloo.net/fDZRGCu8wLSsc5d).

<img src="images/BusinessWebApp.png" width="500" >

You can
also [download](???)
and install the demo application.

<table>
  <tr>
<td align="center"><img src="images/home.png"  ></td>
<td align="center"><img src="images/menu.png"  ></td>
<td align="center"><img src="images/productdetail.png" ></td>
  </tr>
  <tr>
<td align="center"><img src="images/drawer.png" ></td>
<td align="center"><img src="images/restaurant.png"></td>
<td align="center"><img src="images/career.png"></td>
    </tr>
</table>

Clone this repository and import it into **Android Studio**

git clone https://github.com/formaloo/formaloo-android-business-app.git

First of all, we need X-API-KEY.

You can find your X-API-KEY on your [CDP Dashboard](https://cdp.formaloo.net/) and create a
Connection to get your API_KEY.

<img src="images/x-api-key.png">

Now you just need one more key: The appUI address code. Copy the code at the end of the URL. For
instance in this URL  https://app.formaloo.net/fDZRGCu8wLSsc5d the appUI address code is
fDZRGCu8wLSsc5d.

Copy these two keys and paste them in the api.propertise.txt file inside your android project:

<img src="images/api-properties-file.png" width="500" >

Now Define build configuration fields in the Gradle file. These constants will be accessible at
runtime as static fields of the BuildConfig class:

<img src="images/api-propertis-gradle.png" width="500" >

Your Application is almost ready.

You just need some customization like colors, app name, etc. If you are new to android you can use
following steps to customize your project:

# Theming

Find the feature/home module. under ui folder you can see theme folder It contains the following
classes.

Color.kt - for custom colors

Shape.kt - for custom shapes

Type.kt - for custom typography

Theme.kt - for custom themes

You can define your own Colors and Fonts, Material theme function inside Theme file update the
entire app theme:

MaterialTheme(
colors = …, typography = …, shapes = …
)

# Application id

The last item you need to change to have your own application is the package name and application
id, follow the images to rename the ids:

1. <img src="images/package-name.png" height="400" >
2. <img src="images/app-id.png" height="400" >

We are done, our Business App is ready :)
Run the project and test the application.



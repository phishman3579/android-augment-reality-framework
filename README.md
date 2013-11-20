android-augment-reality-framework
=================================

A framework for creating augmented reality Apps on Android

## Introduction

All the pieces needed to create an augmented reality App on Android.

* Created by Justin Wetherell
* Google:   http://code.google.com/p/android-augment-reality-framework
* Github:   http://github.com/phishman3579/android-augment-reality-framework
* LinkedIn: http://www.linkedin.com/in/phishman3579
* E-mail:   phishman3579@gmail.com
* Twitter:  http://twitter.com/phishman3579

## Details

This will walk you through creating your own augmented reality Android App using this framework.

All you have to do to display your own data in this augmented reality App is:

Extend the DataSource class to support getting information for your data.

Extend the AugmentedReality class to get the data and add it to the App. Or you could just use the Demo class and add your new data source to the existing data structure.

That's it...

In the source code I have also created an example that follows the strategy above. The files are:

* Demo.java

* TwitterDataSource.java

* WikipediaDataSource.java

* BuzzDataSource.java

* LocalDataSource.java

You can use them as a reference to create your own augmented reality App based on this framework.


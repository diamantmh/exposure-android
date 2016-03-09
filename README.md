# Exposure

Exposure is an Android application that helps you find and tell other people about nearby photo locations. Exposure allows you to view photo locations in different forms. From a quick glance in a map view to a detailed list view, Exposure helps you either spontaneously or methodically choose your next photo destination.

You can find user documentation [here](http://getexposure.github.io/product/index.html), and developer documentation below.

Additionally, you can view up-to-date [Software Design Specifications](https://docs.google.com/document/d/18sWdtOYFfbUSAQKPTqaiaJ1Nb6MXersM-5ZnIk4cGmY/edit?usp=sharing) and [Software Requirements Specifications](https://docs.google.com/document/d/1nNvZxgk5k2VMcxixJDl_mAD0NAWXg_9bimPEH4NHPy4/edit?usp=sharing).

## User Documentation

Download the Exposure APK file [here](https://github.com/getexposure/exposure-apk/raw/master/exposure.apk).
Download the Exposure signed final APK file [here] (https://github.com/getexposure/exposure-apk/raw/master/exposure-final-release.apk).

If you need to find more information on how to use the application or install the application as a user, you can find that document [here](https://docs.google.com/document/d/1ZT6u75tTV0oWnKEOdW2WJdhG2FlktyoXmdl4aUMio_M/edit?usp=sharing).

## Developer Documentation

### Initial Install

Exposure is hosted on GitHub and requires Android Studio, a Java Development Environment, an Android SDK, and an Android Virtual Device.

- If you don’t have git, install git following the instructions [here](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git).
- If you don’t have Android Studio, download the latest version [here](http://developer.android.com/sdk/index.html).
- If you don’t have the Java Development Kit (JDK) 8, download it [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).
- If you don’t have Android OS API Level 23, download it following the instructions [here](http://developer.android.com/sdk/installing/adding-packages.html).
- If you don’t have an Android Virtual Device, create a Nexus 5 AVD following the instructions [here](http://developer.android.com/tools/devices/managing-avds.html).

### Obtain

To obtain Exposure’s source code, download the Exposure ZIP archive [here](https://github.com/getexposure/exposure-android/archive/master.zip). Alternatively, to obtain Exposure’s source code, clone the Exposure repository [here](https://github.com/getexposure/exposure-android).

To clone, follow the git instructions [here](https://git-scm.com/book/en/v2/Git-Basics-Getting-a-Git-Repository#Cloning-an-Existing-Repository).

### Directory Structure

Under the exposure-android folder, our directory is divided into the app directory and web directory. The app directory contains all the necessary documents to build and run the Exposure mobile Android application. The web directory contains all the necessary documents to build and run the RESTfulWebService. The app and web directories both have their individual src directories, which have main and sub directories inside. Inside each main and sub directories, there are directories for each language (java). Inside each language directory, there is a directory for each project. Lastly, each project directory contains all the source code for the specific project for the specific language.

### Build

Once you have Exposure’s source code, follow these instructions in Android Studio to begin running the app:

- Open Android Studio
- Select “Open Existing Android Studio Project”
- Select the unzipped `exposure-android` directory
- Click "Ok" if it prompts to use a found .gradle file
- Replace your `debug.keystore` file located at `~/.android/debug.keystore` with Exposure's `debug.keystore` [here](https://drive.google.com/drive/u/0/folders/0B2oTf4T_FgbITHYtYnl0SmF0UEE)
- Ensure "ADB Integration" is checked (Tools -> Android -> ADB Integration)
	- Occasionally Android Studio will not recognize the launched emulator to launch the app on.  If that happens, simply run again and select the currently running emulator.
- Once Gradle finishes building, click the green play button to run the app
- Select the “Nexus 5 API 23” Android Virtual Device
- Voila, Exposure should start up in the Android Emulator with a map view

## Run

The app functions are rather intuitive, but there are a few reminders for the Maps:

- To activate GPS for the emulator:
	- Launch the emulator standalone, or through the run process
	- Open the terminal in Android Studio, next to the Android Monitor tab
	- Type: "telnet localhost <emulator id>" where <emulator id> is 5554 by default
		-If telnet is not recognized, you may need to turn the feature on.  For Windows users: [fix](http://stackoverflow.com/questions/25031090/telnet-is-not-recognized-as-internal-or-external-command)
	- Type: "geo fix (longitude) (latitude)"
	- (notice that it's longitude first, and backspace does not work correctly once connected to the emulator terminal)
- The "Loc" button will center the map at your most recent location, if available
- The "Apply Filter" button will load all pins that can be visible on the current orientation of the map
	- Available sample locations:
		- Pins around default location--UW Campus (search "UW" or "University of Washington")
		- Select from a variety of categories, including "All"/"NONE" and click "Apply Filter"
		- Click pin and then pin's info window to load location/photo info.
- Loading the pictures and locations may take up to 30s to load, be patient.


### Design Patterns

Exposure uses two design patterns. First, it uses a Factory pattern. Within MapsActivity, `onMapReady()` uses a CameraUpdateFactory to get a CameraUpdate object with the correct properties (e.g. center position and zoom levels) to initialize a Google Maps Fragment.

Another design pattern Exposure uses is the Builder pattern. With many of the model classes such as Location, they build a mutable object, thus allowing information to be added to the model. However, once finished building, the Location model is an immutable object.

### Test

To test the Exposure DatabaseManager, we implemented several unit tests to assure that the manager was correctly sending and receiving the expected information to and from the RESTful web application. Standard valid input was prepared and used to create http requests. These http requests were sent to the RESTful web application. If a valid expected object were returned, the test would pass, and would fail otherwise. If we send an invalid request, the test would pass if we received the expected response.

In order to assure the RESTful web application was correctly operating, several things were needed to be done. First, the database needed to be pre-populated with known data. Second, http requests were generated. To test that the application was correctly implemented, we manually created http requests and sent it to the address the application was currently running using google chrome. For valid http requests, we expected google chrome to return a valid tuple of elements that were known to be stored on the database. For invalid http requests, we expected to get an appropriate error from google chrome. We also placed numerous assert statements that would be run if the DEBUG flag was set to be true.

#### DatabaseManager Tests

The DatabaseManager has tests in place to test the following functions:

- Creating new Users, Locations, Photos, and Comments sends data to the database properly
- Querying Users, Locations, Photos, and Comments returns the proper data
- Inserting duplicate Users, Locations, Photos, and Comments doesn't work
- Removing existing Users, Locations, Photos, and Comments removes proper data
- Removing nonexisting Users, Locations, Photos, and Comments doesn't remove any data
- Querying multiple Location Photos returns all the proper data

### Setup Automated Daily Builds

For automated daily builds and test runs, we’re using Travis CI, an integrated GitHub test and deployment service. You can find the build reports [here](https://travis-ci.org/getexposure/exposure-android). To setup the build system:

- Ensure Travis CI is authorized on the GitHub account and repository
- Push a commit to the repository and Travis will automatically build and run tests

Travis is integrated with the “getexposure” organization on GitHub such that any emails from the integration service will go out to the entire team.

### Release

To organize releases, we will use GitHub's release feature.

The repository will also be ZIP compressed and uploaded to the team website such that each version can be viewed.

### Access Bug Reporting

You can find our bug reporting system on GitHub issues [here](https://github.com/getexposure/exposure-android/issues).

### Bug Reporting

We will handle bug reporting using GitHub’s built-in issues feature. The list of current bugs will be the list of open issues here, the issues section of the release version of Exposure. New bugs can be added in as an open issue, and issues will be closed once their corresponding bugs are resolved.

The procedure for reporting a bug will be detailed to make finding the source of the issue as easy as possible. The finding user will describe in detail what they were doing at the time the bug occurred, what the undesired behavior was (screen freeze, long wait, app crash, etc.), and will include any error information (stack traces, exception info, etc.) if possible.

Developers on our team will be responsible for reviewing the list of open bugs periodically, and those that pertain to the modules in the app that they are responsible for (i.e. Google Maps API, Facebook SDK, SQL backend, etc.) will fix the bug in a timely manner and push up the modified code accordingly after rebuilding and testing the fix.

# Releases

## Base Feature Release (1.0)

Features not yet implemented:

- List view

Features partially implemented:

- Filters on the map view doesn't yet work, and "Apply Filters" only adds dummy locations

Functional features include:

- Users, Photos, Locations, Comments, and Categories can be created, added, and queried from the database via a web service.
- Locations can be viewed from both a map.
- Users may log in with Facebook and view their profile.

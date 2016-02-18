# Developer Documentation

## Initial Install

Exposure is hosted on GitHub and requires Android Studio, a Java Development Environment, an Android SDK, and an Android Virtual Device.

- If you don’t have git, install git following the instructions [here](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git).
- If you don’t have Android Studio, download the latest version [here](http://developer.android.com/sdk/index.html).
- If you don’t have the Java Development Kit (JDK) 8, download it [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).
- If you don’t have Android OS API Level 23, download it following the instructions [here](http://developer.android.com/sdk/installing/adding-packages.html).
- If you don’t have an Android Virtual Device, create a Nexus 5 AVD following the instructions [here](http://developer.android.com/tools/devices/managing-avds.html).

## Obtain

To obtain Exposure’s source code, download the Exposure ZIP archive [here](https://github.com/getexposure/exposure-android/archive/master.zip). Alternatively, to obtain Exposure’s source code, clone the Exposure repository [here](https://github.com/getexposure/exposure-android).

To clone, follow the git instructions [here](https://git-scm.com/book/en/v2/Git-Basics-Getting-a-Git-Repository#Cloning-an-Existing-Repository).

## Directory Structure

Under the exposure-android folder, our directory is divided into the app directory and web directory. The app directory contains all the necessary documents to build and run the Exposure mobile Android application. The web directory contains all the necessary documents to build and run the RESTfulWebService. The app and web directories both have their individual src directories, which have main and sub directories inside. Inside each main and sub directories, there are directories for each language (java). Inside each language directory, there is a directory for each project. Lastly, each project directory contains all the source code for the specific project for the specific language.

## Build

Once you have Exposure’s source code, follow these instructions in Android Studio to begin running the app:

- Open Android Studio
- Select “Open Existing Android Studio Project”
- Select the unzipped `exposure-android` directory
- Start the database web service. To do so...
-- Open Terminal
-- Navigate to “/exposure-android/web/”
-- Run the “run” script
--- `./run`
- Once Gradle finishes building, click the green play button to run the app
- Select the “Nexus 5 API 23” Android Virtual Device
- Voila, Exposure should start up in the Android Emulator with a map view

## Test

To test the Exposure DatabaseManager, we implemented several unit tests to assure that the manager was correctly sending and receiving the expected information to and from the RESTful web application. Standard valid input was prepared and used to create http requests. These http requests were sent to the RESTful web application. If a valid expected object were returned, the test would pass, and would fail otherwise. If we send an invalid request, the test would pass if we received the expected response.

In order to assure the RESTful web application was correctly operating, several things were needed to be done. First, the database needed to be pre-populated with known data. Second, http requests were generated. To test that the application was correctly implemented, we manually created http requests and sent it to the address the application was currently running using google chrome. For valid http requests, we expected google chrome to return a valid tuple of elements that were known to be stored on the database. For invalid http requests, we expected to get an appropriate error from google chrome. We also placed numerous assert statements that would be run if the DEBUG flag was set to be true.

## Setup Automated Daily Builds

For automated daily builds and test runs, we’re using Travis CI, a integrated GitHub test and deployment service. To setup the build system:

Ensure Travis CI is authorized on the GitHub account and repository
Add `.travis.yml` file to the repository
Push the new Travis file up to the repository to trigger the build
Travis CI triggers builds with any git push as long as there is a `.travis.yml` file within the repository. As Travis is integrated with the “getexposure” organization on GitHub, any emails from the integration service will go out to the entire team.

However, as our application currently stands, we are simply passing information to and from the database. These are more integration tests focused and don’t require unit tests. These integration tests have yet to be planned and will be integrated with Travis CI. Additionally, the main test suite we have created primarily focus on assertions to ensure and check functionality while debugging. These assertions will be integrated into Travis CI as well.

## Release

When a version of Exposure is ready to go (i.e. stable), tags will be used to mark different versions of Exposure and pushed up to the GitHub repository. The repository will also be ZIP compressed and uploaded to the team website such that each version can be viewed.

## Access Bug Reporting

You can find our bug reporting system on GitHub issues [here](https://github.com/getexposure/exposure-android/issues).

## Bug Reporting

We will handle bug reporting using GitHub’s built-in issues feature. The list of current bugs will be the list of open issues here, the issues section of the release version of Exposure. New bugs can be added in as an open issue, and issues will be closed once their corresponding bugs are resolved.

The procedure for reporting a bug will be detailed to make finding the source of the issue as easy as possible. The finding user will describe in detail what they were doing at the time the bug occurred, what the undesired behavior was (screen freeze, long wait, app crash, etc.), and will include any error information (stack traces, exception info, etc.) if possible.

Developers on our team will be responsible for reviewing the list of open bugs periodically, and those that pertain to the modules in the app that they are responsible for (i.e. Google Maps API, Facebook SDK, SQL backend, etc.) will fix the bug in a timely manner and push up the modified code accordingly after rebuilding and testing the fix.
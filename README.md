# MySQL Java 

This is a simple Servlet-based Java webapp that uses MySQL. 

## Deploy to HP Helion
<a href="https://deploynow.hpcloud.com/?repoUrl=https://github.com/HelionDevPlatform/helion-mysql-java">
![Helion  Logo](https://a248.e.akamai.net/cdn.hpcloudsvc.com/g0bc199ab57e65f093a48d069effc0c3b/prodae1//button.png?id=6)
</a>

## Prerequisites
- If you do not have an HP Helion Development Platform Application Lifecycle 
  Services Cluster available, please create one before continuing. You will also
  need to install the Helion CLI. 
- Make sure that the MySQL service is enabled. It is not enabled by default.    
  You can take the following steps to enable it:
    - Go to the Management Console (e.g. https://api.example.com)
    - Admin --> Cluster --> Settings (gear icon on right corner) --> Check off 
      MySQL --> Save
      
## Building the Application

The specific commands listed below are intended for Unix-like machines.

Build the application with Maven. Maven requires you to have the Java Development
Kit (JDK) installed. The JDK can be installed with the following command:

    sudo apt-get install default-jdk

### Maven

Make sure you have [Maven](http://maven.apache.org/ "Maven") installed.
The simplest way to install Maven is:

    sudo apt-get install maven

Then, *cd* into this app's root directory (directory with manifest.yml) and execute:

    mvn clean package

This will create the .war file within the 'target' directory. The pom.xml file 
in the root directory is used by Maven to build the application.

## Deploy the Application

Execute the following commands:

- Open the terminal
- If you are not already there, *cd* to the root directory of the sample. The 
  root directory contains the manifest.yml file which helps automate deployment. 
- If you have not logged in to your target environment yet, execute the following:

    `helion target https://api.example.com`
    
    `helion login`
    
    Enter your Management Console credentials
    
    `helion push -n`

## View and run the app
- Go to the management console (e.g. https://api.example.com)
- Check the applications link to see a list of your apps.
- Click on the name of the app you just deployed. The app name is specified in 
  manifest.yml.
- Click "View App" to see your app in action.

The result when visiting the application page and clicking 'View App' should be
a page showing some text after your app has connected to MySQL and executed a 
query.

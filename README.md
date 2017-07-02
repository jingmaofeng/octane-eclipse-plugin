# octane-eclipse-plugin
## Eclipse IDE Integration for ALM Octane

The plugin shares a common service layer with: https://github.com/HPSoftware/octane-intellij-plugin <br>
Common: https://github.com/HPSoftware/octane-plugin-common <br>

Connection to the server is done using the REST SDK for ALM Octane, see: <br>
* https://github.com/HPSoftware/sdk-extension
* https://github.com/HPSoftware/ALMOctaneJavaRESTSDK

### How to build:
The plugin is build using mvn using tycho.

Go to the project root folder and run: 
```
mvn clean install
```

### How to debug: 
Import the project into eclipse, you'll need a verison of Eclipse for RCP and RAP Developers.
The maven module: octane-eclipse-plugin should be recogized as an eclipse plugin project.

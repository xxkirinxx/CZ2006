CZ2003 Project NozzieMozzie App uses mail api to send emails from a gmail account to fulfil functional requirement of resetting password.
As such, it is a requirement that before building the application, external jar files are added as dependencies. 
The required jar files can be found in the same directory as this read me file. The 3 jar files are:

mail.jar
activation.jar
additonnal.jar

Instructions to add external jars using Andriod Studio as follows:

1. In Andriod Studio, press File
2. Press Project Structure
3. Under Dependencies, go to app and click "+" button to add jar dependencies
4. Copy directory of where the jar files are e.g. "C:\Users\KJY\Documents\GitHub\CZ2006\mail.jar"
5. Set configuration to "implementation" and click OK

Once all 3 jar files are added, building of app should proceed without error regarding jar dependencies. 
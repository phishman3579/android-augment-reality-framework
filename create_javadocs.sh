#1/bin/bash

javadoc -classpath /usr/local/android-sdk-linux_86/platforms/android-4/android.jar -d javadoc/ -sourcepath `find . -name *.java` -verbose com.jwetherell.augmented_reality

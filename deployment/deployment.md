# How to deploy to the nexus repository manager

The nexus repository manager only accepts lower case character in the module name and in the version
number.

We deploy the following files:

- fastdoubleparser/target/fastdoubleparser-x.y.z.jar
- fastdoubleparser/target/fastdoubleparser-x.y.z-sources.jar
- fastdoubleparser-java9/target/fastdoubleparser-java19-x.y.z-sources.jar


All files must be signed with GPG. We create a bundle.jar file, which we then
can upload to the nexus repository manager.

```shell
cp ../fastdoubleparser/target/*.jar .
cp ../fastdoubleparser-java19/target/*javadoc.jar fastdoubleparser-0.8.0-javadoc.jar 
rm *.asc
for f in *.jar; do gpg -ab "$f"; done
for f in *.xml; do gpg -ab "$f"; done
rm *bundle.jar
jar -cf fastdoubleparser-0.8.0-bundle.jar $(ls -1 pom*|xargs) $(ls -1 fastdoubleparser*|xargs)
```

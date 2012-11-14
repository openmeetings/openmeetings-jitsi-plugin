set JAVA_HOME=C:\Program Files\Java\jdk1.7.0_05
#set SC_BUNDLES=%ProgramFiles(x86)%\Jitsi\sc-bundles
set SC_BUNDLES=c:\Program Files\Jitsi\sc-bundles
set LOC=C:\Users\admin\jitsi

"%JAVA_HOME%"\bin\javac -classpath "%SC_BUNDLES%\defaultresources.jar;%SC_BUNDLES%\protocol-jabber.jar;%SC_BUNDLES%\sipaccregwizz.jar;%SC_BUNDLES%\..\lib\jdic-all.jar;%SC_BUNDLES%\..\lib\felix.jar;%SC_BUNDLES%\netaddr.jar;%SC_BUNDLES%\configuration.jar;%SC_BUNDLES%\swing-ui.jar;%SC_BUNDLES%\msnaccregwizz.jar;%SC_BUNDLES%\util.jar;%SC_BUNDLES%\protocol.jar;%SC_BUNDLES%\ui-service.jar;%SC_BUNDLES%\resourcemanager.jar;%SC_BUNDLES%\contactlist.jar;%SC_BUNDLES%\otr.jar" -d . -target 1.6  -source 1.6 "%LOC%"\src\net\java\sip\communicator\plugin\openmeetings\*.java

"%JAVA_HOME%"\bin\jar -0vcfm openmeetings.jar "%LOC%"\src\net\java\sip\communicator\plugin\openmeetings\openmeetingsplugin.manifest.mf net
rem copy openmeetings.jar "%SC_BUNDLES%"\
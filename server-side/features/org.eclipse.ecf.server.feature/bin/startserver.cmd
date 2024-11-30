echo off
setlocal
cd %~dp0
set RP=..\..\..\plugins

rem Relative path for accessing core ECF plugins
set CALL=%RP%\org.eclipse.ecf.call_0.9.4.jar
set DS=%RP%\org.eclipse.ecf.datashare_0.9.4.jar
set DISCOVERY=%RP%\org.eclipse.ecf.discovery_0.9.4.jar
set DOC=%RP%\org.eclipse.ecf.doc_0.9.4.jar
set FT=%RP%\org.eclipse.ecf.filetransfer_0.9.4.jar
set IDENTITY=%RP%\org.eclipse.ecf.identity_0.9.4.jar
set PRESENCE=%RP%\org.eclipse.ecf.presence_0.9.4.jar
set DSP=%RP%\org.eclipse.ecf.provider.datashare_0.9.4.jar
set FTP=%RP%\org.eclipse.ecf.provider.filetransfer_0.9.4.jar
set IRCP=%RP%\org.eclipse.ecf.provider.irc_0.9.4.jar
set JMDNS=%RP%\org.eclipse.ecf.provider.jmdns_0.9.4.jar
set RSP=%RP%\org.eclipse.ecf.provider.remoteservice_0.9.4.jar
set UIP=%RP%\org.eclipse.ecf.provider.ui_0.9.4.jar
set XMPPP=%RP%\org.eclipse.ecf.provider.xmpp_0.9.4.jar
set PROVIDER=%RP%\org.eclipse.ecf.provider_0.9.4.jar
set RS=%RP%\org.eclipse.ecf.remoteservice_0.9.4.jar
set SERVER=%RP%\org.eclipse.ecf.server_0.9.4.jar
set SO=%RP%\org.eclipse.ecf.sharedobject_0.9.4.jar
set UI=%RP%\org.eclipse.ecf.ui_0.9.4.jar
set ECF=%RP%\org.eclipse.ecf_0.9.4.jar
set SMACK=%RP%\org.jivesoftware.smack_2.2.0.jar
rem Now all together
set CORE="%CALL%;%DS%;%DISCOVERY%;%DOC%;%FT%;%IDENTITY%;%PRESENCE%;%DSP%;%FTP%;%IRCP%;%JMDNS%;%RSP%;%UIP%;%XMPPP%;%PROVIDER%;%RS%;%SERVER%;%SO%;%UI%;%ECF%;%SMACK%"

rem examples plugins
set CLIENTS=%RP%\org.eclipse.ecf.example.clients_0.9.4.jar
set CED=%RP%\org.eclipse.ecf.example.collab.editor_0.9.4.jar
set COLLAB=%RP%\org.eclipse.ecf.example.collab_0.9.4.jar
rem now all together
set EXAMPLES="%CLIENTS%;%CED%;%COLLAB%"
rem Eclipse 3.2 runtime classes
set RUNTIME="..\lib\org.eclipse.equinox.registry_3.2.100.v20061023.jar;..\lib\org.eclipse.core.runtime_3.2.100.v20061030.jar;..\lib\org.eclipse.equinox.common_3.3.0.v20061023.jar;..\lib\org.eclipse.osgi_3.3.0.v20061101.jar"
rem Entire classpath together
set CP="%RUNTIME%;.;%CORE%;%EXAMPLES%"

set MAINCLASS=org.eclipse.ecf.provider.app.ServerApplication
set ARGS=-c ..\conf\server.xml %*

rem Start server
echo "Starting server"
echo "Main class: %MAINCLASS%"
echo "Classpath: %CP%"
echo "Args: %ARGS%"
rem start main class with classpath and args
java -cp %CP% %MAINCLASS% %ARGS% 
endlocal
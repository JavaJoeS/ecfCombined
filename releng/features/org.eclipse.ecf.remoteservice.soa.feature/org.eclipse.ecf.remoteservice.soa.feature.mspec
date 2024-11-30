<?xml version="1.0" encoding="UTF-8"?>
<md:mspec xmlns:md="http://www.eclipse.org/buckminster/MetaData-1.0" 
    name="org.eclipse.ecf" 
    materializer="p2" 
    url="org.eclipse.ecf.remoteservice.soa.feature.cquery">
    
    <md:mspecNode namePattern="^ch\.ethz\.iks(\..+)?" materializer="workspace"/>
    <md:mspecNode namePattern="^org\.eclipse\.team\.ecf(\..+)?" materializer="workspace"/>
    <md:mspecNode namePattern="^org\.eclipse\.ecf(\..+)?" materializer="workspace"/>
    <md:mspecNode namePattern="^org\.jivesoftware\.smack$" materializer="workspace"/>
    <md:mspecNode namePattern="^org\.json$" materializer="workspace"/>
    
    <md:mspecNode namePattern=".*" installLocation="${targetPlatformPath}"/>
</md:mspec>
	

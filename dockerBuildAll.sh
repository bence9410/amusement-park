cd amusement-park; sh mvnBuildDockerImage.sh; cd ..;
cd database/performance-test; sh dockerBuild.sh; cd ..;
cd normal; sh dockerBuild.sh;
docker pull dpage/pgadmin4; 

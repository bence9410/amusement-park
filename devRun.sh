gnome-terminal --maximize \
--tab -e "bash -c 'cd amusement-park; mvn clean package -DskipTests; java -Dserver.port=5000 -Damusement-park.ai=false -jar target/*.jar;'" \
--tab -e "bash -c 'cd amusement-park-vue-frontend; npm run serve;'";
cd nginx-proxy; sh applyConfigAndRestart.sh;

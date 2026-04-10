cd amusement-park-frontend; npm run build; cd ..;
rm -r -f amusement-park-backend/src/main/resources/public/*;
mkdir amusement-park-backend/src/main/resources/public/
cp -r amusement-park-frontend/dist/* amusement-park-backend/src/main/resources/public/;
cd amusement-park-backend; mvn clean package -DskipTests;
cd amusement-park-vue-frontend; npm run build; cd ..;
rm -r -f amusement-park/src/main/resources/public/*;
mkdir amusement-park/src/main/resources/public/
cp -r amusement-park-vue-frontend/dist/* amusement-park/src/main/resources/public/;

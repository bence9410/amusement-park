sudo rm -f /etc/nginx/sites-enabled/reverse-proxy.conf;
sudo cp config.conf /etc/nginx/sites-enabled/reverse-proxy.conf;
service nginx restart;

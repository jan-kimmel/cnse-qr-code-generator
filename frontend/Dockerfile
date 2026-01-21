FROM nginx:alpine

COPY public/ /usr/share/nginx/html/

CMD ["/bin/sh", "-c", "envsubst '${BACKEND_URL}' < /usr/share/nginx/html/js/config.js.template > /usr/share/nginx/html/js/config.js && nginx -g 'daemon off;'"]

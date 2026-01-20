FROM nginx:alpine

COPY public/ /usr/share/nginx/html/

CMD ["/bin/sh", "-c", "envsubst '${BACKEND_URL}' < /usr/share/nginx/html/js/config.js.template > /usr/share/nginx/html/js/config.js && nginx -g 'daemon off;'"]

# docker build -t gcr.io/cnse-qr-code-generator/frontend:latest ./frontend

# docker tag frontend:latest gcr.io/cnse-qr-code-generator/frontend:latest
# docker push gcr.io/cnse-qr-code-generator/frontend:latest

# gcloud run deploy frontend-service --image gcr.io/cnse-qr-code-generator/frontend:latest --platform managed --region europe-west1 --allow-unauthenticated --port=80 --set-env-vars BACKEND_URL=https://backend-service-162846799968.europe-west1.run.app
# gcloud run services delete frontend-service --region europe-west1

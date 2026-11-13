FROM node:22-alpine AS build
WORKDIR /workspace

COPY . .
RUN npm ci --include=dev && npm run build --workspace @walletx/auth-mfe

FROM nginx:1.27-alpine
WORKDIR /usr/share/nginx/html

COPY --from=build /workspace/apps/auth-mfe/dist/ ./
COPY docker/nginx.conf /etc/nginx/conf.d/default.conf
COPY docker/runtime-config.sh /docker-entrypoint.d/90-walletx-runtime-config.sh
RUN chmod +x /docker-entrypoint.d/90-walletx-runtime-config.sh

ENV VITE_API_BASE_URL=http://localhost:8080/api

EXPOSE 80
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=5 \
  CMD wget -q --spider http://localhost/healthz || exit 1

CMD ["nginx", "-g", "daemon off;"]

FROM node:16-alpine

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

COPY . /usr/src/app
RUN yarn install
RUN yarn generate

ENV NODE_ENV=production

CMD [ "yarn", "start" ]

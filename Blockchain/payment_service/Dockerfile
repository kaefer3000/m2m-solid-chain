FROM node:17-alpine3.12

# set working directory
WORKDIR /app

# install npm dependencies
COPY package.json ./
COPY package-lock.json ./
RUN npm install --silent

# add app
COPY . ./

# expose ports
EXPOSE 4000

# start app
CMD ["npm", "start"]



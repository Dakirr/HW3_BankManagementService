# Use an official Nginx image as a parent image
FROM nginx:alpine

# Set the working directory in the container
WORKDIR /usr/share/nginx/html

# Remove default Nginx welcome page
RUN rm -rf ./*

# Copy the frontend service files to the Nginx HTML directory
COPY ./ ./

# Expose port 80
EXPOSE 80

# Nginx will start automatically when the container launches

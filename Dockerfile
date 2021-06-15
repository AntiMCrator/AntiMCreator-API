FROM adoptopenjdk:8-jdk-hotspot
EXPOSE 8000:8000
RUN mkdir /app
COPY ./build/install/AntiMCreatorAPI/ /app/
WORKDIR /app/bin
CMD ["./docker"]
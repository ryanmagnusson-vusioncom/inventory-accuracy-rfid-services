FROM azul/zulu-openjdk-alpine:20-latest

ARG FONTS_HOME="/usr/share/fonts"
ARG CONSOLAS_FONT_VERSION="1.12"
ARG CONSOLAS_FONT_FILE_NAME="YaHei.Consolas.$CONSOLAS_FONT_VERSION"
ARG CONSOLAS_FONT_FILE="$CONSOLAS_FONT_FILE_NAME.ttf"
ARG CONSOLAS_FONT_ARCHIVE="$CONSOLAS_FONT_FILE_NAME.zip"
ARG CONSOLAS_FONT_FOLDER="$FONTS_HOME/consolas"
ARG CONSOLAS_FONT_URL="https://storage.googleapis.com/google-code-archive-downloads/v2/code.google.com/uigroupcode/$CONSOLAS_FONT_ARCHIVE"
#ARG MVN_TARGET_FILE_PATH="${MVN_TARGET_FOLDER}/${MVN_TARGET_FILENAME}"

RUN apk --no-cache add fontconfig ttf-dejavu coreutils bash \
     && wget -O "/tmp/$CONSOLAS_FONT_FILE_NAME.zip" "$CONSOLAS_FONT_URL" \
     && unzip "/tmp/$CONSOLAS_FONT_FILE_NAME.zip" \
     && mkdir -p "$FONTS_HOME/consolas" \
     && mv "$CONSOLAS_FONT_FILE_NAME.ttf" "$FONTS_HOME/consolas" \
     && chmod 644 "$FONTS_HOME/consolas/$CONSOLAS_FONT_FILE_NAME.ttf" \
     && cd "$FONTS_HOME/consolas" \
     && mkfontscale && mkfontdir && fc-cache -fv

ARG MVN_TARGET_FOLDER=""
ARG MVN_TARGET_FILENAME="$JAR_FILENAME"


ENV JAR_FILENAME="vusion-rfid-services.jar"
ENV JAR_FILE_PATH="$APP_HOME/$JAR_FILENAME"
ENV INSTANCE_ID=""
ENV SPRING_ACIVE_PROFILES="prod"
ENV APP_HOME="/application"
ENV SPRING_CONFIG_FILES="$APP_HOME/config"
ENV SPRING_ARGS="--spring.profiles.active=$SPRING_PROFILES -Dspring.config.location=$SPRING_CONFIG_FILES"
ENV JAVA_OPTIONS="-XX:MaxRAMPercentage=80.0 -XX:+UseG1GC -XX:+UseContainerSupport \
                  -XX:-OmitStackTraceInFastThrow \
                  -Djava.security.egd=file:/dev/./urandom \
                  --add-opens java.base/java.time=ALL-UNNAMED \
                  -XX:TieredStopAtLevel=1 \
                  -Dspring.output.ansi.enabled=always \
                  -Dcom.sun.management.jmxremote \
                  -Dspring.jmx.enabled=true \
                  -Dspring.liveBeansView.mbeanDomain \
                  -Dspring.application.admin.enabled=true \
                  -Dmanagement.endpoints.jmx.exposure.include=* \
                  -Dfile.encoding=UTF-8 \
                  -Dsun.stdout.encoding=UTF-8 \
                  -Dsun.stderr.encoding=UTF-8"

RUN mkdir -p $APP_HOME && mkdir -p $SPRING_CONFIG_FILES
ADD target/vusion-rfid-services.jar ${APP_HOME}/app.jar

#ADD "${MVN_TARGET_FILE_PATH}" "${JAR_FILE_PATH}"
#WORKDIR "$APP_HOME"

EXPOSE 8080
ENTRYPOINT java $JVM_OPTIONS -jar ${APP_HOME}/app.jar --spring.profiles.active=dev
#!/bin/bash

# 로그 파일이 저장된 디렉토리
LOG_DIR="/logs/log_back"
ERROR_LOG_DIR="/logs/error"
ARCHIVE_DIR="/logs/log_back"
CATALINA_LOG_DIR="/logs/tomcat"

# 보관주기 (일)
RETENTION_DAYS=89

# 압축 및 보관할 일반 로그 파일 목록
LOG_FILES=(
    "producer.log"
    "consumer.log"
    "apiClient.log"
    "apim.log"
)

# 압축 및 보관할 에러 로그 파일 목록
ERROR_LOG_FILES=(
    "producer_error.log"
    "consumer_error.log"
    "apiClient_error.log"
    "apim_error.log"
)

# tomcat log 파일 목록
CATALINA_LOG_FILES=(
	"access.log",
	"catalina.log"
)


# 로그 파일을 tar.gz로 압축
for LOG_FILE in "${LOG_FILES[@]}"; do
    find $LOG_DIR -type f -name "${LOG_FILE%.*}*.log" | while read FILE; do
        # tar.gz 파일 이름 생성 (로그 파일 이름과 날짜 포함)
        BASENAME=$(basename $FILE)
        PREFIX="${BASENAME%.*}"
        DATE="${BASENAME#*.}"
        DATE="${DATE%%_*}"
        TAR_FILE="$ARCHIVE_DIR/${PREFIX}.${DATE}.tar.gz"

        # tar.gz 파일로 압축
        tar -czf $TAR_FILE -C $LOG_DIR $(basename $FILE)

        # 압축이 성공하면 원본 .log 파일 삭제
        if [ $? -eq 0 ]; then
            rm $FILE
        fi
    done
done

# 에러 로그 파일을 tar.gz로 압축
for LOG_FILE in "${ERROR_LOG_FILES[@]}"; do
    find $ERROR_LOG_DIR -type f -name "${LOG_FILE%.*}*.log" | while read FILE; do
        # tar.gz 파일 이름 생성 (로그 파일 이름과 날짜 포함)
        BASENAME=$(basename $FILE)
        PREFIX="${BASENAME%.*}"
        DATE="${BASENAME#*.}"
        DATE="${DATE%%_*}"
        TAR_FILE="$ARCHIVE_DIR/${PREFIX}.${DATE}.tar.gz"

        # tar.gz 파일로 압축
        tar -czf $TAR_FILE -C $ERROR_LOG_DIR $(basename $FILE)

        # 압축이 성공하면 원본 .log 파일 삭제
        if [ $? -eq 0 ]; then
            rm $FILE
        fi
    done
done

# catalina 로그 파일을 tar.gz로 압축
for LOG_FILE in "${CATALINA_LOG_FILES[@]}"; do
    find $CATALINA_LOG_DIR -type f -name "${LOG_FILE%.*}*.log" | while read FILE; do
        # tar.gz 파일 이름 생성 (로그 파일 이름과 날짜 포함)
        BASENAME=$(basename $FILE)
        PREFIX="${BASENAME%.*}"
        DATE="${BASENAME#*.}"
        DATE="${DATE%%_*}"
        TAR_FILE="$ARCHIVE_DIR/${PREFIX}.${DATE}.tar.gz"

        # tar.gz 파일로 압축
        tar -czf $TAR_FILE -C $ERROR_LOG_DIR $(basename $FILE)

        # 압축이 성공하면 원본 .log 파일 삭제
        if [ $? -eq 0 ]; then
            rm $FILE
        fi
    done
done

# tar.gz 파일 보관 기간 설정 (예: 30일)
find $ARCHIVE_DIR -type f -name "*.log.tar.gz" -mtime +$RETENTION_DAYS -exec rm {} \;

echo "Log compression and cleanup completed."

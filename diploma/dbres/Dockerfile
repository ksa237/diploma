#описывается настройка образа для СУБД. дистрибутив полностью подходит и не требует настройки
#но воспользуемся иммено здесь начальной инициализацией БД создание основных таблиц и заполнение данными
FROM postgres:17
COPY ./1-schema.sql /docker-entrypoint-initdb.d/1-schema.sql
COPY ./2-data.sql /docker-entrypoint-initdb.d/2-data.sql

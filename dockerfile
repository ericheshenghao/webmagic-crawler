FROM java:8
MAINTAINER heshenghao 943452349@qq.com
# VOLUME 指定了临时文件目录为/tmp。
# 其效果是在主机 /var/lib/docker 目录下创建了一个临时文件，并链接到容器的/tmp
#VOLUME /tmp
# 将jar包添加到容器中并更名为app.jar
ADD ear-crawler-0.0.1-SNAPSHOT.jar app.jar
# 运行jar包
RUN bash -c 'touch /app.jar'
COPY  google-chrome.repo  /etc/yum.repos.d/google-chrome.repo
COPY google-chrome-stable_current_amd64.deb google-chrome-stable_current_amd64.deb
RUN mv /etc/apt/sources.list /etc/apt/sources.list.bak && \
echo 'deb http://mirrors.163.com/debian/ jessie main non-free contrib' > /etc/apt/sources.list && \
echo 'deb http://mirrors.163.com/debian/ jessie-updates main non-free contrib' >> /etc/apt/sources.list && \
echo 'deb http://mirrors.163.com/debian-security/ jessie/updates main non-free contrib' >> /etc/apt/sources.list
#
RUN apt-key adv --recv-keys --keyserver keyserver.ubuntu.com AA8E81B4331F7F50
RUN echo "deb [check-valid-until=no] http://archive.debian.org/debian jessie-backports main" > /etc/apt/sources.list.d/jessie-backports.list
RUN sed -i '/deb http:\/\/deb.debian.org\/debian jessie-updates main/d' /etc/apt/sources.list

RUN apt-get -o Acquire::Check-Valid-Until=false update
#RUN apt -f install
#RUN apt-get update
RUN apt-get -y install yum
RUN apt-get -f install
#RUN yum install -y redhat-lsb.x86_64
#RUN yum install -y libXScrnSaver
#RUN apt-get -y install lsb
#RUN yum -y install google-chrome-stable --nogpgcheck
#RUN rpm -ivh google-chrome-stable_current_x86_64_73.0.3683.75.rpm --force --nodeps
#RUN  yum install google-chrome-stable_current_x86_64_73.0.3683.75.rpm
RUN apt-get -y install fonts-liberation
RUN apt-get -y install  libappindicator3-1
RUN apt-get -y install  libgbm1
RUN apt-get -y install  libxss1
RUN apt-get -y install  xdg-utils
RUN dpkg -i google-chrome-stable_current_amd64.deb

RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo "Asia/Shanghai" > /etc/timezone
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

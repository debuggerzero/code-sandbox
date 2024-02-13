FROM centos:7
LABEL authors="ZERO"
MAINTAINER zero

# 设置工作路径
ENV MYPATH /usr
WORKDIR $MYPATH

# 设置端口
EXPOSE 8082

# 安装沙箱编译所需环境
RUN yum install -y java-1.8.0-openjdk* gcc gcc-c++ kernel-devel

# 复制文件到对应目录
COPY judge.c code-sandbox/judge/
COPY code-sandbox.jar code-sandbox/

# 编译 .c 文件并启动服务
CMD ["sh", "-c", "g++ /usr/code-sandbox/judge/judge.c -o /usr/code-sandbox/judge/handler && java -jar /usr/code-sandbox/code-sandbox.jar --server.port=8082 --judge.scriptPath=/usr/code-sandbox/judge/handler"]

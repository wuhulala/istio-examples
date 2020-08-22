# IDEA SpringBoot 远程打包Docker镜像



## 运行环境

远程环境 CentOS Linux 7
本地环境 Windows 10
Docker版本 18.06.3-ce

## 开启docker远程连接

修改配置

```shell
vi /usr/lib/systemd/system/docker.service
```

在ExecStart后面 添加一行

```bash
-H tcp://0.0.0.0:2375  -H unix:///var/run/docker.sock 
```

重启服务

```shell
systemctl daemon-reload && systemctl restart docker
```

端口查看

```shell
netstat -nptl | grep 2375
```

curl看是否生效

```shell
curl http://127.0.0.1:2375/info
```



## 修改pom.xml配置

```pom
<plugin>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-maven-plugin</artifactId>
	<configuration>
		<layers>
			<enabled>true</enabled>
		</layers>
		<includeSystemScope>true</includeSystemScope>
		<mainClass>com.wuhulala.istio.springboot.IstioSpringBootDemoApplication</mainClass>
	</configuration>
</plugin>

```

`<layers>
 			<enabled>true</enabled>
 		</layers>`

## 创建Dockerfile

```dockerfile
FROM adoptopenjdk:11-jre-hotspot as builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM adoptopenjdk:11-jre-hotspot
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./

RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo "Asia/Shanghai" > /etc/timezone

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
```

```yaml
spring:
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: Asia/Shanghai
```



将Dockerfile放到项目根目录下

![image-20200822173902376](https://wuhulala.oss-cn-shanghai.aliyuncs.com/blog/image-20200822173902376.png)



## IDEA构建

![image-20200822172228367](https://wuhulala.oss-cn-shanghai.aliyuncs.com/blog/image-20200822172228367.png)

![image-20200822173827624](https://wuhulala.oss-cn-shanghai.aliyuncs.com/blog/image-20200822173827624.png)
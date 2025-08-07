FROM openjdk:8-jdk

WORKDIR /root

RUN apt-get update && apt-get install -y openssh-server curl

ENV HADOOP_VERSION=3.3.6
RUN curl -fSL https://dlcdn.apache.org/hadoop/common/hadoop-$HADOOP_VERSION/hadoop-$HADOOP_VERSION.tar.gz -o /hadoop.tar.gz && \
	tar -xvf /hadoop.tar.gz -C / && \
	mv /hadoop-$HADOOP_VERSION /hadoop && \
	rm /hadoop.tar.gz

ENV HADOOP_HOME=/hadoop
ENV PATH=$PATH:/hadoop/bin:/hadoop/sbin

RUN ssh-keygen -t rsa -f ~/.ssh/id_rsa -P '' && \
    cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys

RUN mkdir -p /hadoop/dfs/name && \
	mkdir -p /hadoop/dfs/data && \
	mkdir -p /hadoop/dfs/journal

COPY config/* /tmp/
RUN mv /tmp/hadoop-env.sh /hadoop/etc/hadoop/hadoop-env.sh && \
	mv /tmp/core-site.xml /hadoop/etc/hadoop/core-site.xml && \
	mv /tmp/hdfs-site.xml /hadoop/etc/hadoop/hdfs-site.xml && \
	mv /tmp/workers /hadoop/etc/hadoop/workers

ADD scripts/init-journalnode.sh /init-journalnode.sh
ADD scripts/init-namenode1.sh /init-namenode1.sh
ADD scripts/init-namenode2.sh /init-namenode2.sh
ADD scripts/init-datanode.sh /init-datanode.sh

RUN chmod +x /init-journalnode.sh && \
	chmod +x /init-namenode1.sh && \
	chmod +x /init-namenode2.sh && \
	chmod +x /init-datanode.sh && \
	chmod +x /hadoop/sbin/start-dfs.sh

CMD ["bash"]


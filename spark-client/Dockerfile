FROM openjdk:8-jdk

ENV SPARK_VERSION=3.5.1
ENV HADOOP_VERSION=3
ENV HADOOP_HOME=/hadoop

# Install required tools
RUN apt-get update && apt-get install -y curl

# Install Spark prebuilt with Hadoop
RUN curl -fSL https://archive.apache.org/dist/spark/spark-${SPARK_VERSION}/spark-${SPARK_VERSION}-bin-hadoop${HADOOP_VERSION}.tgz | tar -xz -C /opt/ && \
    ln -s /opt/spark-${SPARK_VERSION}-bin-hadoop${HADOOP_VERSION} /opt/spark

ENV SPARK_HOME=/opt/spark
ENV PATH=$PATH:$SPARK_HOME/bin:$SPARK_HOME/sbin

COPY config/ /opt/spark/conf/

WORKDIR /opt/spark

CMD ["bash"]

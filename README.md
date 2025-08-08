# پروژه HDFS Cluster با Docker Compose

<div dir="rtl" style="text-align: right; font-family: 'Tahoma', 'Arial', sans-serif;">

## معرفی پروژه

این پروژه شامل یک خوشه HDFS (Hadoop Distributed File System) با قابلیت High Availability است که با استفاده از Docker Compose پیاده‌سازی شده است. این خوشه شامل دو NameNode، دو DataNode، یک JournalNode و یک Zookeeper برای مدیریت failover است.

## معماری سیستم
</div>

- **Zookeeper**: برای مدیریت failover و انتخاب NameNode فعال 
- **JournalNode**: برای نگهداری metadata تغییرات
- **NameNode1**: NameNode اصلی (Active)
- **NameNode2**: NameNode پشتیبان (Standby)
- **DataNode1 & DataNode2**: گره‌های ذخیره‌سازی داده

<div dir="rtl" style="text-align: right; font-family: 'Tahoma', 'Arial', sans-serif;">

## پیش‌نیازها
</div>


- Docker
- Docker Compose

<div dir="rtl" style="text-align: right; font-family: 'Tahoma', 'Arial', sans-serif;">

## مراحل راه‌اندازی

### ۱. راه‌اندازی خوشه HDFS

برای شروع خوشه HDFS، دستور زیر را اجرا کنید:
</div>

```bash
docker compose up -d
```

<div dir="rtl" style="text-align: right; font-family: 'Tahoma', 'Arial', sans-serif;">

**مراحل انجام شده:**
1. ابتدا Zookeeper راه‌اندازی می‌شود
2. سپس JournalNode برای نگهداری metadata شروع می‌شود
3. پس از آن NameNode1 به عنوان NameNode اصلی راه‌اندازی می‌شود
4. سپس NameNode2 به عنوان NameNode پشتیبان راه‌اندازی می‌شود
5. در آخر دو DataNode برای ذخیره‌سازی داده‌ها راه‌اندازی می‌شوند

### ۲. بررسی وضعیت سرویس‌ها

برای مشاهده وضعیت تمام کانتینرها:
</div>

```bash
docker compose ps
```

<div dir="rtl" style="text-align: right; font-family: 'Tahoma', 'Arial', sans-serif;">

برای مشاهده لاگ‌های سرویس‌ها:
</div>

```bash
docker-compose logs -f
```

<div dir="rtl" style="text-align: right; font-family: 'Tahoma', 'Arial', sans-serif;">

### ۳. دسترسی به رابط کاربری HDFS
</div>


- **NameNode1 (Active)**: http://localhost:9870
- **NameNode2 (Standby)**: http://localhost:9871

<div dir="rtl" style="text-align: right; font-family: 'Tahoma', 'Arial', sans-serif;">

### ۴. بارگذاری فایل‌ها به HDFS

برای بارگذاری فایل‌ها به HDFS، ابتدا وارد یکی از کانتینرهای NameNode شوید:
</div>

```bash
docker exec -it namenode1 bash
```

<div dir="rtl" style="text-align: right; font-family: 'Tahoma', 'Arial', sans-serif;">

سپس فایل‌های مورد نظر را بارگذاری کنید:

</div>

<div dir="ltr" style="text-align: left; font-family: 'Tahoma', 'Arial', sans-serif;">

```bash
hdfs dfs -mkdir -p hdfs://hdfs-cluster/data # ایجاد دایرکتوری در HDFS

hdfs dfs -put /path/to/local/file hdfs://hdfs-cluster/data/input.txt # بارگذاری فایل محلی به HDFS

hdfs dfs -ls hdfs://hdfs-cluster/data # مشاهده محتویات HDFS

hdfs dfs -cat hdfs://hdfs-cluster/data/input.txt # مشاهده محتوای فایل
```
</div>

<div dir="rtl" style="text-align: right; font-family: 'Tahoma', 'Arial', sans-serif;">

با استفاده از اسکریپت hdfs_upload.sh، به راحتی می‌توان فایل‌های مورد نیاز تمرین را دانلود و به مسیر مورد نظر در HDFS منتقل کرد:
- دانلود و بارگذاری فایل taxi_zone_lookup.csv:
</div>

```bash
./hdfs_upload.sh https://d37ci6vzurychx.cloudfront.net/misc/taxi_zone_lookup.csv
```


<div dir="rtl" style="text-align: right; font-family: 'Tahoma', 'Arial', sans-serif;">

- دانلود و بارگذاری فایل yellow_tripdata_&lt;date&gt;.parquet:
</div>

```bash
./hdfs_upload.sh https://d37ci6vzurychx.cloudfront.net/trip-data/yellow_tripdata_2025-02.parquet # تاریخ را در صورت نیاز تغییر دهید
```

<div dir="rtl" style="text-align: right; font-family: 'Tahoma', 'Arial', sans-serif;">

### ۵. بارگذاری فایل‌های jar
ابتدا در spark-jobs جاب‌ها را تعریف کنید.
فایل‌های jar را بسازید و آن‌ها را در پوشه `spark-client/jars` قرار دهید:
```bash
cd spark-jobs
mvn clean package
cp q*/target/*.jar ../spark-client/jars
```

### ۶. اجرای فایل jar
با انتخاب `qn` دلخواه می‌توان خروجی سوال شماره `n` را مشاهده کرد.
```bash
docker exec -it spark-client ./bin/spark-submit /job-jars/q1-0.1.jar
```

### ۷. بررسیی خروجی
با بررسی پوشه 
`/output`
 در `http://localhost:9870` و یا اجرای دستور زیر:
```bash
docker exec namenode1 hdfs dfs -ls /output
```

## ساختار پروژه
</div>

```
CT_HW5/
├── docker-compose.yaml          # تنظیمات Docker Compose
└── hdfs-cluster/                # تنظیمات HDFS
    ├── config/                  # فایل‌های config
    ├── Dockerfile               # Docker image
    └── scripts/                 # اسکریپت‌های راه‌اندازی
```

<div dir="rtl" style="text-align: right; font-family: 'Tahoma', 'Arial', sans-serif;">

## نکات مهم

- تمام داده‌ها در volume های Docker ذخیره می‌شوند
- برای حذف کامل داده‌ها از دستور `docker-compose down -v` استفاده کنید
- پورت‌های 9870 و 9871 برای دسترسی به رابط کاربری HDFS استفاده می‌شوند
- پورت 2181 برای Zookeeper و پورت 8485 برای JournalNode استفاده می‌شود

</div>
![image](https://user-images.githubusercontent.com/54580404/143531796-fa3eb0b1-de1d-4d54-9714-ed4ad2e93a85.png)
其中业务系统写日志，写redis的行为已经展示在前面的代码中，而flume的监听主要依靠配置文件中的路径与tail -F命令，所以也省略，spark Streaming的计算也已经在前面的代码中讲完，所以这里主要展示kafka到spark Streaming的数据流处理代码。
Kafka的处理主要是有一个from到to的处理过程，即加工 from的数据，并传输给to，最终运用到spark Streaming中去做运算。
第一步需要配置好，输入topic是flume输入的日志信息，输出topic是处理过的recommendation信息。配置好ip和端口。
1.kafka，zookeeper
下载好kafka和zookeeper的安装包上传到服务器上，并解压，移动到local文件夹里，启动两个组件的服务，并在配置文件中配置好端口。Zookeeper为2181，kafka为9092.
启动服务

![image](https://user-images.githubusercontent.com/54580404/143531850-f2f60cd8-2c45-434d-87ad-4370463a2db3.png)
![image](https://user-images.githubusercontent.com/54580404/143531858-5e35ae14-a473-4a38-8cb2-a84db052caf9.png)
![image](https://user-images.githubusercontent.com/54580404/143531867-27b3b832-1346-4267-a40d-6619c162507f.png)
开发时即直接让kafka与zookeeper运行在服务器上，在运行时已经占据了该服务器70%的内存，清空消息也无效，所以部署其他的组件需要另开一台服务器。

2.MongoDB，Redis，MySQL
为了保证剩余的组件可以正常运行，所以开通一台4G内存的云服务器供部署。
![image](https://user-images.githubusercontent.com/54580404/143531883-50e66b63-5515-480f-9153-bc4e4c16a2e1.png)
部署MongoDB，Redis，MySQL等数据库服务，可以直接使用wget的方式进行下载，下载后解压，并移动到local文件夹中，把本地的数据上传到MongoDB和MySQL中，并分别开启他们的服务。
部署MongoDB，Redis，MySQL等数据库服务，可以直接使用wget的方式进行下载，下载后解压，并移动到local文件夹中，把本地的数据上传到MongoDB和MySQL中，并分别开启他们的服务。
测试mongodb远程连接，并上传数据，使用mongodb Compass
![image](https://user-images.githubusercontent.com/54580404/143531901-b36e6b63-aaa2-4c49-ac4a-8524ed1e5ba4.png)
启动redis
![image](https://user-images.githubusercontent.com/54580404/143531909-84566f68-a612-4c6d-bcd1-7b0ce390c794.png)
Redis的远程连接需要修改配置文件，把bind 127.0.0.1注释掉。

启动MySQL
部署流处理程序与实时推荐，内容推荐等
每一个都是单独的程序，所以分别生成jar包并上传。
使用build artifact的方式，然后用java -jar命令运行，设置为后台运行即可。

部署Springboot项目
![image](https://user-images.githubusercontent.com/54580404/143531960-cb278fcf-468c-4395-b9ae-6bc0bacd2144.png)
![image](https://user-images.githubusercontent.com/54580404/143531965-a1801d6d-3008-442c-9876-bc7dd83120b3.png)
![image](https://user-images.githubusercontent.com/54580404/143531975-db3864e7-54c8-477f-b488-1de5204a2d8c.png)

四．测试报告
4.1用户功能测试
4.1.1 注册
![image](https://user-images.githubusercontent.com/54580404/143531992-abdce7dd-7b84-474a-9814-58f344487010.png)
![image](https://user-images.githubusercontent.com/54580404/143532007-2821efdd-50aa-4e9b-90b8-9c6ee65bbda5.png)
![image](https://user-images.githubusercontent.com/54580404/143532009-ef2fa56e-fecd-4894-a72a-342b641f5e4a.png)
![image](https://user-images.githubusercontent.com/54580404/143532018-30cbd4de-a1d6-4d2b-a67e-c8122c9d3f6c.png)
![image](https://user-images.githubusercontent.com/54580404/143532019-14852d8f-f4eb-41a7-ab99-27330c9ab385.png)
![image](https://user-images.githubusercontent.com/54580404/143532029-8d6539fe-334b-4511-96df-c072b7a68b02.png)
![image](https://user-images.githubusercontent.com/54580404/143532035-49d2a456-7e89-4c41-ba5e-2dd455ba57cc.png)
![image](https://user-images.githubusercontent.com/54580404/143532039-b45eb6d9-5d12-402b-9253-7cfa60b0b084.png)
![image](https://user-images.githubusercontent.com/54580404/143532046-73f9c201-04df-4139-81fa-da5c334f0ab2.png)
![image](https://user-images.githubusercontent.com/54580404/143532056-94480d26-0cb8-4686-b2ad-3699506d4715.png)
![image](https://user-images.githubusercontent.com/54580404/143532060-7d795718-a598-4dd6-a257-b5b75c8dd5fe.png)
![image](https://user-images.githubusercontent.com/54580404/143532069-48e45886-06ba-478d-a2be-8aa59680c586.png)

销售状态信息的监控
![image](https://user-images.githubusercontent.com/54580404/143532077-b5680bf6-899a-4679-b2bf-9955267abf9a.png)

用户购买和浏览的日志记录![image](https://user-images.githubusercontent.com/54580404/143532093-94c865df-1ba2-41f4-af84-c0178d1bc922.png)
![image](https://user-images.githubusercontent.com/54580404/143532097-21c91f93-02d2-42e3-8075-9c1d843995eb.png)
![image](https://user-images.githubusercontent.com/54580404/143532104-6fb7076f-8998-4fea-9008-033b21abbe71.png)
管理员测试
![image](https://user-images.githubusercontent.com/54580404/143532121-2b10dcf9-e70c-4435-89e3-7456acbf6f5b.png)
![image](https://user-images.githubusercontent.com/54580404/143532130-3f48ad15-bcc2-4bde-a5fc-4ff954709906.png)
![image](https://user-images.githubusercontent.com/54580404/143532134-1584c9e1-fddc-4cfe-8cc2-545e7390bf60.png)
![image](https://user-images.githubusercontent.com/54580404/143532141-de526e0f-243d-44d4-9f9c-36e7531f3007.png)
![image](https://user-images.githubusercontent.com/54580404/143532149-13b83fdf-6818-4885-9472-ce291c023bee.png)


大数据推荐![image](https://user-images.githubusercontent.com/54580404/143532161-bd406226-0c2a-4a6f-8d45-39467cb6abdc.png)
![image](https://user-images.githubusercontent.com/54580404/143532168-e54029b8-5449-468b-bd2e-d67ec3244500.png)
基于用户行为的推荐![image](https://user-images.githubusercontent.com/54580404/143532178-4d4428e6-60cd-42e6-ae0a-9acc7981e419.png)
![image](https://user-images.githubusercontent.com/54580404/143532186-c12800d0-7ed4-49f9-8a8d-2df316be8fd1.png)
![image](https://user-images.githubusercontent.com/54580404/143532190-36f3d0be-9a37-434c-b2f9-4e1d8571acbe.png)
![image](https://user-images.githubusercontent.com/54580404/143532193-19765def-4343-43a2-9172-c9c24f4382f3.png)
基于用户行为与商品内容的实时推荐
![image](https://user-images.githubusercontent.com/54580404/143532200-f75ad049-d33c-4ab0-a4f8-8747282162f4.png)
![image](https://user-images.githubusercontent.com/54580404/143532236-db573fa8-74f4-40b4-aa90-85df57291240.png)
![image](https://user-images.githubusercontent.com/54580404/143532240-3e4357d6-b733-4172-9094-74ae0ec2eed6.png)
![image](https://user-images.githubusercontent.com/54580404/143532246-fada9c67-34db-4704-8832-657c7105c579.png)
![image](https://user-images.githubusercontent.com/54580404/143532248-f3a27192-d94e-4f06-b130-cc2420b5e136.png)
![image](https://user-images.githubusercontent.com/54580404/143532257-4168f80b-0747-4d4c-bbb2-9dbd70d32e73.png)

package lu.my.mall.util;

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

    @Configuration
    public class configure {

        private String mongoHost;
        private int mongoPort;
        private String esClusterName;
        private String esHost;
        private int esPort;
        private String redisHost;

        public configure() {
            try {
                //Properties properties = new Properties();
                //Resource resource = new ClassPathResource("recommend.properties");
               // properties.load(new FileInputStream(resource.getFile()));
                this.mongoHost = "localhost";
                this.mongoPort = 27017;
                this.redisHost = "localhost";
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        }

        @Bean(name = "mongoClient")
        public MongoClient getMongoClient() {
            MongoClient mongoClient = new MongoClient(mongoHost, mongoPort);
            return mongoClient;
        }

        @Bean(name = "jedis")
        public Jedis getRedisClient() {
            Jedis jedis = new Jedis(redisHost);
            return jedis;
        }
    }


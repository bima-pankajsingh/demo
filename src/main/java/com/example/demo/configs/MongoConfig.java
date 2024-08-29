package com.example.demo.configs;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.bol.crypt.CryptVault;
import com.bol.secure.CachedEncryptionEventListener;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.WriteConcern;

@Configuration
@EnableMongoRepositories(basePackages = {"com.example.*"})
@EnableMongoAuditing
public class MongoConfig extends AbstractMongoConfiguration {

    @Value("${mongo.database}")
    private String databaseName;

    @Value("${mongo.username}")
    private String username;

    @Value("${mongo.password}")
    private String password;

    @Value("${mongo.host}")
    private String host;

    @Value("${mongo.port}")
    private int port;

    @Value("${mongo.secretKey}")
    private String secretKey;

    @Value("${mongo.oldKey}")
    private String oldKey;

    @Override
    @SuppressWarnings("NullableProblems")
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public MongoClient mongoClient() {
        String uri = "mongodb://" + this.username + ":" + this.password + "@" + this.host + ":" + this.port;
      //  String uri = "mongodb://" + this.username + ":" + this.password + "@" + this.host + ":" + this.port+"/healthcare-bd";
        return new MongoClient(new MongoClientURI(uri));
    }

    @Bean
    MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory, MongoConverter converter) throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory, converter);
        mongoTemplate.setWriteConcern(WriteConcern.MAJORITY);
        mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);
        return mongoTemplate;
    }
    
    

    @Bean
    public CryptVault cryptVault() {
        byte[] secretKey = Base64.getDecoder().decode(this.secretKey);
        byte[] oldKey = Base64.getDecoder().decode(this.secretKey);
        return new CryptVault()
                .with256BitAesCbcPkcs5PaddingAnd128BitSaltKey(0, oldKey)
                .with256BitAesCbcPkcs5PaddingAnd128BitSaltKey(1, secretKey)
                // can be omitted if it's the highest version
                .withDefaultKeyVersion(1);
    }

    @Bean
    public CachedEncryptionEventListener encryptionEventListener(CryptVault cryptVault) {
        return new CachedEncryptionEventListener(cryptVault);
    }

    @Override
    @Bean
    public MappingMongoConverter mappingMongoConverter() throws Exception {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory());
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext());
        converter.setCustomConversions(mongoCustomConversions());
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));

        return converter;
    }
    
    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {

        return new Jackson2ObjectMapperBuilder() {

            @Override
            public void configure(ObjectMapper objectMapper) {
                super.configure(objectMapper);
                objectMapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
                objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
            }
        };
    }
    
    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        List<Object> converters = new ArrayList<>();
        converters.add(new LocalDateTimeConverters.StringToLocalDateTimeConverter());
        converters.add(new LocalDateTimeConverters.LocalDateTimeToStringConverter());
        return new MongoCustomConversions(converters);
    }
}

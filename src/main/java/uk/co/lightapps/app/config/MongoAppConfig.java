//package uk.co.lightapps.app.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.data.mongodb.MongoDbFactory;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.convert.*;
//import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
//import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author Asif Akhtar
// * 25/10/2020 20:48
// */
//@Configuration
//@EnableMongoRepositories({"uk.co.lightapps.app"})
//public class MongoAppConfig {
////    @Bean
////    public MongoCustomConversions customConversions() {
////        List<Converter<?, ?>> converters = new ArrayList<>();
////        converters.add(new SimpleDateStringConverter());
////        converters.add(new StringBasicDateConverter());
////        return new CustomConversions(converters);
////    }
//
//    @Bean
//    public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory, MappingMongoConverter mongoConverter) throws Exception {
//        return new MongoTemplate(mongoDbFactory, mongoConverter);
//    }
//
//    @Bean
//    public MappingMongoConverter mongoConverter(MongoDbFactory mongoDbFactory) throws Exception {
//        MongoMappingContext mappingContext = new MongoMappingContext();
//        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
//        MappingMongoConverter mongoConverter = new MappingMongoConverter(dbRefResolver, mappingContext);
//        mongoConverter.setCustomConversions(customConversions());
//        return mongoConverter;
//    }
//}

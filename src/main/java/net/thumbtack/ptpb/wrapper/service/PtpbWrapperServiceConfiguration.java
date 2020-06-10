package net.thumbtack.ptpb.wrapper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.ptpb.wrapper.SynchronizationService;
import net.thumbtack.ptpb.wrapper.client.syncdata.TodoistClientService;
import net.thumbtack.ptpb.wrapper.db.item.ItemDao;
import net.thumbtack.ptpb.wrapper.db.project.ProjectDao;
import net.thumbtack.ptpb.wrapper.db.project.ProjectMapper;
import net.thumbtack.ptpb.wrapper.db.sync.SyncDao;
import net.thumbtack.ptpb.wrapper.db.user.UserDao;
import net.thumbtack.ptpb.wrapper.service.synchronization.RabbitMqMessageProvider;
import net.thumbtack.ptpb.wrapper.service.synchronization.requests.SyncUserAmqpRequest;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@EnableRabbit
@Configuration
public class PtpbWrapperServiceConfiguration {

//    @Bean
//    public SynchronizationService getSynchronizationService(ObjectMapper mapper, UserDao userDao, ProjectDao projectDao,
//                                                            ProjectMapper projectMapper, ItemDao itemDao,
//                                                            SyncDao syncDao, TodoistClientService todoistClientService, RabbitMqMessageProvider rabbitMqMessageProvider) {
//        SynchronizationService service = new SynchronizationService(mapper, userDao, projectDao, itemDao, syncDao, todoistClientService);
//        rabbitMqMessageProvider.registerHandler(SyncUserAmqpRequest.class.getSimpleName(), service::syncUser);
//        return service;
//    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public Queue queue() {
        return new Queue("ptpb-wrapper");
    }
//
//    @Bean
//    public TopicExchange appExchange() {
//        return new TopicExchange("ptpb-wrapper");
//    }
//
//    @Bean
//    public Queue appQueueGeneric() {
//        return new Queue("ptpb-wrapper");
//    }
//
//    @Bean
//    public Queue appQueueSpecific() {
//        return new Queue("ptpb-wrapper-specific");
//    }
//
//    @Bean
//    public Binding declareBindingGeneric() {
//        return BindingBuilder.bind(appQueueGeneric()).to(appExchange()).with("ptpb");
//    }

//    @Bean
//    public Binding declareBindingSpecific() {
//        return BindingBuilder.bind(appQueueSpecific()).to(appExchange()).with("ptpb-specific");
//    }
}

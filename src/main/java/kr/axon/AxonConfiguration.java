package kr.axon;

import com.mongodb.Mongo;
import kr.axon.post.command.domain.PostAggregateRoot;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.axonframework.contextsupport.spring.AnnotationDriven;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.SimpleEventBus;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventstore.EventStore;
import org.axonframework.eventstore.mongo.DefaultMongoTemplate;
import org.axonframework.eventstore.mongo.MongoEventStore;
import org.axonframework.unitofwork.SpringTransactionManager;
import org.axonframework.unitofwork.TransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@AnnotationDriven
@SuppressWarnings("unused")
public class AxonConfiguration {

    @Autowired
    private Mongo mongo;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Bean
    public TransactionManager axonTransactionManager() {
        return new SpringTransactionManager(platformTransactionManager);
    }

    @Bean
    public EventStore eventStore() {
        return new MongoEventStore(new DefaultMongoTemplate(mongo));
    }

    @Bean
    public CommandBus commandBus() {
        SimpleCommandBus commandBus = new SimpleCommandBus();
        commandBus.setTransactionManager(axonTransactionManager());
        return commandBus;
    }

    @Bean
    public CommandGateway commandGateway() {
        return new DefaultCommandGateway(commandBus());
    }

    @Bean
    public EventBus eventBus() {
        return new SimpleEventBus();
    }

    @Bean
    public EventSourcingRepository eventSourcingRepository() {
        EventSourcingRepository eventSourcingRepository =
                new EventSourcingRepository(PostAggregateRoot.class, eventStore());
        eventSourcingRepository.setEventBus(eventBus());
        return eventSourcingRepository;
    }
}
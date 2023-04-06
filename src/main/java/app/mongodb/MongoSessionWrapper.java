package app.mongodb;

import com.mongodb.reactivestreams.client.ClientSession;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MongoSessionWrapper {
  @Inject
  ReactiveMongoClient client;

  public Uni<ClientSession> getSession(){
    return client.startSession().map(clientSession -> {
      clientSession.startTransaction();
      return clientSession;
    });
  }

  public void closeSession(ClientSession session){
    session.close();
  }
}
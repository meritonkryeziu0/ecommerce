package app.services.order;


import app.common.CustomValidator;
import app.exceptions.BaseException;
import app.helpers.PaginatedResponse;
import app.helpers.PaginationWrapper;
import app.mongodb.MongoUtils;
import app.services.order.models.CreateOrder;
import app.services.order.models.Order;
import app.services.order.models.UpdateOrder;
import app.shared.SuccessResponse;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.function.Function;

@ApplicationScoped
public class OrderService {
    @Inject
    CustomValidator validator;
    @Inject
    OrderRepository repository;

    public Uni<List<Order>> getList() {
        return repository.getList();
    }

    public Uni<PaginatedResponse<Order>> getList(PaginationWrapper wrapper) {
        return MongoUtils.getPaginatedItems(repository.getCollection(), wrapper);
    }

    public Uni<Order> getById(String id) {
        return repository.getById(id)
                .onItem().ifNull().failWith(new OrderException(OrderException.ORDER_NOT_FOUND, 404));
    }

    public Uni<Order> add(CreateOrder createOrder) {
        return validator.validate(createOrder)
                .replaceWith(OrderMapper.from(createOrder))
                .flatMap(order -> repository.add(order));
    }

    public Uni<SuccessResponse> delete(String id) {
        return repository.delete(id).replaceWith(SuccessResponse.toSuccessResponse());
    }

    public Uni<Order> update(String id, UpdateOrder updateOrder) {
        return validator.validate(updateOrder).replaceWith(this.getById(id))
                .onFailure().transform(transformToBadRequest());
    }

    private Function<Throwable, Throwable> transformToBadRequest() {
        return throwable -> {
            if (throwable instanceof BaseException) {
                return new OrderException(OrderException.ORDER_NOT_FOUND, 400);
            }
            return throwable;
        };
    }
}
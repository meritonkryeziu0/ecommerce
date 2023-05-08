package app.services.tracking;

import app.proto.OrderService;
import app.proto.UpdateTrackingStatusRequest;
import app.proto.UpdateTrackingStatusResponse;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;

@GrpcService
public class OrderServiceGrpcImpl implements OrderService {
  @Inject
  app.services.order.OrderService orderService;
  @Override
  public Uni<UpdateTrackingStatusResponse> updateTracking(UpdateTrackingStatusRequest request) {
    return orderService.updateStatusFromTracking(request.getTrackingNumber(), request.getStatus())
        .replaceWith(UpdateTrackingStatusResponse.newBuilder()
            .setTrackingNumber(request.getTrackingNumber())
            .setStatus(request.getStatus())
            .build());
  }
}
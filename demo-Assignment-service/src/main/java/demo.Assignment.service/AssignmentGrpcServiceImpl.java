package demo.Assignment.service;
import demo.interfaces.grpc.Assignment;
import demo.interfaces.grpc.AssignmentServiceGrpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class AssignmentGrpcServiceImpl extends AssignmentServiceGrpc.AssignmentServiceImplBase {


	@Override
	public void getAssignmentByGroup(Assignment request, StreamObserver<Assignment> responseObserver) {
		
		AssignmentResourceProvider.getAssignmentfromAssignmentSource().stream()
				.filter(alloc -> alloc.getGroupID() == request.getGroupID())
				.forEach(responseObserver::onNext);

		responseObserver.onCompleted();
	}	

}

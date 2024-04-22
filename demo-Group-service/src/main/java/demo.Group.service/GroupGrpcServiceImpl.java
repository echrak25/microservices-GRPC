package demo.Group.service;
import java.util.ArrayList;
import java.util.List;

import demo.interfaces.grpc.Group;
import demo.interfaces.grpc.GroupServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class GroupGrpcServiceImpl extends GroupServiceGrpc.GroupServiceImplBase {

	@Override
	public void getGroup(Group request, StreamObserver<Group> responseObserver) {
		responseObserver.onNext(GroupResourceProvider.getGroupListfromGroupSource()
													.stream()
													.filter(emp -> emp.getGroupID() == request.getGroupID())
													.findFirst()
													.get());
		responseObserver.onCompleted();
	}


	@Override
	public StreamObserver<Group> getAllGroupsByIDList(StreamObserver<Group> responseObserver) {
		return new StreamObserver<Group>() {
			
			List<Group> responseList = new ArrayList<Group>();
			
			@Override
			public void onNext(Group value) {
				responseList.add(GroupResourceProvider.getGroupListfromGroupSource().stream()
										.filter(emp -> emp.getGroupID() == value.getGroupID())
										.findFirst()
										.get());
			}
			
			@Override
			public void onError(Throwable t) {
				responseObserver.onError(t);				
			}
			
			@Override
			public void onCompleted() {
				responseList.stream().forEach(responseObserver::onNext);
				responseObserver.onCompleted();
			}
		};
	}


	@Override
	public StreamObserver<Group> getLargestGroup(StreamObserver<Group> responseObserver) {
		return new StreamObserver<Group>() {

			Group response = null;

			@Override
			public void onNext(Group value) {
				
				Group currentGroup = GroupResourceProvider.getGroupListfromGroupSource()
											.stream()
											.filter(emp -> emp.getGroupID() == value.getGroupID())
											.findFirst()
											.get();
																						
				if(response == null || currentGroup.getGroupNumberMembers() > response.getGroupNumberMembers()) {
					response = currentGroup;
				}
			}

			@Override
			public void onError(Throwable t) {
				responseObserver.onError(t);
			}

			@Override
			public void onCompleted() {
				responseObserver.onNext(response);
				responseObserver.onCompleted();
			}
		};
	}	
	
}

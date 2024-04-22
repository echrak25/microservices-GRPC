package demo.Assignment.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.protobuf.Descriptors.FieldDescriptor;

import demo.interfaces.grpc.Group;
import demo.interfaces.grpc.GroupServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;

@Service
public class AssignmentGrpcClientImpl {

	@GrpcClient("Group-service")
	private GroupServiceGrpc.GroupServiceBlockingStub GroupServiceBlockingStub;

	@GrpcClient("Group-service")
	private GroupServiceGrpc.GroupServiceStub GroupServiceStub;

	public Map<FieldDescriptor, Object> getGroupDetailsByAssignmentID(long AssignmentID) {

		return GroupServiceBlockingStub
					.getGroup(Group.newBuilder()
											.setGroupID(AssignmentResourceProvider
															.getAssignmentfromAssignmentSource()
															.stream()
															.filter(alloc -> alloc.getAssignmentID() == AssignmentID)
															.findFirst()
															.get()
															.getGroupID())
											.build())
					.getAllFields();
	}


	public List<Map<FieldDescriptor, Object>> getGroupFullDetails(long projectID) throws InterruptedException {

		final CountDownLatch finishLatch = new CountDownLatch(1);
		List<Map<FieldDescriptor, Object>> GroupDetailsFinalList = new ArrayList<Map<FieldDescriptor, Object>>();

		StreamObserver<Group> responseObserver = GroupServiceStub
				.getAllGroupsByIDList(new StreamObserver<Group>() {
					@Override
					public void onNext(Group value) {
						GroupDetailsFinalList.add(value.getAllFields());
					}

					@Override
					public void onError(Throwable t) {
						finishLatch.countDown();
					}

					@Override
					public void onCompleted() {
						finishLatch.countDown();
					}

				});

		AssignmentResourceProvider.getAssignmentfromAssignmentSource().stream()
				.filter(alloc -> alloc.getProjectID() == projectID).forEach(alloc -> {
					responseObserver.onNext(Group.newBuilder().setGroupID(alloc.getGroupID()).build());
				});

		responseObserver.onCompleted();

		finishLatch.await(1, TimeUnit.MINUTES);

		return GroupDetailsFinalList;
	}

	public Map<String, Map<FieldDescriptor, Object>> getLargestGroup(long projectID) throws InterruptedException {

		final CountDownLatch finishLatch = new CountDownLatch(1);
		Map<String, Map<FieldDescriptor, Object>> responce = new HashMap<String, Map<FieldDescriptor, Object>>();
		

		StreamObserver<Group> responseObserver = GroupServiceStub
				.getLargestGroup(new StreamObserver<Group>() {
								
					@Override
					public void onNext(Group value) {
						responce.put("Group", value.getAllFields());
					}

					@Override
					public void onError(Throwable t) {
						finishLatch.countDown();
					}

					@Override
					public void onCompleted() {
						finishLatch.countDown();
					}

				});

		AssignmentResourceProvider.getAssignmentfromAssignmentSource().stream()
				.filter(alloc -> alloc.getProjectID() == projectID).forEach(alloc -> {
					responseObserver.onNext(Group.newBuilder().setGroupID(alloc.getGroupID()).build());
				});

		responseObserver.onCompleted();

		finishLatch.await(1, TimeUnit.MINUTES);

		return responce;
	}

}

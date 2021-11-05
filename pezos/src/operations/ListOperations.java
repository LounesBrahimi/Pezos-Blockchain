package operations;

import java.util.ArrayList;
import java.util.Arrays;

/*
 * Class representant la liste des operationss
 * */
public class ListOperations {

	private ArrayList<Operation> operations;
	
	public ListOperations() {
		operations = new ArrayList<Operation>();
	}
	
	/*
	 * Methode permettant d'extraire toutes les operations sous format "d'un objet Operation" 
	 * et d'en constituer une liste 
	 * (methode principale d'extraction d'operations)
	 * */
	public void extractAllOperations(byte[] receivedOperation) {
		Operation op = new Operation();
		op.extractFirstOperation(receivedOperation);
		operations.add(op);
		if ((op.typeOfTag() == 1) || (op.typeOfTag() == 3) || (op.typeOfTag() == 4)) {
			receivedOperation = Arrays.copyOfRange(receivedOperation,130,receivedOperation.length);
			if (receivedOperation.length >= 98)
			extractAllOperationRec(receivedOperation);
		} else if (op.typeOfTag() == 2){
			receivedOperation = Arrays.copyOfRange(receivedOperation,106,receivedOperation.length);
			if (receivedOperation.length >= 98)
			extractAllOperationRec(receivedOperation);
		} else if (op.typeOfTag() == 5) {
			receivedOperation = Arrays.copyOfRange(receivedOperation,98,receivedOperation.length);
			if (receivedOperation.length >= 98)
			extractAllOperationRec(receivedOperation);
		}
	}
	
	/*
	 * Methode secondaire d'extraction d'operations appelee par la principale, cette derniere de façon
	 * recurcive extrait toute les operations en integralitee
	 * */
	private void extractAllOperationRec(byte[] receivedOperation) {
		Operation op = new Operation();
		op.extractOperation(receivedOperation);
		operations.add(op);
		if ((op.typeOfTag() == 1) || (op.typeOfTag() == 3) || (op.typeOfTag() == 4)) {
			if (receivedOperation.length > 130) {
				receivedOperation = Arrays.copyOfRange(receivedOperation,130,receivedOperation.length);
				extractAllOperationRec(receivedOperation);	
			}
		} else if (op.typeOfTag() == 2){
			if (receivedOperation.length > 130) {
				receivedOperation = Arrays.copyOfRange(receivedOperation,106,receivedOperation.length);
				extractAllOperationRec(receivedOperation);
			}
		} else if (op.typeOfTag() == 5) {
			if (receivedOperation.length > 130) {
				receivedOperation = Arrays.copyOfRange(receivedOperation,98,receivedOperation.length);
				extractAllOperationRec(receivedOperation);
			}
		}
	}
	
	public ArrayList<Operation> getListOperations(){
		return this.operations;
	}
}
